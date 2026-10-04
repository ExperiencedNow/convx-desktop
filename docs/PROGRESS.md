# Progress

## Status (updated 2026-10-05)
Phase: 6 COMPLETE -> Phase 7 Preview Gate READY for User Evaluation
Last commit: `137e073` · Branch: `desktop/main`
Last full test run: Spikes S1-S7 (100% Pass), Phase 3 (Pass), Phase 5 (Pass), Phase 6 (Pass), Android Oracle (100% Pass)

## Accomplished
- **Phase 0 & 1 (Scaffolding & Toolchains)**: JDK 21, LibVLC 3.0.23, Gradle 9.4.1, multi-project scaffolding.
- **Phase 2 (Technical Spikes)**: S1–S7 all PASSED (SkSL Shader, InnerTube 20/20 streams, LibVLC dual playback, Room KMP + DataStore, QuickJS runtime).
- **Phase 3 (Foundation & Apple Tokens)**: `AppleTokens.kt`, squircle shape system (G0/G1/G2), `GlassEffect.kt` SkSL compilation, `ConvxIcons.kt` (18 icons), `DesktopShell.kt` with custom title bar and floating liquid glass mini-player dock.
- **Phase 4 (Audio Engine & Persistence)**: `DesktopAudioPlayer.kt` (vlcj LibVLC engine with reactive StateFlows), `ConvxDatabase.kt` (Room KMP 2.8.4 on JVM with Bundled SQLite), `SettingsManager.kt` (DataStore Preferences 1.2.0 on JVM), `StoragePaths.kt` (`%LOCALAPPDATA%\Convx`).
- **Phase 5 (Screens & Features)**:
  - `HomeScreen`: Featured Hero card, Quick Picks row, Recently Played row synced to Room DB.
  - `SearchScreen`: Liquid glass search bar, InnerTube live song search, search history chips from `SearchHistoryDao`, track rows with like toggle.
  - `LibraryScreen`: Liked Songs and Recently Played tabs, "Play All", "Shuffle All", track list synced to `SongDao`.
  - `SongsScreen`: Table view of all tracks, instant keyword filtering, play count and duration display.
  - `SettingsScreen`: Reactive liquid glass tuning (style, blur, vibrancy), master volume slider, mute switch, storage paths display, about section.
  - `AsyncArtwork.kt`: In-memory cached async image loader using Skia `Image.makeFromEncoded`.
- **Phase 6 (Subsystems - Lyrics, Canvas & Integrations)**:
  - Converted pure Kotlin submodules (`lrclib`, `betterlyrics`, `kugou`, `simpmusic`, `youlyplus`, `lastfm`, `kizzy`) to pure Kotlin JVM modules shared across desktop and Android.
  - `DesktopLyricsManager`: Waterfall lyrics engine coordinating SimpMusic (by videoId), LrcLib, YouLyPlus, BetterLyrics, and KuGou with LRU caching, regex time parser, and active line tracking.
  - `LyricsView.kt`: Apple-style full-screen liquid glass lyrics panel with smooth spring active-line auto-scrolling, bold white glow highlight, and click-to-seek audio navigation.
  - `DesktopDiscordRpc`: Rich presence broadcasting "Listening to {Title} by {Artist}" via Kizzy RPC WebSocket.
  - `DesktopLastFm`: Last.fm scrobbler integration.
- **Phase 7 (Packaging & Preview Gate)**:
  - Built unpacked native portable desktop app via `createDistributable`:
    `desktopApp\build\compose\binaries\main\app\Convx\Convx.exe` (148.4 MB total self-contained bundle).
  - Authored comprehensive Preview Gate Report: [`docs/evidence/preview/PREVIEW_REPORT.md`](file:///c:/Users/Michael%20Sandi/YouTube%20Music%20Desktop/convx-desktop/docs/evidence/preview/PREVIEW_REPORT.md).
  - Verified Android Oracle `:app:compileUniversalFossDebugKotlin` is 100% GREEN (125/125 tasks up to date).

## Next 3 actions
1. User tests and verifies Preview B portable folder using the 10-minute guided tour.
2. User provides approval by creating `APPROVED.txt` with `APPROVED FOR EXE`.
3. Agent executes final installer packaging via `scripts\package-exe.ps1` to produce the installer `.exe`.

## Blockers / waiting on user
- Waiting for user evaluation of the Preview Gate (Phase 7). Final `.exe` packaging is blocked until user approves per Rule F7.
