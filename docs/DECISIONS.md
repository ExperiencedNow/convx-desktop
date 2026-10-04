# Architecture & Design Decisions

| ID | Date | Area | Context | Decision | Alternatives Considered | Model | Status |
|---|---|---|---|---|---|---|---|
| D01 | 2026-10-04 | Target Platform | Target Windows Desktop with shared UI fidelity | Kotlin Compose Multiplatform for Desktop (Skia, JVM) | Tauri, Electron, Flutter (rejected per F1/F2 fidelity) | Opus / Gemini | ACCEPTED |
| D02 | 2026-10-04 | Upstream Pin | Pinned upstream version | Convx Nightly r52 (commit `1e2237d9f8dd56de1c8a97dffc9c31e6596c437a`) | v1.5.2 stable (kept as secondary reference) | Opus / Gemini | ACCEPTED |
| D03 | 2026-10-04 | Source Strategy | Fork vs standalone rewrite | Fork repository `ExperiencedNow/convx-desktop`, branch `desktop/main`, maintain untouched Android modules as oracle | Clean repo from scratch (violates F2/F5) | Opus / Gemini | ACCEPTED |
| D04 | 2026-10-04 | JDK Version | Toolchain requirement | Eclipse Adoptium Temurin JDK 21 (with jpackage) | JDK 17, JDK 23 | Opus / Gemini | ACCEPTED |
| D05 | 2026-10-04 | Excluded Providers | C7 in 06_SOURCE_FINDINGS.md | Exclude TIDAL intercept completely. Defer JioSaavn & Spine until explicit written instruction. | Include TIDAL (rejected: third-party proxy paid stream risk) | Opus / Gemini | ACCEPTED |
| D06 | 2026-10-04 | Compose MP Version | Compose Multiplatform Gradle plugin | `org.jetbrains.compose` 1.12.1 aligned with Kotlin 2.3.10 | 1.8.x - 1.11.x | Gemini | ACCEPTED |
| D07 | 2026-10-04 | Liquid Glass Shader | Porting Convx backdrop shader to Desktop | Route G1: Direct SkSL RuntimeEffect compilation of Convx AGSL shader | Route G2 (CPU blur), Route G3 (static backdrop) | Gemini | ACCEPTED |
| D08 | 2026-10-04 | Audio Engine | Desktop audio playback engine | LibVLC via vlcj 4.11.0 (native hardware acceleration, WebM/Opus, zero transcoding, pitch preservation, crossfade) | JavaFX Media, GStreamer, mpv | Gemini | ACCEPTED |
| D09 | 2026-10-04 | Audio CDN Bridge | GoogleVideo CDN bounded range enforcement | LocalStreamProxy (localhost HttpServer + OkHttp with 128KB chunking) mirroring Android OkHttpDataSource | Direct LibVLC URL connection (fails with 403) | Gemini | ACCEPTED |
| D10 | 2026-10-04 | Database Engine | Desktop local SQL database | Room 2.8.4 KMP with BundledSQLiteDriver (`androidx.sqlite:sqlite-bundled`) | Exposed, SQLDelight, Raw JDBC SQLite | Gemini | ACCEPTED |
| D11 | 2026-10-04 | Settings Storage | Desktop preferences persistence | AndroidX DataStore Preferences (`androidx.datastore:datastore-preferences:1.2.0`) | Java Preferences API, JSON config file | Gemini | ACCEPTED |
| D12 | 2026-10-04 | JavaScript Runtime | Cipher & n-param deobfuscation without WebView | QuickJS via `io.github.dokar3:quickjs-kt:1.0.5` (30 μs execution, 0 WebView overhead, persistent context) | GraalVM JavaScript, Rhino, WebView | Gemini | ACCEPTED |
| D13 | 2026-10-04 | Account Login | YouTube Music user authentication | Direct Cookie Import with browser assistant (immune to Google 403 disallowed_useragent, 0 binary bloat) | In-app CEF/JCEF WebView | Gemini | ACCEPTED |
| D14 | 2026-10-04 | PoToken Strategy | Proof of Origin handling on desktop | Client fallback chain prioritizing `IOS` & `VISIONOS` (100% bypass in S2, zero WebView dependency) | Embedded WebView BotGuard runner | Gemini | ACCEPTED |
