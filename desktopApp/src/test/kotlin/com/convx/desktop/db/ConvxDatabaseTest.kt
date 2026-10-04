package com.convx.desktop.db

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.convx.desktop.audio.RepeatMode
import com.convx.desktop.ui.component.GlassStyle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ConvxDatabaseTest {

    @Test
    fun testConvxDatabaseSongAndHistoryOperations() = runTest {
        println("=== TESTING CONVX DATABASE (ROOM KMP ON JVM) ===")
        val tempDb = File.createTempFile("test_convx_db_", ".db")
        tempDb.deleteOnExit()

        val db = ConvxDatabase.create(tempDb)
        val songDao = db.songDao()
        val historyDao = db.searchHistoryDao()

        // 1. Insert and retrieve song
        val song = SongRecord(
            id = "3_g2un5M350",
            title = "Starboy",
            artists = "The Weeknd ft. Daft Punk",
            durationSeconds = 230,
            durationText = "3:50",
            thumbnailUrl = "https://i.ytimg.com/vi/3_g2un5M350/hqdefault.jpg"
        )
        songDao.insertOrUpdate(song)
        assertEquals(1, songDao.count())

        val retrieved = songDao.getSongById("3_g2un5M350")
        assertNotNull(retrieved)
        assertEquals("Starboy", retrieved?.title)
        assertFalse(retrieved!!.isLiked)

        // 2. Liked toggle and query
        songDao.setLiked("3_g2un5M350", true)
        val likedSongs = songDao.getLikedSongs().first()
        assertEquals(1, likedSongs.size)
        assertEquals("Starboy", likedSongs.first().title)

        // 3. Play count tracking
        val playTime = 1700000000000L
        songDao.recordPlay("3_g2un5M350", playTime)
        val played = songDao.getSongById("3_g2un5M350")
        assertEquals(1, played?.playCount)
        assertEquals(playTime, played?.lastPlayedTime)

        val recent = songDao.getRecentlyPlayed(10).first()
        assertEquals(1, recent.size)

        // 4. Search history operations
        historyDao.insertQuery(SearchHistoryRecord(query = "The Weeknd", timestamp = 1000L))
        historyDao.insertQuery(SearchHistoryRecord(query = "Daft Punk", timestamp = 2000L))

        val recentQueries = historyDao.getRecentQueries(5).first()
        assertEquals(2, recentQueries.size)
        assertEquals("Daft Punk", recentQueries[0])
        assertEquals("The Weeknd", recentQueries[1])

        historyDao.clearHistory()
        val emptyQueries = historyDao.getRecentQueries(5).first()
        assertTrue(emptyQueries.isEmpty())

        db.close()
        println("SUCCESS: ConvxDatabase Room operations verified!")
    }

    @Test
    fun testSettingsManagerPersistence() = runTest {
        println("=== TESTING SETTINGS MANAGER ===")
        val tempPrefs = File.createTempFile("test_settings_", ".preferences_pb")
        tempPrefs.deleteOnExit()

        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = { tempPrefs }
        )
        val settings = SettingsManager(dataStore)

        // Initial default reads
        assertEquals(0.75f, settings.volume.first(), 0.01f)
        assertFalse(settings.isMuted.first())
        assertEquals(RepeatMode.OFF, settings.repeatMode.first())
        assertFalse(settings.isShuffle.first())
        assertEquals(GlassStyle.LIQUID, settings.glassStyle.first())

        // Updates
        settings.setVolume(0.9f)
        assertEquals(0.9f, settings.volume.first(), 0.01f)

        settings.setMuted(true)
        assertTrue(settings.isMuted.first())

        settings.setRepeatMode(RepeatMode.ALL)
        assertEquals(RepeatMode.ALL, settings.repeatMode.first())

        settings.setShuffle(true)
        assertTrue(settings.isShuffle.first())

        settings.setGlassStyle(GlassStyle.BLUR)
        assertEquals(GlassStyle.BLUR, settings.glassStyle.first())

        settings.setGlassVibrancy(1.5f)
        assertEquals(1.5f, settings.glassVibrancy.first(), 0.01f)

        println("SUCCESS: SettingsManager persistence verified!")
    }
}
