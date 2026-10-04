package com.convx.desktop.audio

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DesktopAudioPlayerTest {

    @Test
    fun testDesktopAudioPlayerLifecycleAndState() = runTest {
        println("=== TESTING DESKTOP AUDIO PLAYER ===")
        val player = DesktopAudioPlayer()
        assertNotNull("Player should be instantiated", player)

        // 1. Initial State
        assertEquals(PlaybackStatus.IDLE, player.status.value)
        assertEquals(0.8f, player.volume.value, 0.01f)
        assertFalse(player.isMuted.value)
        assertEquals(0L, player.positionMs.value)
        assertEquals(RepeatMode.OFF, player.repeatMode.value)
        assertFalse(player.isShuffle.value)

        // 2. Volume and Mute
        player.setVolume(0.4f)
        assertEquals(0.4f, player.volume.value, 0.01f)
        assertFalse(player.isMuted.value)

        player.toggleMute()
        assertTrue(player.isMuted.value)

        player.setVolume(0.6f)
        assertEquals(0.6f, player.volume.value, 0.01f)
        assertFalse("Setting volume above 0 should unmute", player.isMuted.value)

        // 3. Shuffle and Repeat
        player.toggleShuffle()
        assertTrue(player.isShuffle.value)
        player.toggleShuffle()
        assertFalse(player.isShuffle.value)

        player.cycleRepeat()
        assertEquals(RepeatMode.ALL, player.repeatMode.value)
        player.cycleRepeat()
        assertEquals(RepeatMode.ONE, player.repeatMode.value)
        player.cycleRepeat()
        assertEquals(RepeatMode.OFF, player.repeatMode.value)

        // 4. Queue Setup
        val testQueue = listOf(
            MediaTrack("3_g2un5M350", "Starboy", "The Weeknd ft. Daft Punk", "3:50", 230),
            MediaTrack("4NRXx6U8ABQ", "Blinding Lights", "The Weeknd", "3:20", 200)
        )
        player.playQueue(testQueue, startIndex = 1)
        assertEquals(2, player.queue.value.size)
        assertEquals(1, player.queueIndex.value)
        assertEquals("Blinding Lights", player.currentTrack.value?.title)

        // 5. Seek
        player.seekToMs(12000L)
        assertEquals(12000L, player.positionMs.value)

        // 6. Release
        player.release()
        println("SUCCESS: DesktopAudioPlayer engine and state verified!")
    }
}
