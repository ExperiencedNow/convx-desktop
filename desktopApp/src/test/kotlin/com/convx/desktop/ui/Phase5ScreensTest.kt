package com.convx.desktop.ui

import com.convx.desktop.audio.DesktopAudioPlayer
import com.convx.desktop.audio.MediaTrack
import com.convx.desktop.audio.RepeatMode
import com.convx.desktop.db.ConvxDatabase
import com.convx.desktop.db.SearchHistoryRecord
import com.convx.desktop.db.SettingsManager
import com.convx.desktop.db.SongRecord
import com.convx.desktop.ui.component.GlassEffectConfig
import com.convx.desktop.ui.component.GlassStyle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase5ScreensTest {

    @Test
    fun testPhase5ScreensAndStateFlows() = runBlocking {
        println("=== PHASE 5 TEST: SCREENS & PERSISTENT DATA BINDING ===")

        val tempDb = File.createTempFile("convx_phase5_", ".db").apply { deleteOnExit() }
        val database = ConvxDatabase.create(tempDb)
        val tempPrefs = File.createTempFile("convx_prefs_phase5_", ".preferences_pb").apply { deleteOnExit() }
        val settingsManager = SettingsManager(
            androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
                produceFile = { tempPrefs }
            )
        )

        // 1. Verify Home Screen & Database initial state
        val initialRecent = database.songDao().getRecentlyPlayed(10).first()
        assertTrue("Initially recently played should be empty", initialRecent.isEmpty())

        // 2. Play a song and simulate Home screen tracking
        val testTrack = MediaTrack(
            id = "test_starboy",
            title = "Starboy",
            artists = "The Weeknd ft. Daft Punk",
            durationText = "3:50",
            durationSeconds = 230,
            thumbnailUrl = "https://example.com/starboy.jpg"
        )

        database.songDao().insertOrUpdate(
            SongRecord(
                id = testTrack.id,
                title = testTrack.title,
                artists = testTrack.artists,
                durationSeconds = testTrack.durationSeconds,
                durationText = testTrack.durationText,
                thumbnailUrl = testTrack.thumbnailUrl,
                isLiked = true,
                playCount = 1,
                lastPlayedTime = System.currentTimeMillis()
            )
        )

        val updatedRecent = database.songDao().getRecentlyPlayed(10).first()
        assertEquals(1, updatedRecent.size)
        assertEquals("Starboy", updatedRecent.first().title)
        assertTrue(updatedRecent.first().isLiked)

        // 3. Verify Search Screen query history & like toggle
        database.searchHistoryDao().insertQuery(
            SearchHistoryRecord(query = "Daft Punk", timestamp = System.currentTimeMillis())
        )
        val recentQueries = database.searchHistoryDao().getRecentQueries(5).first()
        assertEquals(listOf("Daft Punk"), recentQueries)

        database.songDao().setLiked("test_starboy", false)
        val likedSongs = database.songDao().getLikedSongs().first()
        assertTrue("Liked songs should be empty after toggle", likedSongs.isEmpty())

        // 4. Verify Settings Screen reactivity with SettingsManager
        settingsManager.setGlassStyle(GlassStyle.TRANSPARENT)
        val activeStyle = settingsManager.glassStyle.first()
        assertEquals(GlassStyle.TRANSPARENT, activeStyle)

        settingsManager.setGlassVibrancy(1.5f)
        val activeVibrancy = settingsManager.glassVibrancy.first()
        assertEquals(1.5f, activeVibrancy, 0.01f)

        settingsManager.setGlassBlurRadius(12f)
        val activeBlur = settingsManager.glassBlurRadius.first()
        assertEquals(12f, activeBlur, 0.01f)

        // 5. Build reactive GlassEffectConfig matching DesktopShell logic
        val config = GlassEffectConfig(
            style = activeStyle,
            blurRadius = activeBlur,
            vibrancy = activeVibrancy
        )
        assertEquals(GlassStyle.TRANSPARENT, config.style)
        assertEquals(12f, config.blurRadius, 0.01f)
        assertEquals(1.5f, config.vibrancy, 0.01f)

        database.close()
        println("SUCCESS: Phase 5 screens, state flows, and persistence verified!")
    }
}
