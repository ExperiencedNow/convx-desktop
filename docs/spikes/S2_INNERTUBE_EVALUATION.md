# Spike S2: InnerTube Client & Stream Resolution on JVM

**Date**: 2026-10-04  
**Status**: **PASS (100.0% success rate)**  
**Environment**: Temurin JDK 21, Kotlin 2.3.10, Ktor 3.4.0, Compose Desktop  

## 1. Objective & Pass Criteria
Verify whether the unofficial YouTube & YouTube Music InnerTube API client (`:innertube`) runs natively on the JVM without Android dependencies and reliably resolves playable audio stream URLs using Convx r52's fallback client chain.

**Pass Criteria**: Search for tracks, resolve playable stream URLs in guest mode repeatedly across 20 attempts with at least 75% success rate.

## 2. Methodology & Test Execution
- Executed `SpikeS2InnerTubeTest.kt` in `:desktopApp:test`.
- Tested popular search queries: `Starboy`, `Blinding Lights`, `Get Lucky`, `Bohemian Rhapsody`, `Hotel California`.
- Queried `YouTube.search(query, FILTER_SONG)`.
- Resolved stream audio for top 4 results per query (total 20 tracks) using client fallback chain:
  1. `ANDROID_VR_1_43_32`
  2. `VISIONOS`
  3. `IOS`
  4. `ANDROID_VR_1_61_48`
  5. `WEB_REMIX`
  6. `TVHTML5_SIMPLY_EMBEDDED_PLAYER`

## 3. Results Summary

| Metric | Result |
|---|---|
| Total Tracks Tested | 20 |
| Successful Stream Resolutions | **20 / 20 (100.0%)** |
| Failed Resolutions | 0 |
| Primary Effective Client | **`IOS`** (consistently bypassed bot-guard on guest sessions) |
| Audio Codec Delivered | `audio/webm; codecs="opus"` |
| Bitrate Range | **134 kbps - 192 kbps** (high-quality Opus) |
| Test Execution Duration | 37 seconds |

## 4. Key Findings & Architecture Decisions
1. **Zero Android Dependencies**: `:innertube` has 0 Android imports and compiles/runs flawlessly on pure Kotlin JVM.
2. **Client Chain Efficacy**: In headless/guest desktop sessions, `IOS` successfully and instantly provided direct audio stream URLs for 100% of tested tracks without requiring PoToken or browser verification.
3. **Decoded Audio Stream Format**: Streams are Opus encoded in WebM containers (`audio/webm`). The audio player engine (Spike S3) must support WebM/Opus stream playback natively.
