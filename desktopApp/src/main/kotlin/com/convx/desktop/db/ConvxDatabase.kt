package com.convx.desktop.db

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
import kotlinx.coroutines.flow.Flow
import java.io.File

@Entity(tableName = "songs")
data class SongRecord(
    @PrimaryKey val id: String,
    val title: String,
    val artists: String,
    val durationSeconds: Int,
    val durationText: String,
    val thumbnailUrl: String,
    val isLiked: Boolean = false,
    val playCount: Int = 0,
    val lastPlayedTime: Long = 0L
)

@Entity(tableName = "search_history")
data class SearchHistoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val query: String,
    val timestamp: Long
)

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(song: SongRecord)

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: String): SongRecord?

    @Query("SELECT * FROM songs ORDER BY lastPlayedTime DESC LIMIT :limit")
    fun getRecentlyPlayed(limit: Int = 20): Flow<List<SongRecord>>

    @Query("SELECT * FROM songs WHERE isLiked = 1 ORDER BY title ASC")
    fun getLikedSongs(): Flow<List<SongRecord>>

    @Query("UPDATE songs SET isLiked = :liked WHERE id = :id")
    suspend fun setLiked(id: String, liked: Boolean)

    @Query("UPDATE songs SET playCount = playCount + 1, lastPlayedTime = :time WHERE id = :id")
    suspend fun recordPlay(id: String, time: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun count(): Int
}

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(record: SearchHistoryRecord)

    @Query("SELECT query FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentQueries(limit: Int = 10): Flow<List<String>>

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}

@Database(entities = [SongRecord::class, SearchHistoryRecord::class], version = 1, exportSchema = false)
abstract class ConvxDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        fun create(dbFile: File): ConvxDatabase {
            dbFile.parentFile?.mkdirs()
            return Room.databaseBuilder<ConvxDatabase>(
                name = dbFile.absolutePath
            )
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
        }
    }
}
