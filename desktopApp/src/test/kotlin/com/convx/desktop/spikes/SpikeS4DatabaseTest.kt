package com.convx.desktop.spikes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

@Entity(tableName = "test_songs")
data class TestSong(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Int,
    val isLiked: Boolean = false
)

@Dao
interface TestSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: TestSong)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<TestSong>)

    @Query("SELECT * FROM test_songs WHERE id = :id")
    suspend fun getById(id: String): TestSong?

    @Query("SELECT * FROM test_songs ORDER BY title ASC")
    suspend fun getAll(): List<TestSong>

    @Query("SELECT COUNT(*) FROM test_songs")
    suspend fun count(): Int

    @Query("DELETE FROM test_songs WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Database(entities = [TestSong::class], version = 1, exportSchema = false)
abstract class TestMusicDatabase : RoomDatabase() {
    abstract fun songDao(): TestSongDao
}

class SpikeS4DatabaseTest {

    @Test
    fun testRoomDatabaseWithBundledSQLite() = runBlocking {
        println("=== SPIKE S4: ROOM KMP 2.8.4 & BUNDLED SQLITE EVALUATION ===")

        val tempDbFile = File.createTempFile("convx_test_db_", ".db")
        tempDbFile.deleteOnExit()
        println("Temporary database path: ${tempDbFile.absolutePath}")

        // 1. Build RoomDatabase using BundledSQLiteDriver (pure C SQLite engine on JVM)
        val db = Room.databaseBuilder<TestMusicDatabase>(
            name = tempDbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .build()

        assertNotNull("Database instance should be constructed", db)
        val dao = db.songDao()
        assertNotNull("DAO should be available", dao)

        // 2. Insert test entities
        val song1 = TestSong("id1", "Blinding Lights", "The Weeknd", 200, isLiked = true)
        val song2 = TestSong("id2", "Starboy", "The Weeknd", 230, isLiked = false)
        val song3 = TestSong("id3", "Get Lucky", "Daft Punk", 248, isLiked = true)

        dao.insert(song1)
        dao.insertAll(listOf(song2, song3))

        // 3. Query entities
        val count = dao.count()
        println("Stored song count: $count")
        assertEquals(3, count)

        val retrieved = dao.getById("id1")
        assertNotNull("Should retrieve song1 by ID", retrieved)
        assertEquals("Blinding Lights", retrieved?.title)
        assertEquals("The Weeknd", retrieved?.artist)
        assertEquals(true, retrieved?.isLiked)
        println("Retrieved song: ${retrieved?.title} by ${retrieved?.artist} (liked=${retrieved?.isLiked})")

        val allSongs = dao.getAll()
        println("All songs query returned: ${allSongs.map { it.title }}")
        assertEquals(3, allSongs.size)

        // 4. Delete entity
        dao.deleteById("id2")
        assertEquals(2, dao.count())
        println("Deleted song id2, remaining: ${dao.count()}")

        db.close()
        println("Room database closed cleanly. SQLite file size: ${tempDbFile.length()} bytes")
        assertTrue("Database file should exist and have content", tempDbFile.exists() && tempDbFile.length() > 0)
    }

    @Test
    fun testDataStorePreferencesPersistence() = runBlocking {
        println("\n=== SPIKE S4: DATASTORE PREFERENCES ON JVM EVALUATION ===")

        val tempPrefsFile = File.createTempFile("convx_settings_", ".preferences_pb")
        tempPrefsFile.deleteOnExit()
        println("Temporary preferences path: ${tempPrefsFile.absolutePath}")

        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { tempPrefsFile }
        )

        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_AUDIO_QUALITY = stringPreferencesKey("audio_quality")
        val KEY_DISCORD_RPC = booleanPreferencesKey("discord_rpc_enabled")

        // 1. Initial write
        dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = true
            prefs[KEY_AUDIO_QUALITY] = "HIGH"
            prefs[KEY_DISCORD_RPC] = true
        }

        // 2. Read back
        val initialPrefs = dataStore.data.first()
        println("Read initial preferences: dark_mode=${initialPrefs[KEY_DARK_MODE]}, quality=${initialPrefs[KEY_AUDIO_QUALITY]}")
        assertEquals(true, initialPrefs[KEY_DARK_MODE])
        assertEquals("HIGH", initialPrefs[KEY_AUDIO_QUALITY])
        assertEquals(true, initialPrefs[KEY_DISCORD_RPC])

        // 3. Atomic update
        dataStore.edit { prefs ->
            prefs[KEY_AUDIO_QUALITY] = "AUTO"
        }

        val updatedPrefs = dataStore.data.first()
        println("Read updated preferences: quality=${updatedPrefs[KEY_AUDIO_QUALITY]}")
        assertEquals("AUTO", updatedPrefs[KEY_AUDIO_QUALITY])
        assertEquals(true, updatedPrefs[KEY_DARK_MODE])

        println("DataStore file size: ${tempPrefsFile.length()} bytes")
        assertTrue("Preferences file should exist and have content", tempPrefsFile.exists() && tempPrefsFile.length() > 0)
        println("SUCCESS: Spike S4 Database & Preferences evaluation completed cleanly!")
    }
}
