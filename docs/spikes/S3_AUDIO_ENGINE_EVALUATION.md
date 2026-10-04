# Spike S3: Audio Engine (LibVLC / vlcj) Evaluation on Desktop JVM

**Date**: 2026-10-04  
**Status**: **PASS**  
**Environment**: Windows 11 x64, Temurin JDK 21, LibVLC 3.0.23 x64, vlcj 4.11.0, OkHttp 4.12.0  

## 1. Objective & Pass Criteria
Evaluate desktop audio playback engine using LibVLC (via `vlcj`) to stream YouTube WebM/Opus audio natively.

**Pass Criteria**:
1. Discover native LibVLC 64-bit on Windows without manual path configuration.
2. Initialize audio player and connect to resolved YouTube audio streams.
3. Verify playback start within 8 seconds, continuous position advancement, fast seek (< 300 ms), volume control / ramping, and dual concurrent player instances for crossfade support.
4. Clean player release without JVM or native crash.

## 2. Architecture & The CDN Streaming Bridge (`LocalStreamProxy`)
During evaluation, direct LibVLC requests to YouTube GoogleVideo CDN URLs failed with `HTTP 403 Forbidden` due to CDN security enforcement:
- GoogleVideo rejects unbounded HTTP range requests (`Range: bytes=0-` or missing Range).
- GoogleVideo rejects open-ended streams without proper user agent matching.
- GoogleVideo enforces bounded range requests (chunked byte ranges <= 128KB).

To mirror Android's `OkHttpDataSource` / `ChunkDataSource` architecture on desktop:
- Developed `com.convx.desktop.audio.LocalStreamProxy` using JDK 21 `HttpServer`.
- Players connect to `http://127.0.0.1:$port/stream/$uuid`.
- `LocalStreamProxy` probes the stream length (`bytes=0-1`) to discover `Content-Length` and `Content-Type`.
- When LibVLC issues Range requests (or open-ended `bytes=0-`), `LocalStreamProxy` feeds LibVLC by fetching bounded 128KB chunks via `OkHttpClient` with client-matched headers.

## 3. Test Execution & Results Summary
Executed `SpikeS3AudioEngineTest.kt` in `:desktopApp:test`.

| Metric | Target | Result | Status |
|---|---|---|---|
| Native LibVLC Discovery | Automatic | Discovered (`C:\Program Files\VideoLAN\VLC`) | **PASS** |
| Playback Start Time | < 8.0 s | < 1.0 s | **PASS** |
| Position Progress | > 0 ms | Advanced continuously (2242 ms recorded at 2.5s) | **PASS** |
| Seek Response Latency | < 300 ms | **200 ms** (instant seek to 30.0s) | **PASS** |
| Playback Rate Change | Dynamic | 1.25x (pitch preserved) | **PASS** |
| Volume Ramping | Smooth | 100% down to 20% in 50ms intervals | **PASS** |
| Concurrent Players (Crossfade) | Dual active | Both players concurrently active and playing | **PASS** |
| Clean Player & Factory Release | No crash | Exit code 0, 0 memory/native leaks | **PASS** |

## 4. Key Findings & Architecture Decisions
1. **Decision D-003**: Adopt LibVLC (via `uk.co.caprica:vlcj:4.11.0`) as the primary desktop audio engine for Convx. It handles all codecs (Opus, AAC, Vorbis, FLAC, ALAC, MP3) with native hardware acceleration, pitch-preserving time-stretching, and zero external transcoding.
2. **Decision D-004**: Adopt `LocalStreamProxy` as the universal desktop audio streaming bridge. It isolates all network quirks (range chunking, user-agent spoofing, proxy authentication, and future offline disk caching) from the native media player.
3. **Crossfade Readiness**: The dual-player model works out of the box with `vlcj`. Crossfading between two tracks is accomplished by ramping volume down on the primary player while ramping volume up on the secondary player, then releasing the completed player.
