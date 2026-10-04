# Third-Party Dependency Licenses & GPL-3.0 Compatibility (Phase 1 Audit)

Convx is licensed under **GNU General Public License v3.0 (GPL-3.0)**.
The desktop port is a derivative work and remains under GPL-3.0.

## Dependency License Audit

| Component / Library | Purpose | License | GPL-3.0 Compatibility Verdict | Notes |
|---|---|---|---|---|
| **Convx Core** | App UI, logic, viewmodels | GPL-3.0 | Authoritative | Original author: Aryan "CosmicTaser" |
| **vivi-music** | Upstream music foundation | GPL-3.0 | Compatible | By Vividh P Ashokan |
| **NewPipeExtractor** | InnerTube YouTube stream extraction | GPL-3.0 | Compatible | Team NewPipe |
| **Compose Multiplatform** | Desktop UI runtime & Skia graphics | Apache-2.0 | Compatible | JetBrains / Google |
| **Kyant0 / backdrop** | Liquid glass backdrop shaders | Apache-2.0 | Compatible | Modified source vendored in Convx |
| **Ktor Client** | Network engine (OkHttp/CIO engines) | Apache-2.0 | Compatible | JetBrains |
| **Coil 3** | Image & artwork loading | Apache-2.0 | Compatible | Multiplatform image loader |
| **MaterialKolor** | Dynamic color extraction from artwork | Apache-2.0 | Compatible | Material You color schemes |
| **Room KMP / SQLite** | Local relational database | Apache-2.0 | Compatible | AndroidX |
| **DataStore KMP** | Preference & queue persistence | Apache-2.0 | Compatible | AndroidX |
| **Lucide Icons** | SF-Symbols style icon vector set | ISC | Compatible | Permissive license |
| **LibVLC / vlcj** | Audio engine candidate (S3) | LGPL-2.1+ / GPL-3.0 | Compatible | Bundled native runtime under LGPL-2.1+ |
| **libmpv** | Audio engine candidate (S3) | LGPL-2.1+ / GPL-3.0 | Compatible | Clean GPL-3.0 compliance |
| **JAudiotagger** | Local music tag scanner | LGPL-2.1+ | Compatible | Replaces Android MediaStore scanner |
| **QuickJS** | JS cipher deobfuscation runtime | MIT | Compatible | Embedded JavaScript engine |
| **Brotli** | Stream compression decompression | MIT | Compatible | Google |

## Obligations for Desktop Release
1. Ship a visible **Credits & Licenses** screen (`Screens.About` / Credits).
2. Bundle `LICENSE` (GPL-3.0 text) and `NOTICE` in the distributable and installer root.
3. Keep the non-affiliation disclaimer with YouTube/Google visible on first launch and in About.
4. Source code of every distributed release must be publicly accessible under GPL-3.0.
