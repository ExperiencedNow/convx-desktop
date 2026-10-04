package com.convx.desktop.spikes

import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_43_32
import com.music.innertube.models.YouTubeClient.Companion.ANDROID_VR_1_61_48
import com.music.innertube.models.YouTubeClient.Companion.IOS
import com.music.innertube.models.YouTubeClient.Companion.TVHTML5_SIMPLY_EMBEDDED_PLAYER
import com.music.innertube.models.YouTubeClient.Companion.VISIONOS
import com.music.innertube.models.YouTubeClient.Companion.WEB_REMIX
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SpikeS2InnerTubeTest {

    private val clients = listOf(
        ANDROID_VR_1_43_32,
        VISIONOS,
        IOS,
        ANDROID_VR_1_61_48,
        WEB_REMIX,
        TVHTML5_SIMPLY_EMBEDDED_PLAYER
    )

    @Test
    fun testInnerTubeSearchAndStreamResolution() = runBlocking {
        println("=== SPIKE S2: INNERTUBE STREAM RESOLUTION ON JVM ===")
        YouTube.clearGuestSession()

        // 1. Search for songs
        val queries = listOf("Starboy", "Blinding Lights", "Get Lucky", "Bohemian Rhapsody", "Hotel California")
        var successfulResolutions = 0
        val targetTests = 20

        var testCount = 0
        for (query in queries) {
            println("\n--- Searching for '$query' ---")
            val searchResult = YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).getOrNull()
            val items = searchResult?.items.orEmpty()
            println("Found ${items.size} items for '$query'")
            assertTrue("Should find songs for '$query'", items.isNotEmpty())

            for (item in items.take(4)) {
                testCount++
                val videoId = item.id
                val title = item.title
                print("[$testCount/$targetTests] Resolving '$title' ($videoId)... ")

                var resolvedUrl: String? = null
                var successfulClient: YouTubeClient? = null
                var audioFormatInfo: String? = null

                for (client in clients) {
                    val playerResult = YouTube.player(videoId = videoId, client = client).getOrNull()
                    val playability = playerResult?.playabilityStatus?.status
                    if (playability == "OK") {
                        val streamingData = playerResult.streamingData
                        val audioFormat = streamingData?.adaptiveFormats
                            ?.filter { it.mimeType.startsWith("audio/") }
                            ?.maxByOrNull { it.bitrate ?: 0 }
                            ?: streamingData?.formats?.filter { it.mimeType.startsWith("audio/") }?.maxByOrNull { it.bitrate ?: 0 }

                        if (audioFormat?.url != null) {
                            resolvedUrl = audioFormat.url
                            successfulClient = client
                            audioFormatInfo = "${audioFormat.mimeType} (${audioFormat.bitrate?.div(1000)} kbps)"
                            break
                        }
                    }
                }

                if (resolvedUrl != null) {
                    successfulResolutions++
                    println("SUCCESS via ${successfulClient?.clientName} -> $audioFormatInfo")
                } else {
                    println("FAILED (all clients returned no direct url)")
                }

                if (testCount >= targetTests) break
            }
            if (testCount >= targetTests) break
        }

        println("\n=== S2 RESULTS: $successfulResolutions/$testCount successfully resolved ===")
        val passRate = (successfulResolutions.toDouble() / testCount) * 100
        println("Pass rate: ${"%.1f".format(passRate)}%")
        assertTrue("At least 75% of streams must resolve successfully in guest mode", passRate >= 75.0)
    }
}
