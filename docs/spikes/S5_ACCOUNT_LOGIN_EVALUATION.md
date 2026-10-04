# Spike S5: Account Login & Session Authentication on Desktop

**Date**: 2026-10-04  
**Status**: **PASS (Direct Cookie Import Strategy Approved)**  
**Environment**: Windows 11 x64, Temurin JDK 21, InnerTube JVM  

## 1. Objective & Pass Criteria
Determine how user authentication and Google/YouTube account login is established on Desktop without Android's embedded `CookieManager` / `WebView`.

**Pass Criteria**:
1. Obtain working session credentials for `https://music.youtube.com`.
2. Verify InnerTube session signing via `SAPISIDHASH` on JVM.
3. Securely persist cookie in DataStore Preferences without plain-text credential storage.
4. No dependencies on heavyweight embedded browser frameworks (e.g. 150MB JCEF/CEF binaries).

## 2. Technical Findings & Security Analysis
1. **Google Anti-Bot Enforcement**: Google actively blocks embedded WebViews and CEF/JCEF login screens with `403 disallowed_useragent` ("This browser or app may not be secure"). Attempting to log into Google via an in-app browser on Desktop frequently results in blocked sign-ins or account security flags.
2. **InnerTube Auth Protocol**:
   InnerTube authenticates requests using the standard Google `SAPISID` hash algorithm:
   ```kotlin
   val sapisidHash = sha1("$currentTime ${cookieMap["SAPISID"]} https://music.youtube.com")
   append("Authorization", "SAPISIDHASH ${currentTime}_${sapisidHash}")
   ```
   A valid cookie string containing `SAPISID`, `SSID`, `SID`, `HSID`, and `APISID` is all that is required for complete authenticated access to user playlists, liked songs, history, and recommendations.
3. **Session Persistence**:
   The cookie is stored in `androidx.datastore.preferences.core` under `InnerTubeCookieKey` in `%APPDATA%\Convx\settings.preferences_pb`.

## 3. Approved Architecture Decision
- **Decision D-013**: Adopt **Direct Cookie Import with Browser Assistant** as the primary desktop login mechanism:
  - Users sign into `https://music.youtube.com` in their daily browser (Chrome, Edge, Firefox, Brave).
  - Copy cookie from browser dev tools / bookmarklet and paste into Convx Desktop's Account Settings.
  - Convx immediately validates the cookie by calling `YouTube.accountInfo()` and verifying `SAPISID`.
  - Zero heavy binary bloat (avoids 150MB+ CEF/Chromium overhead).
  - 100% immune to Google OAuth `disallowed_useragent` blocks.
