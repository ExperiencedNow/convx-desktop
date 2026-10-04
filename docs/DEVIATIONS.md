# Deviations from Convx Android r52

All deviations must strictly follow rule F3: only desktop input, window chrome, Windows OS integration, and replacement of Android-only subsystems.

| ID | Component | Deviation | Rationale | Source Reference | User Impact |
|---|---|---|---|---|---|
| DEV-01 | Navigation | No IME keyboard open delay (`KeyboardOpenDelayMs = 260` removed) | Desktop hardware keyboard is always present, no software IME slide-in delay needed | `FloatingTabBar.kt` / `06_SOURCE_FINDINGS.md §4` | Instant search bar activation |
| DEV-02 | Window Chrome | Window title bar / minimize / maximize / close controls added | Desktop window management | Master Plan §1-F3 | Standard desktop window control |
| DEV-03 | Sources | TIDAL source excluded | Source finding C7: fetches lossless FLAC via public 3rd-party proxies | `06_SOURCE_FINDINGS.md §2 C7` | Cleaner legal posture; YouTube Music remains primary |
