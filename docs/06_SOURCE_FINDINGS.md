# 06 — SOURCE FINDINGS & PLAN CORRECTIONS (v1.1 addendum)

**Where this wins:** if anything here contradicts sections 3–9 of `CONVX_DESKTOP_IMPLEMENTATION.md`, this file wins. Phase 1 must still re-verify everything against the real repo.

## 1. How this was obtained (and its limits)
- `github.com/.../tree/main/app` is **still blocked** for automated readers (robots). Retrying did not change that.
- Two routes DID work: (a) **DeepWiki** (`deepwiki.com/cosmictaserdev-creator/Convx/...`) — an AI-generated wiki that cites real file/line ranges; (b) **GitHub blob pages at a pinned commit** (`.../blob/a03a19df/<path>`), which gave the full text of `GlassEffect.kt` (529 lines).
- **Snapshot caveat:** both reflect commit **`a03a19df`** (roughly the v1.5.x era, before r52's "20 commits since v1.5.2"). They do NOT include r52's Player V2 morph, motion retune, VISIONOS-first chain, Local Home, etc. The agent must run `git diff a03a19df..1e2237d9` (if both commits exist in history) and treat the diff as the r52 delta.
- Read so far: `GlassEffect.kt` in full; DeepWiki pages: Overview, Liquid Glass (4.1), Nav Shell (4.2), MusicService (3.1), Content Resolution (3.2). **Not yet read:** the other DeepWiki pages and ~all other source files (list in §8).
- DeepWiki is machine-written; code in the repo outranks it.

## 2. Corrections to plan v1.0
| # | v1.0 said | Reality (source) | Consequence |
|---|---|---|---|
| C1 | Glass sits on Kyant backdrop; swap internals for official 2.x | Convx uses a **vendored, modified, Android-only** copy (`ui/component/backdrop/...`) built on `RenderEffect`/`RenderNode`/runtime shader, plus Convx-specific additions: `frozen`, `loopBucket`, `backdropScale`, `onDrawSurface`, `highlight`, `shadow`, `colorControls` | S1 must compare **G1** (port Convx's own backdrop code to Skia: blur/colour-matrix/runtime-shader lens) vs **G2** (official backdrop 2.x + re-implement Convx extras). Pick by visual match + frame time, log in `DECISIONS.md`. `Modifier.liquidGlass(...)` public signature stays identical either way. |
| C2 | DI unspecified | **Hilt** DI (DeepWiki overview) | Hilt is Android-only → desktop uses Koin / kotlin-inject / manual wiring. ViewModels via KMP lifecycle-viewmodel. `[ASSUMPTION: Hilt confirmed in build files — verify]` |
| C3 | Playback = "play a stream" | `MusicService : MediaLibraryService`, **two ExoPlayers** (`player` + `nextPlayer` pre-buffer), crossfade by **volume ramp** (keys `CrossfadeEnabledKey`, `CrossfadeDurationKey`, `CrossfadeGaplessKey`; trigger `position >= duration - fade`), audio processors (`SilenceSkippingAudioProcessor`, `LosslessStallWatchdogAudioProcessor`, `SonicAudioProcessor` for speed/pitch), `LoudnessEnhancer` normalization (`AudioNormalizationKey`), **sleep timer with fade-out**, queue persisted in DataStore (`PersistentQueueKey`, `RememberShuffleAndRepeatKey`) | **S3 pass criteria expanded** (see §5). Desktop engine must support: two simultaneous streams + per-stream volume, speed/pitch, silence skipping, normalization gain, stall detection, precise position. |
| C4 | Client chain generic | `YTPlayerUtils`: `MAIN_CLIENT = ANDROID_VR_1_43_32`, `METADATA_CLIENT = WEB_REMIX`, `STREAM_FALLBACK_CLIENTS` (incl. `TVHTML5_SIMPLY_EMBEDDED_PLAYER`), `BotDetectionMitigator`, `PoTokenGenerator`, `CipherDeobfuscator` (n-param), custom DNS by `IpVersion` pref, proxy selector, own OkHttp client. **r52 puts VISIONOS first, IOS second** | Port the **r52** order; keep the roles (stream client vs metadata client so signed-in history works). |
| C5 | JS engine unspecified | Spine uses **`QuickJsExecutor`** | S6: find a JVM QuickJS binding (or equivalent) and keep a persistent engine as r52 does. |
| C6 | PoToken unspecified | `PoTokenGenerator` exists. On Android such generators typically run a **WebView/BotGuard** script `[ASSUMPTION — verify]` | **New spike S7:** desktop PoToken generation (embedded browser/JCEF or equivalent). If not feasible, document that the desktop client relies on the fallback chain only. |
| C7 | Tidal/JioSaavn/Spine are "P1" | `TidalService` fetches lossless FLAC through **public third-party "hifi-api" proxies** with an instance-discovery mechanism, i.e., it gets TIDAL's paid catalog without a TIDAL subscription. JioSaavn goes through an unofficial "Melo" API wrapper; Spine runs external JS modules that resolve "proprietary stream URLs" | **TIDAL is excluded from this plan** — I won't specify it, and the agent must not build it. JioSaavn and Spine intercepts are **deferred**: not built unless the user explicitly instructs it in writing and the agent records that in `DECISIONS.md`. The YouTube path is unaffected. |
| C8 | Window-rounded corners etc. | `isGlassAllowed() = SDK>=31 && !isLowRamDevice()`; below that → translucent-tint fallback (not black) | Desktop: always allowed; keep the three **GlassStyle** tiers (LIQUID / BLUR / TRANSPARENT) and a "reduced glass" auto-fallback for weak GPUs. |

## 3. Glass — real values from `GlassEffect.kt` @ a03a19df  (tokens for docs/audit/design-tokens.md; **re-check against r52**)
| Item | Value |
|---|---|
| `GlassEffectConfig` defaults | `globalEnabled=true`, `vibrancy=1.2`, `blurRadius=2` (dp), `lensHeight=0.4`, `lensAmount=0.6`, `chromaticAberration=false`, `depthEffect=false`, `surfaceTintColor=0xFF1A1A1A`, `highlightColor=Unspecified (white)`, `highlightOpacity=0.55`, `style=LIQUID`, `puckColor=Unspecified`, `puckOpacity=0.8`, `surfaceOpacity=0.5`, `textColor=White`, per-component switches (`player`, `miniPlayer`, `navBar`, `sidePanel`) |
| Side panel | Own tuning: `sidePanelVibrancy=1.2`, `BlurRadius=2`, `LensHeight=0.4`, `LensAmount=0.6`, `SurfaceOpacity=0.5`, `TextColor=White`, `Color=Unspecified`; `forSidePanel()` swaps them in |
| Lens scale | `LENS_MAX_DP = 48` → px = `(config.lens* × 48).dp × resolutionScale` |
| Saturation | `glassSaturation(v) = 1 + 0.5 × clamp(v, 0..2)` (vibrancy 1 → ×1.5) |
| Player blur | `PLAYER_BLUR_MULTIPLIER = 4` (full-screen player ≈ 4× pill blur); edge effects (lens/rim/shadow) **off** for large surfaces (`applyEdgeEffects=false`) |
| Resolution ramp | `MIN_GLASS_RESOLUTION_SCALE = 0.30`, `FULL_QUALITY_BLUR_DP = 8`; `scale = 1 − clamp(blur/8,0,1) × 0.70`; pixel params pre-multiplied by scale; scale clamped to [0.05, 1]. *Do not flatten to a constant — the source documents a failed experiment.* |
| Effect chain order | `colorControls(saturation)` (if ≠1) → `blur` (if >0) → `lens(refractionHeight, refractionAmount, depthEffect, chromaticAberration)` (if LIQUID, edge effects on, and either lens value >0; Android gates on API≥33) |
| Rim (highlight) | width **0.8 dp**, default alpha **0.55**, `rimAlpha = highlightAlpha × (highlightOpacity/0.55)`, angle **frozen at 45°** (range 25–65, drift animation deliberately disabled for idle-power reasons — port the frozen state, not the drift) |
| Shadow | `Shadow.Default` when edge effects on |
| Adaptive tint | if no user colour: light theme `0xFFFAFAFA`, dark theme `0xFF4A4A4E`; tint drawn at `surfaceOpacity` via `onDrawSurface` |
| Fallback path | `shouldUseTranslucentGlassFallback(style, renderEffectSupported)` = `style==TRANSPARENT || !supported` → `clip(shape).background(tint @ surfaceOpacity)`; pure function, has a unit test (`GlassEffectTest.kt`) — port the test |
| Shape constraint | `CornerBasedShape` only (lens throws otherwise) |
| Content colour | `glassContentColorFor(behind, tint, opacity)` — composite tint over backdrop, pick `0xFF1A1A1A` if luminance>0.5 else white |
| Stable lambdas | every effect/highlight/shadow/surface lambda is `remember`ed on the exact values it reads; **required** (fresh lambdas caused full-screen re-capture each recomposition). Keep this discipline in the port and add a test/trace that counts re-captures. |
| Locals | `LocalGlassEffectConfig`, `LocalAppBackdrop` (app UI minus glass surfaces, recorded to a `GraphicsLayer` attached at the **root layout in `MainActivity`**), `LocalBackdropLoopBucket` (pool one processed layer per loop bucket for looping canvas video), `LocalAppleMusicUi` |
| Freeze | `frozen = { true }` during heavy ops (scroll/navigation/FloatingTabBar state transitions) → hold last capture; `rememberBackdropFreeze` in `MainActivity` |
| GlassSwitch | Deliberately **not** backdrop-based: simplified glass + white border for rim (perf on settings screens) |
| Settings screen | `GlassEffectSettings.kt` has a live preview sandbox |

## 4. Navigation shell tokens (DeepWiki 4.2 @ a03a19df; verify)
- `FloatingTabBar` `BarState`: `INLINE` (collapsed pill while scrolling down), `EXPANDED` (full tabs + search circle), `SEARCH_EXPANDED` (tabs shrink to one "current screen" icon; search circle becomes wide bar).
- Puck: spring-damped via `DampedDragAnimation`; `InteractiveHighlight` (finger-tracking glow → on desktop: **pointer-tracking** glow); lens refraction + accent-tinted glass sample of the selected icon; `PuckRestHighlightAlpha = 0.5`, `PuckRestShadowAlpha = 0.35`.
- Gooey transition INLINE↔EXPANDED: `GooeyPeakBlur = 12.dp`, `GooeyDurationMs = 300`.
- Geometry: `NavBarSearchBarHeight = 48.dp` (= mini-player collapsed height), `NavBarStandaloneReserve = 80.dp`, `NavBarMinTabWidth = 56.dp`.
- `NavSearchState` hoisted in `MainActivity`, exposed via `LocalNavSearchState` (nav bar reacts to search input even before the Search screen is mounted).
- Wide screens: `AppNavigationRail` / `FloatingSideBar`; `rememberStickySelectedRoute` keeps the puck on the parent tab inside detail screens.
- `KeyboardOpenDelayMs = 260` exists for the Android IME → **desktop deviation: no IME delay** (log in DEVIATIONS).
- Backdrop for the bar: `rememberLayerBackdrop` inside `FloatingTabBar`, plus the app backdrop from `MainActivity`.

## 5. Updated spike criteria
- **S1 (glass):** run both G1 and G2; compare to `reference/` captures and to §3 numbers; measure re-capture count (must be 0 at idle) and frame time while scrolling.
- **S3 (audio):** must additionally prove: two concurrent streams with independent volume ramps (crossfade), playback-rate change with pitch correction, gap < 50 ms between tracks in gapless mode, stall detection (position advancing without samples), normalization gain, sleep-timer fade-out, position accuracy ±50 ms (for word-synced lyrics).
- **S6 (JS):** QuickJS-compatible engine with persistent context across calls.
- **S7 (PoToken):** see C6.

## 6. Android-only items confirmed in source/wiki (feed docs/audit/android-api.md)
`android.os.Build`, `ActivityManager.isLowRamDevice`, `RenderEffect`/`RenderNode`, `LocalContext`; `MediaStore` scan in `LocalAudioScanner` (`MIN_DURATION_MS = 15 s`, excluded folders); `RingtoneHelper`/ringtone dialogs; Media3 `SimpleCache` + `ResolvingDataSource` in `DownloadUtil`; `LoudnessEnhancer`; `MediaLibrarySessionCallback` (custom commands `CommandToggleLike`, `CommandToggleStartRadio`, `CommandToggleLibrary`; browsable root for Android Auto/Bluetooth); `UpdateDownloadWorker` (APK updater); foreground service/notification; `AndroidManifest.xml`.

## 7. Plan changes triggered by these findings
1. Master plan §6 tiers: remove "Spine/JioSaavn" from P1; add "Deferred (needs written user instruction)"; TIDAL excluded.
2. Add **sleep timer, audio normalization, silence skipping, speed/pitch** to the P1 playback list (they exist in Convx).
3. Port `GlassEffectTest.kt` and any other unit tests in `app/src/test` first — they are free regression tests.
4. Preserve DataStore key names and the queue-persistence format.
5. Re-read r52 delta before porting Player UI (V2 morph) and motion: this snapshot predates them.

## 8. Phase-1 reading list (agent reads ALL before coding)
Base for blob pages (works): `https://github.com/cosmictaserdev-creator/Convx/blob/a03a19df/<path>` — and switch to `1e2237d9` (r52) via `git` locally. Under `app/src/main/kotlin/com/convx/music/`:
`ui/component/GlassEffect.kt` ✔ read · `ui/component/GlassSwitch.kt` · `ui/component/backdrop/DrawBackdropModifier.kt` · `ui/component/backdrop/backdrops/LayerBackdrop.kt` · `.../LayerBackdropModifier.kt` · `ui/component/backdrop/catalog/components/LiquidBottomTab.kt`, `LiquidBottomTabs.kt`, `LiquidSlider.kt` · `ui/component/floatingtabbar/FloatingTabBar.kt` · `ui/component/FloatingNavBar.kt` · `ui/component/AppNavigation.kt` · `ui/component/NavSearchState.kt` · `MainActivity.kt` · `App.kt` · `constants/Dimensions.kt` · `constants/PreferenceKeys.kt` · `ui/screens/NavigationBuilder.kt` · `ui/player/MiniPlayer.kt`, `Queue.kt`, `Thumbnail.kt` · `ui/component/Lyrics.kt` · `lyrics/LyricsUtils.kt` · `ui/screens/settings/GlassEffectSettings.kt` · `playback/MusicService.kt`, `MediaLibrarySessionCallback.kt`, `DownloadUtil.kt`, `audio/LosslessStallWatchdogAudioProcessor.kt` · `utils/YTPlayerUtils.kt`, `LocalAudioScanner.kt` · test: `app/src/test/.../GlassEffectTest.kt`.
DeepWiki pages to read fully: 2.1 Room · 2.2 DataStore · 2.3 Navigation · 3.3 Auto-DJ · 3.4 Queue/Downloads · 4.3 Player UI · 4.4 Theming · 5.x Screens · 6.x InnerTube · 7.x Lyrics · 8.x Canvas · 9.1 Kizzy · 9.2 Last.fm/ListenBrainz · 9.4 Spine (read-only, for understanding; see C7) · 10.x Listen Together · 11.x Settings · 13 Glossary.
