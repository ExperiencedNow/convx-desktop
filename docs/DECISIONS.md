# Architecture & Design Decisions

| ID | Date | Area | Context | Decision | Alternatives Considered | Model | Status |
|---|---|---|---|---|---|---|---|
| D01 | 2026-10-04 | Target Platform | Target Windows Desktop with shared UI fidelity | Kotlin Compose Multiplatform for Desktop (Skia, JVM) | Tauri, Electron, Flutter (rejected per F1/F2 fidelity) | Opus / Gemini | ACCEPTED |
| D02 | 2026-10-04 | Upstream Pin | Pinned upstream version | Convx Nightly r52 (commit `1e2237d9f8dd56de1c8a97dffc9c31e6596c437a`) | v1.5.2 stable (kept as secondary reference) | Opus / Gemini | ACCEPTED |
| D03 | 2026-10-04 | Source Strategy | Fork vs standalone rewrite | Fork repository `ExperiencedNow/convx-desktop`, branch `desktop/main`, maintain untouched Android modules as oracle | Clean repo from scratch (violates F2/F5) | Opus / Gemini | ACCEPTED |
| D04 | 2026-10-04 | JDK Version | Toolchain requirement | Eclipse Adoptium Temurin JDK 21 (with jpackage) | JDK 17, JDK 23 | Opus / Gemini | ACCEPTED |
| D05 | 2026-10-04 | Excluded Providers | C7 in 06_SOURCE_FINDINGS.md | Exclude TIDAL intercept completely. Defer JioSaavn & Spine until explicit written instruction. | Include TIDAL (rejected: third-party proxy paid stream risk) | Opus / Gemini | ACCEPTED |
| D06 | 2026-10-04 | Compose MP Version | Compose Multiplatform Gradle plugin | `org.jetbrains.compose` 1.12.1 aligned with Kotlin 2.3.10 | 1.8.x - 1.11.x | Gemini | PROPOSED |
