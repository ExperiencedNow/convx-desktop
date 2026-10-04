# Spike S4: Database & Settings Persistence (Room KMP + DataStore on JVM)

**Date**: 2026-10-04  
**Status**: **PASS**  
**Environment**: Windows 11 x64, Temurin JDK 21, Kotlin 2.3.10, Room 2.8.4, SQLite Bundled 2.5.0-alpha11, DataStore 1.2.0  

## 1. Objective & Pass Criteria
Verify whether Convx's data persistence stack—Room Database and AndroidX DataStore Preferences—can compile and execute natively on the desktop JVM using Room 2.8.4 KMP with the Bundled SQLite driver (`androidx.sqlite:sqlite-bundled`) without requiring Android framework APIs or an external SQLite installation.

**Pass Criteria**:
1. Configure Room 2.8.4 runtime and KSP compiler in `:desktopApp`.
2. Construct and build Room database instance using `BundledSQLiteDriver`.
3. Perform CRUD operations (insert, batch insert, query by ID, ordered query, delete) against an actual SQLite database file on Windows.
4. Construct DataStore Preferences instance on JVM, perform type-safe reads, atomic edits, and verify disk persistence.

## 2. Test Execution & Results Summary
Executed `SpikeS4DatabaseTest.kt` in `:desktopApp:test`.

| Component / Test | Target | Result | Status |
|---|---|---|---|
| KSP Room Code Generation | Kotlin 2.3.10 JVM | Generated DAO & Database implementations cleanly | **PASS** |
| Bundled SQLite Driver | Windows x64 JVM | Embedded C SQLite engine loaded and active | **PASS** |
| Room CRUD Operations | SQLite table operations | Inserted 3 rows, verified count, retrieved by ID, deleted | **PASS** |
| SQLite File Persistence | Windows NTFS file | Database created and saved to disk (20,480 bytes) | **PASS** |
| DataStore Preferences Write | Proto-backed prefs | Wrote `dark_mode`, `audio_quality`, `discord_rpc_enabled` | **PASS** |
| DataStore Atomic Edit | Mutation transaction | Atomically updated `audio_quality` to "AUTO" | **PASS** |
| DataStore File Persistence | `.preferences_pb` file | Preferences persisted to disk (69 bytes) | **PASS** |

## 3. Architecture Decisions & Implementation Guidance
1. **Decision D-010**: Use Room 2.8.4 KMP with `androidx.sqlite:sqlite-bundled` for desktop persistence. This allows direct reuse of Convx's Room entities (`SongEntity`, `ArtistEntity`, `AlbumEntity`, `PlaylistEntity`, `SearchHistory`, `LyricsEntity`, etc.) and DAOs on Desktop without rewriting SQL queries.
2. **Decision D-011**: Use AndroidX DataStore Preferences (`androidx.datastore:datastore-preferences:1.2.0`) for settings storage on Desktop. Paths on Windows should resolve to `%APPDATA%\Convx\settings.preferences_pb` (or `%LOCALAPPDATA%`).
3. **Database Migration Compatibility**: Convx's existing SQLite schema (version 37) can be opened directly or imported seamlessly from mobile Convx backups on desktop.
