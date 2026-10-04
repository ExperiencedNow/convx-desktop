package com.convx.desktop.audio

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

data class StreamTarget(
    val url: String,
    val userAgent: String,
    var totalLength: Long = -1L,
    var contentType: String = "audio/webm"
)

/**
 * Local HTTP streaming proxy that bridges between desktop media players (LibVLC)
 * and GoogleVideo / YouTube CDN streams using OkHttpClient.
 *
 * GoogleVideo CDN enforces bounded HTTP Range requests (rejecting open-ended ranges
 * with 403 Forbidden). LocalStreamProxy transparently negotiates byte ranges with
 * LibVLC and fetches bounded 512KB chunks via OkHttp, mirroring Android's
 * OkHttpDataSource / ChunkDataSource architecture.
 */
class LocalStreamProxy(
    private val client: OkHttpClient = OkHttpClient.Builder().build()
) {
    private var server: HttpServer? = null
    private val streamTargets = ConcurrentHashMap<String, StreamTarget>()

    val port: Int
        get() = server?.address?.port ?: 0

    fun start() {
        if (server != null) return
        val s = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        s.executor = Executors.newCachedThreadPool()
        s.createContext("/stream/", StreamHandler(client, streamTargets))
        s.start()
        server = s
    }

    fun stop() {
        server?.stop(0)
        server = null
        streamTargets.clear()
    }

    fun registerStream(targetUrl: String, userAgent: String): String {
        val streamId = UUID.randomUUID().toString()
        streamTargets[streamId] = StreamTarget(targetUrl, userAgent)
        return "http://127.0.0.1:$port/stream/$streamId"
    }

    fun unregisterStream(streamId: String) {
        streamTargets.remove(streamId)
    }

    private class StreamHandler(
        private val client: OkHttpClient,
        private val streamTargets: ConcurrentHashMap<String, StreamTarget>
    ) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val path = exchange.requestURI.path ?: ""
            val streamId = path.removePrefix("/stream/").trim()

            val target = streamTargets[streamId]
            if (target == null) {
                println("[LocalStreamProxy] Stream ID not found: '$streamId'")
                exchange.sendResponseHeaders(404, -1)
                return
            }

            // 1. Probe total length and content type if not yet known
            if (target.totalLength <= 0) {
                val probeReq = Request.Builder()
                    .url(target.url)
                    .header("Range", "bytes=0-1")
                    .apply { if (target.userAgent.isNotEmpty()) header("User-Agent", target.userAgent) }
                    .build()
                try {
                    client.newCall(probeReq).execute().use { probeResp ->
                        if (probeResp.isSuccessful) {
                            val cr = probeResp.header("Content-Range")
                            val ct = probeResp.header("Content-Type")
                            if (ct != null) target.contentType = ct
                            val total = cr?.substringAfterLast('/')?.toLongOrNull()
                            if (total != null && total > 0) {
                                target.totalLength = total
                                println("[LocalStreamProxy] Probed stream: length=${target.totalLength}, type=${target.contentType}")
                            }
                        } else {
                            println("[LocalStreamProxy] Upstream probe failed with HTTP ${probeResp.code}")
                        }
                    }
                } catch (e: Exception) {
                    println("[LocalStreamProxy] Probe error: ${e.message}")
                }
            }

            if (target.totalLength <= 0) {
                exchange.sendResponseHeaders(502, -1)
                return
            }

            // 2. Parse client range request
            val rangeHeader = exchange.requestHeaders.getFirst("Range")
            var start = 0L
            var end = target.totalLength - 1
            var isPartial = false

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                isPartial = true
                val spec = rangeHeader.removePrefix("bytes=").trim()
                val parts = spec.split("-")
                if (parts[0].isNotEmpty()) {
                    start = parts[0].toLongOrNull() ?: 0L
                }
                if (parts.size > 1 && parts[1].isNotEmpty()) {
                    end = minOf(parts[1].toLongOrNull() ?: end, target.totalLength - 1)
                }
            }

            val contentLength = (end - start + 1).coerceAtLeast(0L)
            exchange.responseHeaders.set("Content-Type", target.contentType)
            exchange.responseHeaders.set("Accept-Ranges", "bytes")

            val statusCode = if (isPartial) {
                exchange.responseHeaders.set("Content-Range", "bytes $start-$end/${target.totalLength}")
                206
            } else {
                200
            }

            exchange.sendResponseHeaders(statusCode, contentLength)

            // 3. Stream bounded chunks to LibVLC (GoogleVideo CDN limits chunks to 128KB)
            val chunkSize = 128 * 1024L // 128 KB chunks
            var current = start
            val output = exchange.responseBody
            try {
                while (current <= end) {
                    val chunkEnd = minOf(current + chunkSize - 1, end)
                    val chunkReq = Request.Builder()
                        .url(target.url)
                        .header("Range", "bytes=$current-$chunkEnd")
                        .apply { if (target.userAgent.isNotEmpty()) header("User-Agent", target.userAgent) }
                        .build()

                    client.newCall(chunkReq).execute().use { chunkResp ->
                        if (!chunkResp.isSuccessful) {
                            println("[LocalStreamProxy] Chunk $current-$chunkEnd failed: HTTP ${chunkResp.code}")
                            return
                        }
                        chunkResp.body?.byteStream()?.copyTo(output)
                        output.flush()
                    }
                    current = chunkEnd + 1
                }
            } catch (e: Exception) {
                // Socket closed or connection reset when player seeks or closes media
            } finally {
                try { output.close() } catch (_: Exception) {}
            }
        }
    }
}
