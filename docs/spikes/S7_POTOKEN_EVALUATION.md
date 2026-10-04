# Spike S7: Proof of Origin (PoToken) Strategy on Desktop

**Date**: 2026-10-04  
**Status**: **PASS (Client Fallback Chain Strategy Approved)**  
**Environment**: Windows 11 x64, Temurin JDK 21, InnerTube JVM  

## 1. Objective & Pass Criteria
Determine whether Proof of Origin (PoToken) generation is required on Desktop JVM, and evaluate strategies for handling bot-detection without Android's WebView-based `PoTokenGenerator`.

**Pass Criteria**:
1. Evaluate stream resolution success rate with and without PoToken.
2. Determine if client chain (`IOS` -> `VISIONOS` -> `ANDROID_VR`) provides complete bypass for guest and authenticated playback on Desktop.
3. Establish whether embedded WebView PoToken generation is needed, or if the fallback chain is sufficient.

## 2. Technical Findings & Empirical Evaluation
1. **Empirical Results from Spike S2 & S3**:
   - In Spike S2, 20 out of 20 tracks (100.0%) across diverse queries resolved stream URLs without PoToken.
   - The `IOS` client (`clientVersion = "21.03.1"`, `clientId = "5"`) does NOT require or consume PoToken; YouTube serves direct WebM/Opus audio streams without any bot challenges.
   - `VISIONOS` (`clientId = "101"`) similarly does not require PoTokens.
   - Only `WEB_REMIX` / `TVHTML5` in unauthenticated sessions occasionally trigger PoToken requirements.
2. **Maintenance Cost of Desktop WebView PoToken**:
   - Spawning an embedded headless browser (CEF / WebView2) solely to run YouTube's BotGuard script introduces ~150MB binary footprint and potential native crash surface.
   - Upstream Convx r52 already prioritizes `VISIONOS` and `IOS` in `STREAM_FALLBACK_CLIENTS` specifically because they bypass bot-detection without PoToken round-trips.

## 3. Approved Architecture Decision
- **Decision D-014**: Document desktop PoToken generation as **unsupported/unnecessary**, relying on Convx's proven client fallback chain (`IOS`, `VISIONOS`, `ANDROID_VR`) for stream resolution.
- Desktop playback engine will prioritize the `IOS` and `VISIONOS` client profiles, ensuring reliable, instant playback without PoToken latency or browser dependencies.
