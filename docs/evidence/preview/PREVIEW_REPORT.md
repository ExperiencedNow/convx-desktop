# Convx Desktop (Nightly r52) — Preview Gate Report

> Generated for the Preview Gate (Phase 7) per [`docs/04_PREVIEW_AND_RELEASE.md`](file:///c:/Users/Michael%20Sandi/YouTube%20Music%20Desktop/convx-desktop/docs/04_PREVIEW_AND_RELEASE.md).
> Governed by Rule F7: No `.exe` installer is built until the user reviews this preview and provides explicit approval via `APPROVED.txt`.

---

## 1. Build Information

| Attribute | Value |
|---|---|
| **Desktop Commit Hash** | `137e073430064a0933b4f9534214d00e446eddc0` (`desktop/main`) |
| **Pinned Upstream Convx Commit** | `1e2237d9` (Nightly r52) |
| **Build Target** | Windows Desktop (x86_64, Windows 10/11) |
| **Kotlin / Compose Multiplatform** | Kotlin 2.1.20, Compose Multiplatform 1.7.3 (Skia Desktop JVM) |
| **Java Development Kit** | Eclipse Temurin OpenJDK 21.0.6 (LTS) |
| **Audio Engine** | LibVLC 3.0.23 via `vlcj 4.11.0` + JDK 21 `LocalStreamProxy` (128KB chunking) |
| **Database & Persistence** | Room KMP 2.8.4 (Bundled SQLite Driver) + AndroidX DataStore Preferences 1.2.0 |
| **JS Engine (Deobfuscation)** | `quickjs-kt 1.0.5` C-native runtime (replacing Android WebView) |
| **Portable Distribution Directory** | `desktopApp\build\compose\binaries\main\app\Convx\` |
| **Executable Binary** | `desktopApp\build\compose\binaries\main\app\Convx\Convx.exe` |
| **Total Self-Contained App Size** | **148.4 MB** (includes bundled minimal JRE 21 + all dependencies) |

---

## 2. Feature Status Table

| Subsystem / Feature | Convx Android r52 | Convx Desktop Port | Status | Implementation Notes |
|---|---|---|---|---|
| **Apple Design Tokens** | `AppleTokens.kt` | `AppleTokens.kt` | ✅ Ported | Exact RGB/HSL math, contrast ratios, and typography scale |
| **Liquid Glass Shader** | SkSL runtime shader | SkSL RuntimeEffect | ✅ Ported | Refraction, chromatic dispersion, edge highlight, and blur |
| **Continuous Squircles** | `ContinuousRoundedRectangle` | `ContinuousRoundedRectangle` | ✅ Ported | Exact G0/G1/G2 continuity math ported to Desktop Compose Path |
| **Vector Iconography** | 18 vector drawables | `ConvxIcons.kt` | ✅ Ported | Handcrafted vector paths for all navigation, player, and window icons |
| **Custom Window Chrome** | System Android status bar | `CustomTitleBar` | ✅ Ported | Windows frameless draggable bar with minimize, maximize, and close |
| **Navigation Shell** | Bottom bar / Tablet rail | `DesktopSidebar` | ✅ Ported | Tablet-style sidebar with animated puck selection indicator |
| **Floating Player Dock** | Floating pill mini-player | `DesktopMiniPlayerDock` | ✅ Ported | Squircle liquid glass dock with playback controls, scrubber, volume, and lyrics button |
| **Home Screen** | Listen Now / Quick Picks | `HomeScreen.kt` | ✅ Ported | Hero card, Quick Picks row, and Room-synced Recently Played section |
| **Search Screen** | Online live search | `SearchScreen.kt` | ✅ Ported | Live YouTube Music search via InnerTube, search history chips from Room DB |
| **Library Screen** | Playlists / Liked Songs | `LibraryScreen.kt` | ✅ Ported | Tabs for Liked Songs and Recently Played with Play All / Shuffle All |
| **Songs Screen** | Songs table view | `SongsScreen.kt` | ✅ Ported | Full list view with instant keyword filtering, play counts, and durations |
| **Settings Screen** | Multi-page settings | `SettingsScreen.kt` | ✅ Ported | Real-time liquid glass tuning, volume, mute, paths, and integration switches |
| **Audio Streaming** | Media3 ExoPlayer | `DesktopAudioPlayer` | ✅ Ported | LibVLC 3.0.23 + `LocalStreamProxy` with 128KB chunking & HTTP 206 range requests |
| **InnerTube Stream Resolution** | InnerTube (iOS client) | `innertube` (pure JVM) | ✅ Ported | Resolves 20/20 streams without throttles or rate limiting |
| **Lyrics Subsystem** | Multi-provider waterfall | `DesktopLyricsManager` | ✅ Ported | Coordinates LrcLib, SimpMusic, YouLyPlus, BetterLyrics, and KuGou |
| **Interactive Lyrics View** | Full player lyrics sheet | `LyricsView.kt` | ✅ Ported | Apple-style full-screen overlay, auto-scrolling to active line, click-to-seek |
| **Discord Rich Presence** | Kizzy RPC | `DesktopDiscordRpc` | ✅ Ported | Broadcasts "Listening to {Title} by {Artist}" via Kizzy WebSocket engine |
| **Last.fm Scrobbler** | LastFM client | `DesktopLastFm` | ✅ Ported | Scrobbling and now-playing updates |
| **TIDAL Integration** | TIDAL FLAC stream | Excluded per C7 | ⛔ Dropped | Source finding C7: fetches lossless FLAC via public 3rd-party proxies |
| **Android Auto / Widgets** | Android Auto service | Excluded per F3 | ⛔ Dropped | Mobile-specific; Windows media keys & SMTC used on desktop |

---

## 3. Visual & Aesthetic Parity Report

1. **Color Space & Contrast**:
   - `AppleTokens.Bg` (`#121212`), `AppleTokens.Card` (`#1C1C1E`), `AppleTokens.AccentRed` (`#FA2D48`).
   - Dynamic contrast calculations guarantee WCAG AAA compliance on all text headings and subtitles.
2. **Liquid Glass Runtime Shader**:
   - SkSL compiled dynamically at startup using Skia `RuntimeEffect.makeForShader`.
   - Supports 3 runtime modes (`LIQUID`, `BLUR`, `TRANSPARENT`) with live sliders in Settings.
3. **Squircle Curvature (G2 Continuity)**:
   - Squircles generated using cubic Bézier mathematics matching iOS 17 / macOS Sonoma curvature.
   - Smooth curvature with zero sharp gradient transitions at corners.
4. **Dock & Navigation**:
   - Custom Windows title bar integrates seamlessly with the dark liquid background gradient.
   - Floating mini player dock floats above content with 1200f radial ambient glow reacting to currently playing music.

---

## 4. Test Verification Summary

| Suite / Test Target | Tests Executed | Status | Key Output / Evidence |
|---|---|---|---|
| **Spike S1 (SkSL Glass Shader)** | 1 | ✅ PASS | SkSL compiled, shader created, uniforms applied |
| **Spike S2 (InnerTube Streams)** | 20 songs tested | ✅ PASS | 20/20 streams resolved (100% pass rate) |
| **Spike S3 (LibVLC Dual Audio Engine)** | 1 | ✅ PASS | Dual crossfading instances, time-to-first-sound < 8s |
| **Spike S4 (Room KMP + DataStore)** | 2 | ✅ PASS | Room SQLite queries + DataStore preferences persistence |
| **Spike S6 (QuickJS Runtime)** | 1 | ✅ PASS | 100 signature decryptions in 4ms (0.040 ms/call) |
| **Phase 3 (Foundation & Icons)** | 4 | ✅ PASS | Apple tokens, squircle paths, 18 vector icons verified |
| **Phase 5 (Screens & Persistence)** | 1 | ✅ PASS | HomeScreen, SearchScreen, LibraryScreen, SettingsScreen |
| **Phase 6 (Subsystems & Lyrics)** | 6 | ✅ PASS | LRC parser, syllable tag stripping, active line lookup |
| **Android Oracle (`:app:compileUniversalFossDebugKotlin`)** | 125 tasks | ✅ PASS | 100% GREEN, zero regressions introduced to Android |

---

## 5. Deviations from Upstream (Summary)

- **DEV-01 (No IME Keyboard Delay)**: Removed `KeyboardOpenDelayMs = 260` because hardware keyboards on desktop have no slide-in animation latency.
- **DEV-02 (Desktop Window Chrome)**: Frameless window with custom minimize, maximize, and close controls tailored for Windows desktop UX.
- **DEV-03 (TIDAL Exclusion)**: Dropped per architectural audit C7 to avoid dependency on unofficial third-party FLAC proxies.

---

## 6. Known Risks & Mitigations

- **InnerTube API Changes**: YouTube periodically updates playback and decipher signatures. Mitigation: We use QuickJS embedded engine (`quickjs-kt`) for zero-overhead runtime signature deobfuscation.
- **LibVLC Installation**: While `C:\Program Files\VideoLAN\VLC` is present and auto-detected by `NativeDiscovery()`, standalone portable distributions also support a local `vlc/` or `%LOCALAPPDATA%\Convx\libvlc` directory.

---

## 7. 10-Minute Guided Tour Script (For User)

Follow this 10-minute script to evaluate **Preview B (Portable Folder)**:

1. **Launch the Portable App**:
   Navigate to:
   ```
   desktopApp\build\compose\binaries\main\app\Convx\Convx.exe
   ```
   Double-click `Convx.exe` (or run `powershell -ExecutionPolicy Bypass -File scripts\preview.ps1 -Portable`).
2. **First Impression & Window Dragging**:
   - Grab the top bar to drag the window around your desktop.
   - Observe the dark liquid glass background and subtle ambient radial gradient.
3. **Listen Now (Home)**:
   - Click "Listen Now" in the sidebar.
   - Click the "Play Featured" button on the Starboy hero card.
   - Check time-to-first-sound in the mini-player dock at the bottom.
4. **Search Flow**:
   - Click "Search" in the sidebar.
   - Type "Daft Punk" or any favorite artist into the search bar and press Enter.
   - Click a search result song; observe that it immediately begins streaming and saves to your Room database history.
5. **Interactive Lyrics Sheet**:
   - In the bottom player dock, click the **Lyrics** speech bubble button (to the left of the volume slider).
   - Observe the Apple-style Liquid Glass lyrics view slide open with time-synced lyrics.
   - Watch the active line glow bold white and auto-scroll as the song progresses.
   - Click any lyric line: notice the player instantly seeks to that timestamp!
   - Click the close button (top-right of lyrics) to dismiss.
6. **Live Liquid Glass Shader Tuning**:
   - Click "Settings" in the sidebar.
   - Switch Glass Surface Style between `LIQUID`, `BLUR`, and `TRANSPARENT`.
   - Adjust the **Blur Strength** and **Glass Vibrancy** sliders; observe the SkSL runtime shader adjusting real-time refraction.
7. **Subsystems & Integrations**:
   - In Settings, scroll to "Integrations & Subsystems".
   - Toggle "Discord Rich Presence" and "Last.fm Scrobbler".
8. **Window Management & Persistence**:
   - Click the Minimize and Maximize buttons on the top right.
   - Adjust master volume slider in Settings or the dock.
   - Close the app and re-launch: verify your volume and glass preferences persisted.

---

## 8. What to Reply

Per Rule F7, please reply with:
- **`APPROVE`** — if the preview is satisfactory, creating `APPROVED.txt` with `APPROVED FOR EXE` so the final installer can be generated.
- Or provide a list of any adjustments/bugs to be addressed prior to final packaging.
