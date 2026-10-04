# 01 — AUDIT TEMPLATES (Phase 1)

Copy each template into `docs/audit/<name>.md` and fill it **from source**, with `file:line` references. Empty or guessed cells are not allowed — write `UNKNOWN` and add it to `docs/OPEN_QUESTIONS.md`.

Suggested method: use the repo's `.codegraph` folder if it is a usable index; otherwise grep imports (`android.`, `androidx.`, `com.google.android.`, `androidx.media3`, `androidx.room`, `coil`).

---

## A. Module inventory → `docs/audit/modules.md`
| Module | Gradle plugin (`android.library` / `kotlin.jvm` / `kotlin.multiplatform`) | Purpose | Android imports count | Depends on | Desktop verdict (PURE / ADAPT / ANDROID-ONLY) | Notes |
|---|---|---|---|---|---|---|
| innertube | | | | | | |
| app | | | | | | |
| lrclib | | | | | | |
| kugou | | | | | | |
| betterlyrics | | | | | | |
| simpmusic | | | | | | |
| youlyplus | | | | | | |
| paxsenixlyrics | | | | | | |
| lastfm | | | | | | |
| kizzy | | | | | | |
| jiosaavn | | | | | | |
| spine | | | | | | |
| spotify | | | | | | |
| shazamkit | | | | | | |
| canvas / applecanvas / vivimusiccanvas / artistvideo | | | | | | |

Also record: Kotlin version, Compose version, AGP, minSdk/targetSdk, build flavors (FOSS/GMS — `BuildConfig.CAST_AVAILABLE` is mentioned in the README), all dependency versions from the version catalog.

## B. Android-API inventory → `docs/audit/android-api.md`
One row per **file** that imports Android-only APIs.
| File | Android API used | What it is used for | Desktop strategy (expect/actual, interface, replace, drop) | Effort (S/M/L) | Priority tier |
|---|---|---|---|---|---|

## C. Design tokens → `docs/audit/design-tokens.md`
### C1 Glass
| Element (nav bar, mini bar, puck, back/share buttons, sheets, menus, cards…) | blur (dp/px) | vibrancy | lens refractionHeight | lens refractionAmount | depth effect | chromatic aberration | tint color+alpha | highlight style | shadow | shape / corner radius | source file:line | Settings key that changes it |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
### C2 Color & theme
Light/dark palettes, dynamic color source, text-color override logic, library background modes.
### C3 Typography
Font families (Google Sans, Sans Flex, Outfit, Plus Jakarta Sans, system, custom), sizes, weights, line heights per text style.
### C4 Spacing & shape
Paddings, margins, corner radii, icon sizes, touch-target sizes, nav bar height, mini player height, per window-size class.
### C5 Motion
Every `spring(...)`, `tween(...)`, easing, duration: value + file:line + where used. Include the rubber-band overscroll constants (0.55 etc.), bounce spring natural period, morph animation spec, page-transition blur spec.
### C6 Iconography
Icon pack(s), custom V1/V2 player glyph slots, app icon variants.

## D. Screens & user flow → `docs/audit/screens.md`
| Route | Composable | ViewModel | Entry points | Back behavior | Transition in/out | Backdrop attach point | Tablet/wide variant | Reference capture file |
|---|---|---|---|---|---|---|---|---|
Add a Mermaid flow chart of the whole navigation graph.

## E. System flows → `docs/audit/system-flows.md`
Write sequence diagrams (Mermaid) for:
1. Cold start → Home loaded.
2. Tap a song → client chain → stream URL → player → UI states.
3. Playback failure → retry → visitorData rotation → fallback client.
4. Lyrics fetch: provider order, cache, word-sync data model.
5. Like/download → DB → sync to YouTube account.
6. Login → session persistence → account switching.
7. Local-only mode: scan → DB → Home shelves.
8. Preset export/import file format (exact schema).

## F. Settings inventory → `docs/audit/settings.md`
| Preference key | Type | Default | Screen & label | Effect | Android-only? |
|---|---|---|---|---|---|
(All of `constants/PreferenceKeys.kt`.)

## G. Third-party dependency licenses → `docs/audit/licenses.md`
Every dependency, license, GPL-3.0 compatibility verdict (important for the audio engine and JCEF/WebView choices).

## H. Phase 1 exit checklist
- [ ] A–G complete, no empty cells
- [ ] `PORT_MANIFEST.md` has every file in `app/` and reused modules
- [ ] P0/P1/P2 list updated and shown to the user
- [ ] Every `[ASSUMPTION]` in the master plan marked CONFIRMED / CORRECTED in `docs/DECISIONS.md`
