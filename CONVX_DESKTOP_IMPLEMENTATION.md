# CONVX DESKTOP (Windows) — IMPLEMENTATION PLAN

Plan v1.1 · updated 2026-10-04 · executor: **Antigravity**
> **v1.1 NOTE: read `docs/06_SOURCE_FINDINGS.md` right after this page.** It contains real values and corrections taken from the Convx source (commit `a03a19df`, pre-r52). Where it contradicts sections 3-9 below, **docs/06 wins**.
Primary model: **Claude Opus 5.5 (High)** · Fallback when tokens run out: **Gemini 3.8 Flash (High)** → follow `docs/05_MODEL_HANDOFF.md`

---

## 0. READ THIS FIRST

### 0.1 What this plan is based on (and what it is not)
This plan was written from: the Convx README, the full GitHub release notes (including **Convx Nightly r52**), and public docs of the Kyant0 `backdrop` library.
It was **not** written from reading Convx source files or running the app. The plan author could not clone or execute anything.

Therefore every statement is tagged:
- `[VERIFIED-README]` / `[VERIFIED-R52]` — stated in the README or r52 release notes.
- `[VERIFIED-LIB]` — stated in public Kyant0/backdrop or Compose Multiplatform docs.
- `[ASSUMPTION]` — educated guess. **Phase 1 must confirm or correct it from the real source before any code is written that depends on it.** If the source contradicts this plan, the source wins; log it in `docs/DECISIONS.md`.

### 0.2 File map of this kit
| File | Purpose |
|---|---|
| `CONVX_DESKTOP_IMPLEMENTATION.md` | This file. The master plan. |
| `docs/01_AUDIT_TEMPLATES.md` | Templates the agent must fill during Phase 1 (module inventory, Android-API inventory, design-token extraction, screen/user-flow map). |
| `docs/02_REFERENCE_CAPTURE_GUIDE.md` | The **moodboard**. The user captures real screenshots/recordings of Convx r52; they are the visual ground truth. |
| `docs/03_TEST_PLAN.md` | Test matrix, parity checks, retest loop, exit criteria. |
| `docs/04_PREVIEW_AND_RELEASE.md` | Preview gate and the approval-controlled `.exe` build. |
| `docs/05_MODEL_HANDOFF.md` | Rules for switching Opus ⇄ Gemini Flash without losing state. |
| `docs/06_SOURCE_FINDINGS.md` | **Real source findings + corrections to this plan** (glass tokens, nav shell tokens, playback internals, exclusions). |
| `scripts/preview.ps1` | Launches the preview (dev run). |
| `scripts/package-exe.ps1` | Builds the final `.exe`; **refuses to run without approval file**. |

### 0.3 Session start ritual (every session, every model)
1. Read this file's sections 1 and 2, then `docs/PROGRESS.md` (create on first run).
2. Continue from "Next 3 actions" in `PROGRESS.md`. Do not re-plan.
3. At session end, update `PROGRESS.md`, `PORT_MANIFEST.md`, `DEVIATIONS.md`.

---

## 1. NON-NEGOTIABLE RULES

**F1 — Convx r52 is the only design authority.** Do not invent layouts, colors, radii, spacing, icons, animation curves, or flows. Extract them from source (and from reference captures in `reference/`). If something is missing, ask the user; do not improvise.

**F2 — Reuse before rewrite.** Copy Convx Kotlin/Compose code (keeping original package names and relative paths) and adapt only what cannot run on desktop. Prefer `expect/actual` seams or thin adapter interfaces over rewriting logic.

**F3 — Allowed deviations (and only these):** desktop input (mouse wheel, trackpad, keyboard shortcuts, right-click as long-press), window chrome (title bar / resize / maximize), Windows integration (tray, media keys, installer), and replacement of Android-only subsystems (see §7). Every deviation is logged in `docs/DEVIATIONS.md` with: what, why, source reference, user impact.

**F4 — Do not port known r52 glitches.** The r52 notes list: mini-bar icon glitches while scrolling, artwork flicker once, animation timings still being tuned `[VERIFIED-R52]`. Port the *intended* behavior; log it.

