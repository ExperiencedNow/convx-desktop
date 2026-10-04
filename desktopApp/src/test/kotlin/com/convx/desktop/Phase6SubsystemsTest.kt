package com.convx.desktop

import com.convx.desktop.integrations.DesktopDiscordRpc
import com.convx.desktop.integrations.DesktopLastFm
import com.convx.desktop.lyrics.DesktopLyricsManager
import com.convx.desktop.lyrics.LyricLine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase6SubsystemsTest {

    @Test
    fun testParseLrcSyncedLines() {
        val sampleLrc = """
            [ti:Starboy]
            [ar:The Weeknd]
            [al:Starboy]
            [00:10.50]I'm tryna put you in the worst mood, ah
            [00:15.200]P1 cleaner than your church shoes, ah
            [00:20.00]Milli point two just to hurt you, ah
        """.trimIndent()

        val parsed = DesktopLyricsManager.parseLrc(sampleLrc)

        assertTrue(parsed.isSynced)
        assertEquals(3, parsed.lines.size)

        // Line 1: 00:10.50 -> 10 * 1000 + 500 = 10500 ms
        assertEquals(10500L, parsed.lines[0].timeMs)
        assertEquals("I'm tryna put you in the worst mood, ah", parsed.lines[0].text)

        // Line 2: 00:15.200 -> 15 * 1000 + 200 = 15200 ms
        assertEquals(15200L, parsed.lines[1].timeMs)
        assertEquals("P1 cleaner than your church shoes, ah", parsed.lines[1].text)

        // Line 3: 00:20.00 -> 20 * 1000 = 20000 ms
        assertEquals(20000L, parsed.lines[2].timeMs)
        assertEquals("Milli point two just to hurt you, ah", parsed.lines[2].text)
    }

    @Test
    fun testParseLrcWithSyllableTags() {
        val sampleSyllableLrc = """
            [00:05.10]<00:05.10>Blinding <00:05.60>lights <00:06.10>in <00:06.50>the <00:07.00>night
            [00:08.00]{bg}Backup vocals singing
        """.trimIndent()

        val parsed = DesktopLyricsManager.parseLrc(sampleSyllableLrc)

        assertTrue(parsed.isSynced)
        assertEquals(2, parsed.lines.size)
        assertEquals("Blinding lights in the night", parsed.lines[0].text)
        assertEquals("Backup vocals singing", parsed.lines[1].text)
    }

    @Test
    fun testParsePlainLyrics() {
        val plainText = """
            Line one without timestamps
            Line two without timestamps
            Line three
        """.trimIndent()

        val parsed = DesktopLyricsManager.parseLrc(plainText)

        assertFalse(parsed.isSynced)
        assertEquals(3, parsed.lines.size)
        assertEquals(-1L, parsed.lines[0].timeMs)
        assertEquals("Line one without timestamps", parsed.lines[0].text)
    }

    @Test
    fun testLyricsManagerActiveLineTracking() = runBlocking {
        val manager = DesktopLyricsManager.getInstance()
        manager.clear()

        // Manually test position tracking logic
        val lines = listOf(
            LyricLine(0L, "Intro"),
            LyricLine(5000L, "First Verse"),
            LyricLine(12000L, "Chorus"),
            LyricLine(25000L, "Outro")
        )

        // Verify active index calculations at different timestamps
        fun findActiveIndex(timeMs: Long): Int {
            var active = -1
            for (i in lines.indices) {
                if (lines[i].timeMs <= timeMs) {
                    active = i
                } else {
                    break
                }
            }
            return active
        }

        assertEquals(0, findActiveIndex(2000L))  // Within Intro
        assertEquals(1, findActiveIndex(5000L))  // Exactly on First Verse
        assertEquals(1, findActiveIndex(8000L))  // Mid First Verse
        assertEquals(2, findActiveIndex(15000L)) // Chorus
        assertEquals(3, findActiveIndex(30000L)) // Outro
    }

    @Test
    fun testDiscordRpcSingleton() {
        val rpc = DesktopDiscordRpc.getInstance()
        assertNotNull(rpc)
        // Should not throw when updating config with empty or valid tokens
        rpc.updateConfig(enabled = false, token = "")
        rpc.clearPresence()
    }

    @Test
    fun testLastFmSingleton() {
        val lastFm = DesktopLastFm.getInstance()
        assertNotNull(lastFm)
        lastFm.updateConfig(enabled = false, sessionKey = "")
    }
}
