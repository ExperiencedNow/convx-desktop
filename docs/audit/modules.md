# Module Inventory (Phase 1 Audit)

Audit completed from source in `convx-desktop` @ nightly-r52 (commit `1e2237d9`).

## Global Versions
- **Kotlin**: `2.3.10`
- **Compose**: `1.10.2` (Android Compose) / `1.12.1` (Compose Multiplatform Desktop)
- **AGP**: `9.2.1`
- **compileSdk**: `37` (app) / `36` (libraries)
- **minSdk**: `26`
- **targetSdk**: `36`
- **JVM Toolchain / Target**: Java 21
- **Gradle**: 9.4.1

## Module Table

| Module | Gradle Plugin | Purpose | Android Imports Count | Depends On | Desktop Verdict | Notes |
|---|---|---|---|---|---|---|
| `app` | `com.android.application` | Main Android application, UI, ViewModels, Services, DB | 250+ | All submodules, Media3, Room, Hilt, Coil, Ktor | ADAPT / COPY-PORT | UI and viewmodels to be copy-ported into `desktop-core`, entry into `desktopApp` |
| `innertube` | `com.android.library` | Unofficial YouTube & YouTube Music InnerTube API client | 0 | Ktor, Brotli, NewPipeExtractor | PURE (Reusable directly) | Zero Android imports! Pure Kotlin client. Can be converted to `kotlin("jvm")` or consumed directly |
| `lrclib` | `com.android.library` | LRCLIB synchronized lyrics provider | 0 | Ktor Core/OkHttp/CIO/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports. |
| `betterlyrics` | `com.android.library` | BetterLyrics synchronized/word-sync lyrics provider | 0 | Ktor Core/CIO/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports. |
| `kugou` | `com.android.library` | Kugou synchronized lyrics provider | 0 | Ktor Core/OkHttp/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports. |
| `simpmusic` | `com.android.library` | SimpMusic API lyrics & metadata | 0 | Ktor Core/CIO/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports. |
| `youlyplus` | `com.android.library` | YouLyPlus lyrics service | 0 | Ktor Core/OkHttp/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports. |
| `paxsenixlyrics`| `com.android.library` | Paxsenix lyrics service | 1 (`Context`) | Ktor, Timber, `:betterlyrics` | ADAPT (Trivial) | Single Context import for cache directory; can be adapted with a file path |
| `musixmatchlyrics` | `com.android.library` | Musixmatch token & lyrics client | 1 (`Context`) | Ktor, Timber | ADAPT (Trivial) | Single Context import for cache path |
| `lastfm` | `com.android.library` | Last.fm scrobbler & metadata client | 0 | Ktor Core/OkHttp/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports. |
| `kizzy` | `com.android.library` | Discord Rich Presence RPC integration | 0 | Ktor, `org.json` | PURE (Reusable directly) | Zero Android imports. |
| `spotify` | `com.android.library` | Spotify canvas & metadata resolver | 0 | Ktor Core/OkHttp/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports. |
| `shazamkit` | `com.android.library` | Shazam track recognition client | 0 | Ktor Core/OkHttp/CIO/Negotiation/Serialization | PURE (Reusable directly) | Zero Android imports! Not an iOS/Android SDK wrapper, pure Ktor network client |
| `canvas` | `kotlin` (JVM) | Canvas video base models & network client | 0 | Ktor Core/OkHttp/Negotiation/Serialization | PURE (Reusable directly) | Already a pure Kotlin module (`id("kotlin")`) |
| `applecanvas` | `kotlin` (JVM) | Apple Music Canvas video resolver | 0 | `:canvas`, Ktor Core/OkHttp/Negotiation/Serialization | PURE (Reusable directly) | Already a pure Kotlin module (`id("kotlin")`) |
| `vivimusiccanvas`| `kotlin` (JVM) | ViviMusic Canvas video stream provider | 0 | `:canvas`, Ktor Core/OkHttp/Negotiation/Serialization | PURE (Reusable directly) | Already a pure Kotlin module (`id("kotlin")`) |
| `artistvideo` | `com.android.library` | Artist background video player component | 3 (`TextureView`, `ViewGroup`) | Ktor, Compose, Media3 | ADAPT / REPLACE | Android View video player replaced by desktop video/canvas surface in Phase 4 |
| `jiosaavn` | `com.android.library` | JioSaavn API client | 1 (`Log`) | Ktor Core/CIO/Negotiation/Serialization | DEFERRED (Per C7) | Deferred until written user instruction; replacement of `Log` is trivial |
| `spine` | `com.android.library` | QuickJS runtime for dynamic JS extractors | 2 (`Log`) | QuickJS-kt, Ktor, Timber | DEFERRED (Per C7) | Deferred per C7; uses Android QuickJS-kt; desktop uses JVM QuickJS in S6 |
| `listen-together-server` | Standalone | Local WebSocket server for Listen Together | 0 | Ktor Server | P2 | Optional feature |
