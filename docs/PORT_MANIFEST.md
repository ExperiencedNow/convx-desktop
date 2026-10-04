# Port Manifest

Tracks porting status of every subsystem, module, and source file from Convx r52 into `desktopApp` / `desktop-core`.

Status values:
- `COPIED`: Reused directly or copied with identical code
- `ADAPTED`: Adapted for Compose Desktop / JVM (thin expect/actual seam)
- `REPLACED`: Replaced by desktop equivalent (e.g. MediaStore -> FolderScanner, Media3 -> PlayerEngine)
- `SKIPPED`: Excluded or dropped per F3/C7 (e.g. Android Auto, TIDAL, ringtone)

| Component / File | Original Path | Status | Adaptations | Verification / Tests |
|---|---|---|---|---|
| **InnerTube Client** | `innertube/` | COPIED | 100% pure Kotlin; direct dependency | `SpikeS2InnerTubeTest` (20/20 pass) |
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
| **Design Tokens** | `app/.../ui/theme/AppleTokens.kt` | ADAPTED | Pure Kotlin RGB <-> HSL conversion math replacing Android `ColorUtils` | `Phase3FoundationTest` (pass) |
| **Squircle Shape** | `app/.../shapes/ContinuousRoundedRectangle.kt` | ADAPTED | Ported G0/G1/G2 continuities and path builder using pure Compose UI `Path` | `Phase3FoundationTest` (pass) |
| **Glass Effect** | `app/.../ui/component/GlassEffect.kt` | ADAPTED | Preserved exact r52 public API; integrated Skia RuntimeEffect and highlight | `Phase3FoundationTest`, `SpikeS1GlassShaderTest` (pass) |
| **Vector Icons** | `app/src/main/res/drawable/` | ADAPTED | Ported 18 Lucide / Apple-style icons into `ConvxIcons.kt` | `Phase3FoundationTest` (pass) |
| **Desktop Shell** | `desktopApp/.../ui/shell/DesktopShell.kt` | ADAPTED | Custom draggable title bar, tablet sidebar with animated puck, liquid glass dock | `Phase3FoundationTest`, `Main.kt` |
| **Audio Engine** | `desktopApp/.../audio/LocalStreamProxy.kt` | REPLACED | `vlcj 4.11.0` (LibVLC 3.0.23) + JDK 21 `LocalStreamProxy` with 128KB chunking | `SpikeS3AudioEngineTest` (pass) |
| **QuickJS Runtime** | `desktopApp/...` | REPLACED | `quickjs-kt 1.0.5` C-native runtime replacing Android WebView | `SpikeS6QuickJsTest` (pass) |
| **Database** | `desktopApp/...` | ADAPTED | Room KMP 2.8.4 + SQLite bundled driver | `SpikeS4DatabaseTest` (pass) |
| **Preferences** | `desktopApp/...` | ADAPTED | AndroidX DataStore Preferences 1.2.0 on JVM | `SpikeS4DatabaseTest` (pass) |
| **Paxsenix Lyrics** | `paxsenixlyrics/` | ADAPTED | Remove single `Context` import, use cache path | Lyrics resolution test |
| **Musixmatch Lyrics** | `musixmatchlyrics/` | ADAPTED | Remove single `Context` import, use cache path | Token & lyrics test |
| **Local Music Scanner** | `app/.../utils/LocalAudioScanner.kt` | REPLACED | Java NIO folder walker + JAudiotagger | Local music scan test |
| **TIDAL Integration** | `app/.../utils/tidal/` | SKIPPED | Excluded per C7 | Excluded |
| **Android Auto / Widgets** | `app/.../widget/` | SKIPPED | Dropped (no desktop equivalent) | Replaced by Windows SMTC |
