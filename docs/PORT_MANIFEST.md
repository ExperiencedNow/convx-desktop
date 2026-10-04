# Port Manifest

Tracks porting status of every subsystem, module, and source file from Convx r52 into `desktopApp` / `desktop-core`.

Status values:
- `COPIED`: Reused directly or copied with identical code
- `ADAPTED`: Adapted for Compose Desktop / JVM (thin expect/actual seam)
- `REPLACED`: Replaced by desktop equivalent (e.g. MediaStore -> FolderScanner, Media3 -> PlayerEngine)
- `SKIPPED`: Excluded or dropped per F3/C7 (e.g. Android Auto, TIDAL, ringtone)

| Component / File | Original Path | Status | Adaptations | Verification / Tests |
|---|---|---|---|---|
| **InnerTube Client** | `innertube/` | COPIED | 100% pure Kotlin; direct dependency | InnerTube search & stream unit tests |
| **LRCLIB Provider** | `lrclib/` | COPIED | 100% pure Kotlin; direct dependency | LRCLIB lyrics tests |
| **BetterLyrics** | `betterlyrics/` | COPIED | 100% pure Kotlin; direct dependency | Word-sync lyrics tests |
| **Kugou Lyrics** | `kugou/` | COPIED | 100% pure Kotlin; direct dependency | Kugou lyrics tests |
| **SimpMusic Lyrics** | `simpmusic/` | COPIED | 100% pure Kotlin; direct dependency | SimpMusic tests |
| **YouLyPlus** | `youlyplus/` | COPIED | 100% pure Kotlin; direct dependency | Lyrics tests |
| **Last.fm Scrobbler** | `lastfm/` | COPIED | 100% pure Kotlin; direct dependency | Scrobble test |
| **Discord RPC (Kizzy)** | `kizzy/` | COPIED | 100% pure Kotlin; direct dependency | Discord presence test |
| **Spotify Canvas** | `spotify/` | COPIED | 100% pure Kotlin; direct dependency | Canvas stream resolution test |
| **ShazamKit** | `shazamkit/` | COPIED | 100% pure Kotlin; direct dependency | Recognition network test |
| **Canvas Core** | `canvas/` | COPIED | 100% pure Kotlin; direct dependency | Video metadata test |
| **Apple Canvas** | `applecanvas/` | COPIED | 100% pure Kotlin; direct dependency | Apple canvas video test |
| **ViviMusic Canvas** | `vivimusiccanvas/` | COPIED | 100% pure Kotlin; direct dependency | Vivi canvas test |
| **Paxsenix Lyrics** | `paxsenixlyrics/` | ADAPTED | Remove single `Context` import, use cache path | Lyrics resolution test |
| **Musixmatch Lyrics** | `musixmatchlyrics/` | ADAPTED | Remove single `Context` import, use cache path | Token & lyrics test |
| **Glass Effect** | `app/.../ui/component/GlassEffect.kt` | ADAPTED | Maintain public API verbatim; wire internals to Skia backdrop | `GlassEffectTest.kt` (ported) |
| **Backdrop Shaders** | `app/.../ui/component/backdrop/` | ADAPTED | Skia image filter blur & SkSL runtime shader | S1 spike test (60fps benchmark) |
| **Design Tokens** | `app/.../ui/theme/AppleTokens.kt` | COPIED | Direct copy into `desktop-core` | Parity checks |
| **Dimensions** | `app/.../constants/Dimensions.kt` | COPIED | Direct copy into `desktop-core` | Dimension checks |
| **Motion Specs** | `app/.../ui/utils/Motion.kt` | COPIED | Direct copy into `desktop-core` | Motion test suite |
| **iOS Overscroll** | `app/.../ui/utils/IosOverscroll.kt` | ADAPTED | Mouse wheel / trackpad scroll tuning | `OverscrollBounceTest.kt` |
| **Navigation Graph** | `app/.../ui/screens/NavigationBuilder.kt` | ADAPTED | Desktop window navigation container | Navigation smoke tests |
| **App Shell & Floating Bar** | `app/.../ui/component/FloatingNavBar.kt` | ADAPTED | Mouse pointer tracking instead of touch | Visual parity check |
| **Mini Player & Player V2** | `app/.../ui/player/` | ADAPTED | Pill-to-sheet morph on desktop window | `PlayerSheetMotionTest.kt` |
| **Playback Service** | `app/.../playback/MusicService.kt` | REPLACED | `PlayerEngine` interface + bundled libVLC | S3 audio playback spike |
| **Database** | `app/.../db/MusicDatabase.kt` | ADAPTED | Room KMP with bundled SQLite driver | S4 DB migrations test |
| **Preferences** | `app/.../utils/DataStore.kt` | ADAPTED | Multiplatform DataStore KMP | Preference read/write tests |
| **Local Music Scanner** | `app/.../utils/LocalAudioScanner.kt` | REPLACED | Java NIO folder walker + JAudiotagger | Local music scan test |
| **TIDAL Integration** | `app/.../utils/tidal/` | SKIPPED | Excluded per C7 | Excluded |
| **Android Auto / Widgets** | `app/.../widget/` | SKIPPED | Dropped (no desktop equivalent) | Replaced by Windows SMTC |