**F5 — Android build is the behavior oracle.** Keep the original Android project compiling/untouched in the fork. When desktop behavior is in doubt, compare with the Android app (or user's captures).

**F6 — Nothing is "done" without evidence.** A slice is done only when its tests pass and its parity checklist (docs/03) is ticked with a screenshot or log attached under `docs/evidence/`.

**F7 — No `.exe` before approval.** The final installer is built only after the user places `APPROVED.txt` (see `docs/04`). Preview builds are allowed anytime.

**F8 — Licensing.** Convx is **GPL-3.0** `[VERIFIED-README]`. The port is a derivative work → it must stay GPL-3.0, keep the LICENSE, and keep the credits (Convx/Aryan "CosmicTaser"; vivi-music by Vividh P Ashokan; Kyant0/backdrop (Apache-2.0); Better Lyrics; SimpMusic; YouLyPlus; Monochrome) `[VERIFIED-README]`. Ship a visible "Credits & Licenses" screen and a `NOTICE` file. Convx is not affiliated with YouTube/Google `[VERIFIED-README]`; keep that disclaimer.

---

## 2. SOURCE OF TRUTH

- Repo: `https://github.com/cosmictaserdev-creator/Convx`
- Version to port: **Convx Nightly r52** (pre-release, tag `nightly-r52`, commit `1e2237d9`, "20 commits since v1.5.2") `[VERIFIED-R52]`
- Latest stable for cross-reference: v1.5.2 (commit `652dc05`) `[VERIFIED-R52]`
- Steps:
  1. Fork the repo to `convx-desktop` (keeps history + license).
  2. `git checkout nightly-r52` → create branch `desktop/main` from it.
  3. Record the pinned commit in `docs/PROGRESS.md`. Never rebase onto newer upstream without a logged decision.
- Top-level folders `[VERIFIED-README listing]`: `.codegraph`, `.github`, `app`, `applecanvas`, `artistvideo`, `assets`, `betterlyrics`, `canvas`, `gradle`, `innertube`, `jiosaavn`, `kizzy`, `kugou`, `lastfm`, `lrclib`, `paxsenixlyrics`, `scripts`, `shazamkit`, `simpmusic`, `spine`, `spotify`, `vivimusiccanvas`, `youlyplus`. (`.codegraph` may be a code index the agent can use `[ASSUMPTION]`.)

---

## 3. WHAT IS KNOWN ABOUT CONVX r52

### 3.1 Stack `[VERIFIED-README]`
| Area | Facts |
|---|---|
| UI | Jetpack Compose, MVVM (`ui/screens` + `viewmodels`), nav in `ui/screens/NavigationBuilder.kt` |
| Liquid Glass | `ui/component/GlassEffect.kt` exposes `Modifier.liquidGlass(...)`; built on a **vendored source copy** of Kyant0/backdrop in `ui/component/backdrop/`. A `Backdrop` (usually `rememberLayerBackdrop()` attached with `Modifier.layerBackdrop(...)` to a subtree) captures real pixels; any surface holding that backdrop samples/blurs/refracts it via `drawBackdrop(...)`. Floating nav bar, circular back/share buttons, and sheets are all glass surfaces sampling a nearby backdrop. |
| Playback | Media3 `ExoPlayer` service in `playback/MusicService.kt` |
| Data | Room (`db/`), DataStore (`utils/DataStore.kt`, `constants/PreferenceKeys.kt`) |
| YouTube Music | `innertube` module = unofficial InnerTube client, separate from app module |
| Updater | `vivimusic/updater/` checks GitHub Releases, installs APK |

### 3.2 Features to reproduce `[VERIFIED-README + releases]`
Liquid Glass chrome (real backdrop blur/refraction), iOS-style rubber-band overscroll, blurred page transitions, springy nav "puck", adaptive colors from artwork, full YT Music catalog, offline downloads, lossless/high-quality + equalizer, synced karaoke lyrics with word-by-word highlight, Discord Rich Presence, Listen Together (disabled in 1.5.1; r52 status unknown), zero telemetry, built-in updater.
From releases: floating pill nav bar with inline search bar; collapsible tablet sidebar with its own glass tuning; iPad-style capped-width mini player; compact expanded-player option for wide screens; mini-player waveform seek bar; multi-icon mini bar; Apple-Music-style Home cards; DIY player editor (stickers, custom icons, shareable preset files); Auto-DJ BPM crossfade + creative transitions; ambient mode; local-only mode with local Home shelves; fast-scroll rail on 14 screens; scan screen; searchable Settings; Low-data mode; ListenBrainz; multi-account/channel switch; bundled fonts (Google Sans, Sans Flex, Outfit, Plus Jakarta Sans); Lucide-based SF-Symbols-style icon set; Home sections hide/reorder; overlay long-press menus; Client probe tool.

### 3.3 r52-specific behaviors that must be ported exactly `[VERIFIED-R52]`
1. **Player V2 (Apple Music–style):** mini pill *morphs* into the full sheet — a rounded surface grows from the pill's exact bounds to the window; corner radius travels from capsule to the window radius. (On desktop: window corner radius = 0 / Win11 rounded; log the choice.)
2. **Motion retune:** critically damped springs + `FastOutSlowInEasing` everywhere; iOS rubber-band overscroll (Apple's **0.55** constant, damped release spring, real fling handoff); bounce-back natural period ≈ **0.4 s**, settles in 300–500 ms; tile morph uses scale-crop (`ResizeMode.ScaleToBounds`) not relayout; nav puck follows live selection (no spring lag), colour animates.
3. **Backdrop re-record policy:** no re-record every frame during navigation transitions; re-records throttled to a budget window.
4. **Sharp artwork:** art requested at real on-screen pixel size (not fixed 96×96).
5. **Long-press menus:** overlay by default with a *dim* (no blur) behind; sheet style optional.
6. **Playback fallback chain:** VISIONOS first, IOS second, then others; lazy signature-timestamp fetch; `visitorData` rotation on retry for signed-in and guest; **Client probe** tool (Settings → Content → Logs → Client probe).
7. **Home video background** stops when app is backgrounded (desktop: when minimized/occluded).
8. LRU caches for online playlists and list-row lookups; `contentType` on every list.

---

## 4. STRATEGY DECISION

**Chosen: Kotlin Compose Multiplatform for Desktop (JVM, Skia), copy-port inside a fork.**

Why:
- Convx UI is Compose; Compose Multiplatform shares most of the Jetpack Compose API and targets Windows/macOS/Linux `[VERIFIED-LIB]`.
- **Kyant0/backdrop became a Compose Multiplatform library in 2.0.0** (JVM among targets; latest seen: 2.0.1) `[VERIFIED-LIB]`; it adds a common `RuntimeShader` interface for custom shader effects `[VERIFIED-LIB]`. **Correction (docs/06 C1):** Convx does not use that artifact; it uses a *vendored, modified, Android-only* copy with its own extras (`frozen`, `loopBucket`, `backdropScale`...). Spike S1 therefore compares porting Convx's own backdrop code to Skia (G1) against official 2.x + re-implemented extras (G2).
- InnerTube client, lyrics providers, scrobblers, Discord RPC are likely plain Kotlin/JVM → reusable `[ASSUMPTION]`.

Rejected: Electron/Tauri/Flutter rewrites (would not reuse Convx code; fidelity risk violates F1/F2).

**Known risk to prove in Phase 2 (Spike S1):** an upstream Kotlin Slack thread notes that on desktop the backdrop must come from *in-app Compose content* (which is Convx's case: everything is Compose). A true "see the Windows desktop behind the window" glass is **not** in scope — Convx does not do that either `[ASSUMPTION — confirm]`.

### 4.1 Repo layout (copy-port)
```
convx-desktop/                (fork @ nightly-r52)
├─ app/ innertube/ ...        ← UNTOUCHED Android oracle
├─ desktopApp/                ← NEW: Compose Desktop entry, window, tray, packaging
├─ desktop-core/              ← NEW: ported UI + viewmodels + adapters (copy-port, same package names)
├─ docs/                      ← this kit + PROGRESS, DECISIONS, DEVIATIONS, PORT_MANIFEST, evidence/
├─ reference/                 ← user's screenshots/recordings (docs/02)
└─ scripts/
```
- Pure Kotlin modules (`innertube`, `lrclib`, `kugou`, `betterlyrics`, `simpmusic`, `youlyplus`, `paxsenixlyrics`, `lastfm`, `kizzy`, …): if Android-free, depend on them directly; if they are `com.android.library` with few Android calls, convert to `kotlin("jvm")`/KMP **in the fork** with minimal diffs `[ASSUMPTION]`.
- `PORT_MANIFEST.md`: one row per source file: `path | status (COPIED/ADAPTED/REPLACED/SKIPPED) | adaptations | tests`.

---

## 5. PHASES (each ends with an EXIT GATE — do not proceed until green)

### Phase 0 — Environment & pin
Install: JDK 21 (with `jpackage`), Gradle wrapper from repo, Android SDK (to build the oracle APK), Windows 10/11 dev machine, **WiX Toolset 3.x** (needed for `.exe` packaging; verify against current Compose Multiplatform docs). Fork, checkout `nightly-r52`, build the Android app once.
**Gate:** Android r52 builds; commit hash recorded; `./gradlew :desktopApp:run` shows an empty Compose window.

### Phase 1 — Deep audit (no feature code)
Fill all templates in `docs/01_AUDIT_TEMPLATES.md` into `docs/audit/`:
1. Module map + build files (`build.gradle.kts`, `settings.gradle.kts`, versions).
2. **Android-API inventory** per module/file (classify: PURE / PORTABLE-WITH-ADAPTER / ANDROID-ONLY).
3. Design-token extraction: all glass parameters (blur, vibrancy, lens heights/amounts, tint, highlight, shadow, shapes/corner radii), colors, typography, spacing, icon set, motion curves/spring specs — each with source `file:line`.
4. Screen & user-flow map: every route in `NavigationBuilder.kt`, its entry points, back stack behavior, transitions.
5. System-flow map: play request → client chain → stream URL → player → queue → notification/media session → persistence.
6. Settings inventory (every `PreferenceKeys` entry, default, screen).
**Gate:** all audit files complete; `PORT_MANIFEST.md` lists every file; user reviews the P0/P1/P2 list (§6).

### Phase 2 — Feasibility spikes (throw-away code in `spikes/`, results in `docs/spikes/`)
| ID | Question | Pass criteria |
|---|---|---|
| S1 | Which route reproduces Convx glass on desktop: G1 (port Convx's vendored backdrop to Skia) or G2 (official backdrop 2.x + Convx extras)? | Nav-bar-like pill with `colorControls + blur + lens + rim` over scrolling artwork grid, ≥60 fps on a mid GPU, matches reference capture and the numbers in docs/06 §3, **0 backdrop re-captures at idle** |
| S2 | Does the InnerTube client run on JVM and resolve streams with the r52 chain (VISIONOS → IOS → …)? | Search "x", resolve a playable stream URL, in guest mode, repeatedly (20 tries) |
| S3 | Which audio engine plays YouTube streams (Opus/WebM, AAC/MP4) with seek, volume, gapless/crossfade? | Candidates: **vlcj/libVLC bundled**, libmpv, FFmpeg+Java sound. Must: seek <300 ms, no gaps, stable 2 h, **plus** the extra criteria in docs/06 §5 (two concurrent streams for crossfade, speed/pitch, stall detection, normalization, sleep-timer fade). Pick one, log in `DECISIONS.md` `[ASSUMPTION: JavaFX Media likely can't decode WebM/Opus]` |
| S4 | Do Room-KMP (bundled SQLite) and DataStore-KMP open Convx's schema/keys on desktop? | Reads/writes pass; migrations (schema ≥36 referenced in 1.3 notes) succeed |
| S5 | Account login on desktop (Android uses an in-app web login `[ASSUMPTION]`) | Embedded browser (JCEF/WebView) or external-browser cookie flow obtains a working session; no credentials stored in plain text |
| S6 | JS engine (Convx uses `QuickJsExecutor`; r52 keeps one persistent engine across calls) | JVM QuickJS-compatible engine runs the cipher/n-param scripts with persistent state |
| S7 | PoToken generation on desktop (Android `PoTokenGenerator` likely WebView-based `[ASSUMPTION]`) | Works via embedded browser/JCEF, or documented as unsupported with fallback chain only |
**Gate:** every spike has a PASS or an approved fallback in `DECISIONS.md`.

### Phase 3 — Foundation
- `desktopApp`: window (resizable, min size from audit of tablet layouts), title bar, DPI scaling, app icon, single-instance lock.
- `desktop-core`: theme/tokens ported verbatim; fonts bundled (Google Sans, Sans Flex, Outfit, Plus Jakarta Sans — check font licenses; log) `[VERIFIED-R52 names]`; icon set (Lucide-derived) as vector resources.
- **Glass layer:** reimplement internals of `Modifier.liquidGlass(...)` on top of official backdrop 2.0.x **while keeping the exact public signature of Convx's `GlassEffect.kt`** so all ported screens compile unchanged. Note: backdrop changed API between 1.0 and 2.x (e.g., `refraction`+`dispersion` merged into `lens`; Android-specific effects removed in 2.0.0) `[VERIFIED-LIB]` — the vendored copy in `ui/component/backdrop/` is therefore **not** directly reusable on desktop; map each call.
- Navigation shell, DI wiring, DataStore/Room, logging with a Settings → Logs screen (to feed Client probe and bug reports).
**Gate:** shell with glass nav bar + puck + glass buttons over a placeholder scrolling backdrop matches reference captures within tolerance (`docs/03` §3).

### Phase 4 — Vertical slices (order matters; one slice = port + tests + evidence)
1. **App shell:** layout, nav bar (pill, puck, inline search), tablet-sidebar mode.
   *Desktop layout rule:* use Convx's own tablet/wide layouts (collapsible sidebar, capped-width mini player, compact expanded player) as the desktop layouts `[VERIFIED-R52 features]`. Do not design new desktop layouts.
2. **Home** (Apple Music-style cards, hide/reorder sections, background image/GIF, video background).
3. **Search** (inline bar, results, suggestions).
4. **Playback core:** engine adapter (`PlayerEngine` interface), queue, repeat/shuffle, client chain, retries, visitorData rotation, Client probe.
5. **Mini player + Player V2 + pill→sheet morph**, waveform seek bar, quality/codec pill, Up Next redesign.
6. **Lyrics:** providers, synced word-by-word renderer, full-screen lyrics, inline lyrics.
7. **Library:** liked/offline/top, playlists, albums, artists, hero pages (artist/playlist/album), fast-scroll rail, pull-to-refresh equivalent.
8. **Settings:** entire tree incl. *Liquid Glass* (copy-on-enable, nav-vs-mini dials, per-element overrides), Appearance, Player, Content/Logs, searchable settings.
9. **Menus & sheets** (overlay default), dialogs, share.
10. **Downloads/offline** + storage management.
11. **Local-only mode** (Home shelves, scan screen, audio properties) — Windows folder scanner replaces MediaStore.
12. **Extras (P1/P2):** Discord RPC (kizzy), ListenBrainz/Last.fm, Ambient mode, DIY editor + presets (**file format must stay byte-compatible with Android presets**), Auto-DJ, EQ, crossfade, accounts/channel switch, updater (desktop variant).

### Phase 5 — Desktop adaptation layer (only §1-F3 items)
Keyboard shortcuts (Space, ←/→, ↑/↓, M, L for lyrics, Ctrl+F search, Esc closes player) — **list shown to user for approval before implementing**; mouse wheel/trackpad scroll feel (rubber-band behavior with discrete wheel steps), right-click = long-press menu, hover states (minimal, consistent with Convx style), system tray, Windows media keys + SMTC (System Media Transport Controls) via native bridge `[ASSUMPTION: needs JNA/WinRT bridge]`, single-instance, startup/close-to-tray options, DPI 100–200%, multi-monitor.

### Phase 6 — Parity test & retest loop → `docs/03_TEST_PLAN.md`
### Phase 7 — **PREVIEW GATE** → `docs/04_PREVIEW_AND_RELEASE.md` (STOP; wait for the user)
### Phase 8 — Release build (`.exe`) only after `APPROVED.txt`

---

## 6. FEATURE TIERS (to be confirmed after Phase 1)
| Tier | Features |
|---|---|
| **P0 — needed for the preview** | Glass system, nav shell, Home, Search, Library basics, mini player, Player V2 + morph, queue/Up Next, playback + fallback chain, synced lyrics, artwork-adaptive colors, core Settings (Liquid Glass/Appearance/Player), local DB |
| **P1 — before final .exe if the user approves** | Account login/sync, downloads/offline, crossfade/EQ, Discord RPC, ListenBrainz/Last.fm, DIY editor + presets, local-only mode, Ambient mode, Home video background, updater, sleep timer, audio normalization, silence skipping, speed/pitch |
| **P2 — optional/after** | Auto-DJ BPM mixing + creative transitions, AI playlist editing, Listen Together (disabled in 1.5.1; r52 unknown), Windows SMTC polish |
| **Excluded / deferred (docs/06 C7)** | **TIDAL intercept: excluded** (pulls paid lossless streams via third-party proxies). JioSaavn and Spine stream intercepts: deferred, only with explicit written user instruction recorded in DECISIONS.md |
| **Dropped (no desktop equivalent)** | Android Auto, set-as-ringtone, APK installer, Material-You system wallpaper colors (replace with artwork colors only), ShazamKit `[ASSUMPTION: Android SDK]`, tag-editor chooser (replace with "Show in Explorer") |

---

## 7. ANDROID → DESKTOP MAPPING `[ASSUMPTION — Phase 1 confirms each row]`
| Android piece | Desktop replacement | Notes |
|---|---|---|
| Media3 ExoPlayer + `MusicService` | `PlayerEngine` interface + engine from S3 (vlcj/libVLC preferred) | Keep queue/crossfade logic in common code |
| Media notification/lock screen | Windows SMTC + tray + media keys | Phase 5 |
| Room | Room KMP (bundled SQLite) | Keep entity/DAO names & schema |
| DataStore | DataStore KMP (same `PreferenceKeys`) | Preserve keys → preset/backups compatible |
| Coil + hardware bitmaps | Coil 3 (KMP) / Skia images | Keep "size art from real pixels" rule |
| MediaStore (local music) | Folder scanner + tag reader (e.g., JAudiotagger) | Scan screen UI unchanged |
| WebView login | JCEF / external browser flow (S5) | Never store passwords |
| Material You dynamic color | Keep artwork-derived palette (e.g., materialkolor KMP) | |
| Android `Activity`/navigation | Same Compose nav graph in a Window | |
| Hilt DI | Koin / kotlin-inject / manual wiring | docs/06 C2 |
| `MediaLibrarySessionCallback` (Android Auto/BT) | Windows SMTC + media keys; custom like/radio commands become in-app actions | |
| `LoudnessEnhancer`, Sonic speed/pitch, silence-skip processors | engine-level or custom DSP | S3 |
| Haptics | Drop | log |
| Foreground service/WorkManager | Coroutine scopes + app lifetime / tray | |
| APK updater | Desktop updater reading GitHub Releases of the *desktop* repo | opt-in only |
| JS engine (Spine) | S6 result | |
| Equalizer (Android AudioEffect) | engine-level EQ (libVLC has one) or DSP | P1 |
| BPM detection (Auto-DJ) | JVM audio decode + same algorithm | P2 |

---

## 8. LIQUID GLASS PORTING NOTES
1. Mechanics `[VERIFIED-README]`: capture subtree into a layer backdrop (`rememberLayerBackdrop()` + `Modifier.layerBackdrop(...)`), glass surfaces call `drawBackdrop(...)` with effects (`blur`, `vibrancy`, `lens`, highlight, shadow) `[VERIFIED-LIB]`.
2. In Convx, *where* the backdrop is attached (which subtree) defines what the glass can see. Extract the exact attach points per screen in Phase 1 (they are layout decisions: F1).
3. Keep the **throttled re-record policy** (r52). Measure GPU cost on desktop (Skia) in S1.
4. Shaders: backdrop 2.x exposes a common runtime-shader interface; Skia uses SkSL on desktop `[VERIFIED-LIB]`. Do not hand-write replacement shaders unless S1 proves a gap; if a gap exists, log in `DECISIONS.md` and keep visual output matched to `reference/`.
5. Android below API 31/33 degrades gracefully (blur/AGSL) `[VERIFIED-LIB]` — desktop has no such tiers; still implement a "reduced glass" fallback for software-rendered/very weak GPUs (setting, off by default).
6. Match all dials in *Settings → Liquid Glass* (blur/vibrancy/lens, per-element color/surface/text, puck blur vs transparent, glass type).

---

## 9. DATA, NETWORK & RELIABILITY
- Reproduce the r52 client chain order, retry rules, and `visitorData` rotation exactly; port **Client probe** UI and logs.
- Expect bot-detection failures ("Sign in to confirm you're not a bot") — r52 notes show most clients were being flagged `[VERIFIED-R52]`. Playback depends on an unofficial API that can break without notice; the desktop app inherits this. Surface errors clearly; keep probe/logs.
- No telemetry (matches Convx) `[VERIFIED-README]`. No analytics libraries.
- Stream URL expiry: refresh logic (see v1.5.2 "downloaded stream links expiring" fix) `[VERIFIED-R52]`.
- Respect ToS/legal notes in §1-F8; add a first-run disclaimer screen reusing Convx's disclaimer wording.

---

## 10. PACKAGING (used only in Phase 8)
- Compose Gradle plugin `nativeDistributions`: `targetFormats(TargetFormat.Exe, TargetFormat.Msi)`, `packageName = "Convx"`, version from a **single source** (r52 moved Android versioning to one source `[VERIFIED-R52]`), `.ico` icon, `modules("java.sql", "jdk.unsupported", ...)` as required by dependencies, bundled native libs for the audio engine, `windows { menuGroup, shortcut = true, dirChooser = true, upgradeUuid }`.
- Tasks to use (verify names against your plugin version): `:desktopApp:run`, `:desktopApp:createDistributable`, `:desktopApp:runDistributable`, `:desktopApp:packageExe`, `:desktopApp:packageMsi`.
- Code signing: optional; unsigned `.exe` triggers Windows SmartScreen. Document it for the user.
- Installer size and cold-start time recorded in `docs/evidence/release/`.

---

## 11. DEFINITION OF DONE (for the preview, then for the .exe)
**Preview-ready:** all P0 slices pass tests (docs/03), parity checklist ≥ 95% items ticked with the remaining ones listed honestly, no open P0/P1 bugs, `reference/` comparisons attached, `docs/DEVIATIONS.md` complete.
**EXE-ready:** user wrote `APPROVED FOR EXE` in `APPROVED.txt`; P1 scope agreed; 3 consecutive green full-suite runs; clean-VM install/uninstall tested; credits/licenses screen present; installer built via `scripts/package-exe.ps1`.

---

## 12. OPEN QUESTIONS FOR THE USER (agent asks at Phase 1 gate, not before)
1. Which P1 features are mandatory for v1.0 of the desktop app?
2. Is an unsigned installer acceptable?
3. Keyboard shortcut list approval (Phase 5).
4. Will you provide the reference captures in `docs/02`? (Without them the agent must rely on source only and visual parity can't be verified.)
