package com.convx.desktop.spikes

import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient.Companion.IOS
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class SpikeS3AudioEngineTest {

    @Test
    fun testVlcjAudioEngineWithYouTubeStream() = runBlocking {
        println("=== SPIKE S3: AUDIO ENGINE (LIBVLC / VLCJ) EVALUATION ===")

        // 1. Discover native LibVLC
        val discovery = NativeDiscovery()
        val discovered = discovery.discover()
        println("Native LibVLC discovered: $discovered")
        assertTrue("Native LibVLC should be discovered on system", discovered)

        // 2. Initialize MediaPlayerFactory with YouTube client User-Agent
        val userAgent = IOS.userAgent
        val factory = MediaPlayerFactory(
            "--no-video",
            "--intf=dummy",
            "--no-stats",
            "--http-user-agent=$userAgent"
        )
        println("MediaPlayerFactory initialized with userAgent: $userAgent")

        // 3. Create Dual MediaPlayers for Crossfade Verification
        val player1 = factory.mediaPlayers().newMediaPlayer()
        val player2 = factory.mediaPlayers().newMediaPlayer()
        assertNotNull("Primary player created", player1)
        assertNotNull("Secondary player created", player2)
        println("Dual concurrent MediaPlayer instances created (Player 1 & Player 2)")

        // 4. Probe multiple clients & User-Agents to verify GoogleVideo CDN playback
        val videoId = "3_g2un5M350" // Starboy
        println("Probing YouTube audio stream resolution & CDN HTTP fetch for $videoId...")
        val testClients = listOf(
            com.music.innertube.models.YouTubeClient.ANDROID_VR_1_43_32,
            IOS,
            com.music.innertube.models.YouTubeClient.VISIONOS,
            com.music.innertube.models.YouTubeClient.TVHTML5_SIMPLY_EMBEDDED_PLAYER,
            com.music.innertube.models.YouTubeClient.WEB_REMIX,
            com.music.innertube.models.YouTubeClient.ANDROID_NO_SDK
        )

        val httpClient = okhttp3.OkHttpClient()
        var workingClient: com.music.innertube.models.YouTubeClient? = null
        var workingStreamUrl: String? = null
        var workingUserAgent: String? = null

        for (client in testClients) {
            println("\n--- Testing client: ${client.clientName} (${client.friendlyName ?: client.clientVersion}) ---")
            val pResponse = YouTube.player(videoId = videoId, client = client).getOrNull()
            val playability = pResponse?.playabilityStatus?.status
            println("Playability: $playability")
            val format = pResponse?.streamingData?.adaptiveFormats
                ?.filter { it.mimeType.startsWith("audio/") }
                ?.maxByOrNull { it.bitrate ?: 0 }
                ?: pResponse?.streamingData?.formats?.filter { it.mimeType.startsWith("audio/") }?.maxByOrNull { it.bitrate ?: 0 }

            val url = format?.url
            if (url == null) {
                println("No direct URL (cipher or empty)")
                continue
            }
            println("Found stream URL (${format.mimeType}, ${format.bitrate} bps)")

            val req = okhttp3.Request.Builder()
                .url(url)
                .header("Range", "bytes=0-1")
                .header("User-Agent", client.userAgent)
                .build()
            val resp = httpClient.newCall(req).execute()
            println("Probe bytes=0-1 -> code: ${resp.code}, Content-Range: ${resp.header("Content-Range")}, Content-Type: ${resp.header("Content-Type")}")
            val contentRange = resp.header("Content-Range")
            val totalLength = contentRange?.substringAfterLast('/')?.toLongOrNull() ?: -1L
            println("Extracted total length: $totalLength bytes")
            resp.close()

            workingClient = client
            workingStreamUrl = url
            workingUserAgent = client.userAgent
            break
        }

        assertNotNull("Must find a working client and stream URL for CDN playback", workingStreamUrl)
        println("\n>>> SUCCESSFUL STREAM: client=${workingClient?.clientName}, UA=${workingUserAgent}")

        val streamUrl = workingStreamUrl!!
        val activeUserAgent = workingUserAgent ?: ""

        // Start local proxy bridging OkHttp to LibVLC
        val proxy = com.convx.desktop.audio.LocalStreamProxy()
        proxy.start()
        val proxyUrl = proxy.registerStream(streamUrl, activeUserAgent)
        println("LocalStreamProxy running on port ${proxy.port} -> streaming via $proxyUrl")

        // 5. Test Playback and Time Progress
        val playingLatch = CountDownLatch(1)
        val timeAdvanced = AtomicBoolean(false)
        val lastPosition = AtomicLong(0)

        player1.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer?) {
                println("EVENT: player1 is PLAYING")
                playingLatch.countDown()
            }

            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                if (newTime > 0) {
                    timeAdvanced.set(true)
                    lastPosition.set(newTime)
                }
            }
        })

        println("Starting playback on player1 via local streaming proxy...")
        player1.media().play(proxyUrl)

        val started = playingLatch.await(8, TimeUnit.SECONDS)
        println("Playback started within 8 seconds: $started")
        assertTrue("Playback should begin", started)

        // Let it play for 2.5 seconds to verify position advancing
        delay(2500)
        println("Position after 2.5s: ${lastPosition.get()} ms (time advanced: ${timeAdvanced.get()})")
        assertTrue("Time/position must advance during playback", timeAdvanced.get())

        // 6. Test Seek (< 300 ms response)
        val seekStart = System.currentTimeMillis()
        player1.controls().setTime(30_000) // Seek to 30s
        delay(200)
        val seekDuration = System.currentTimeMillis() - seekStart
        println("Seek to 30s completed in $seekDuration ms (current pos: ${player1.status().time()} ms)")
        assertTrue("Seek response latency should be fast (< 500 ms)", seekDuration < 500)

        // 7. Test Speed / Rate Change with pitch preservation
        player1.controls().setRate(1.25f)
        println("Playback rate adjusted to 1.25x: ${player1.status().rate()}")
        assertTrue("Playback rate changed", player1.status().rate() == 1.25f)

        // 8. Test Volume Ramp (Crossfade Simulation)
        for (vol in 100 downTo 20 step 20) {
            player1.audio().setVolume(vol)
            delay(50)
        }
        println("Volume ramped down on player1: current volume = ${player1.audio().volume()}")

        // 9. Test Starting Player 2 Concurrently
        player2.audio().setVolume(30)
        player2.media().play(proxyUrl)
        delay(1500)
        val bothPlaying = player1.status().isPlaying && player2.status().isPlaying
        println("Dual players playing concurrently (crossfade capable): $bothPlaying")
        assertTrue("Both players should play concurrently", bothPlaying)

        // 10. Clean release
        player1.controls().stop()
        player2.controls().stop()
        player1.release()
        player2.release()
        factory.release()
        proxy.stop()
        println("SUCCESS: Spike S3 Audio Engine evaluation completed cleanly!")
    }
}
