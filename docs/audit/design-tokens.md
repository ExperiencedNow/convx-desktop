# Design Tokens Inventory (Phase 1 Audit)

Extracted from Convx r52 source code with exact file and line references.

## C1. Liquid Glass Tokens (`app/.../ui/component/GlassEffect.kt:46-89` & `06_SOURCE_FINDINGS.md §3`)

| Parameter | Default Value | Tunable via Preference? | Notes / Source |
|---|---|---|---|
| `globalEnabled` | `true` | Yes (`LocalGlassEffectConfig`) | `GlassEffect.kt:47` |
| `vibrancy` | `1.2f` | Yes (`glassSaturation = 1 + 0.5 * clamp(v, 0..2)`) | `GlassEffect.kt:48`, Saturation multiplier |
| `blurRadius` | `2.dp` (2f) | Yes | `GlassEffect.kt:50`, Pill blur |
| `lensHeight` | `0.4f` (40%) | Yes | `GlassEffect.kt:52`, Maps to `0.4 * 48.dp = 19.2.dp` |
| `lensAmount` | `0.6f` (60%) | Yes | `GlassEffect.kt:54`, Maps to `0.6 * 48.dp = 28.8.dp` |
| `LENS_MAX_DP` | `48.dp` | Constant | Max scale limit for lens refraction |
| `chromaticAberration` | `false` | Yes | `GlassEffect.kt:55` |
| `depthEffect` | `false` | Yes | `GlassEffect.kt:56` |
| `surfaceTintColor` | `Color(0xFF1A1A1A)` | Yes | Dark grey default, adaptive: light `0xFFFAFAFA`, dark `0xFF4A4A4E` |
| `highlightColor` | `Color.Unspecified` (White) | Yes | Specular rim tint |
| `highlightOpacity` | `0.55f` | Yes | Rim alpha default |
| `highlightWidth` | `0.8.dp` | Constant | Rim highlight width |
| `highlightAngle` | `45°` (frozen) | Constant | Drift animation intentionally disabled for idle perf |
| `style` | `GlassStyle.LIQUID` | Yes (`LIQUID` / `BLUR` / `TRANSPARENT`) | Fallback: `TRANSPARENT` or when unsupported |
| `puckColor` | `Color.Unspecified` | Yes | Selection puck wash (adapts to theme) |
| `puckOpacity` | `0.8f` | Yes | Relaxes toward clear while pressed |
| `surfaceOpacity` | `0.5f` (50%) | Yes | Alpha of surface tint |
| `textColor` | `Color.White` | Yes | Contrast helper switches to dark if luminance > 0.5 |
| `playerEnabled` | `true` | Yes | Component toggle |
| `miniPlayerEnabled` | `true` | Yes | Component toggle |
| `navBarEnabled` | `true` | Yes | Component toggle |
| `sidePanelEnabled` | `true` | Yes | Tablet sidebar toggle |
| `sidePanelVibrancy` | `1.2f` | Yes | Independent tuning for wide screens |
| `sidePanelBlurRadius` | `2.dp` | Yes | Independent tuning |
| `sidePanelLensHeight` | `0.4f` | Yes | Independent tuning |
| `sidePanelLensAmount` | `0.6f` | Yes | Independent tuning |
| `sidePanelSurfaceOpacity` | `0.5f` | Yes | Independent tuning |
| `PLAYER_BLUR_MULTIPLIER` | `4` | Constant | Full player blur ≈ 4× pill blur; edge effects OFF |
| `MIN_GLASS_RESOLUTION_SCALE` | `0.30f` | Constant | Lower bound on glass resolution downscale |
| `FULL_QUALITY_BLUR_DP` | `8.dp` | Constant | Blur radius threshold where scale bottoms out |

## C2. Color & Theme Tokens (`app/.../ui/theme/AppleTokens.kt`)

| Token | Hex / Value | Usage |
|---|---|---|
| `AccentRed` | `0xFFFA2D48` | Primary active accent (Apple Music pink-red) |
| `Bg` | `0xFF121212` | Default dark surface background |
| `BgElevated` | `0xFF1A1A1A` | Elevated surface (panels, sheets) |
| `Card` | `0xFF1C1C1E` | Primary card background |
| `CardSecondary` | `0xFF2C2C2E` | Secondary card background |
| `Metadata` | `0xFF8E8E93` | Subtitles, artist names, secondary metadata |
| `divider` | `LocalContentColor @ 12% alpha` | Hairline separators |
| Dynamic Tint | HSL-shifted from artwork | `onColor(bg)`, `onColorSecondary(bg)`, `onColorHeading(bg)` |

## C3. Typography Scale (`AppleTokens.kt` & `BrandFont.kt`)

| Style | Font Size | Line Height | Role |
|---|---|---|---|
| `TitleLarge` | `34.sp` | `41.sp` | Top screen title ("Listen Now") |
| `SectionHeader` | `22.sp` | `28.sp` | Section headers ("Recently Played") |
| `ItemTitle` | `15.sp` | `20.sp` | Grid tile title & list primary row text |
| `ItemSubtitle` | `13.sp` | `18.sp` | Tile subtitle & metadata row text |
| `Caption` | `12.sp` | `16.sp` | Speed dial & badge captions |

**Bundled Fonts** (`AppFont` enum in `PreferenceKeys.kt:52`):
1. `SYSTEM` ("system")
2. `GOOGLE_SANS` ("google_sans")
3. `SANS_FLEX` ("sans_flex")
4. `OUTFIT` ("outfit")
5. `PLUS_JAKARTA_SANS` ("plus_jakarta_sans")

## C4. Spacing, Geometry & Shapes (`Dimensions.kt` & `AppleTokens.kt`)

| Token | Value | Role |
|---|---|---|
| `Gutter` | `20.dp` | Horizontal screen outer margin |
| `ItemGap` | `16.dp` | Gap between grid items / siblings |
| `SectionGap` | `24.dp` | Gap between distinct sections |
| `TextGap` | `2.dp` | Vertical gap between stacked title and subtitle |
| `NavigationBarHeight` | `80.dp` | Full floating nav bar height |
| `SlimNavBarHeight` | `64.dp` | Compact/slim nav bar height |
| `NavBarSearchBarHeight` | `48.dp` | Inline search bar inside nav pill |
| `NavBarStandaloneReserve` | `80.dp` | Bottom reserve space for floating nav |
| `NavBarMinTabWidth` | `56.dp` | Minimum width per tab button |
| `MiniPlayerHeight` | `64.dp` | Collapsed mini player height |
| `DockedAccessoryHeight` | `84.dp` | Docked accessory height |
| `MiniPlayerBottomSpacing` | `8.dp` | Gap between mini player and bottom nav |
| `QueuePeekHeight` | `64.dp` | Up Next queue peek sheet height |
| `AppBarHeight` | `64.dp` | Top app bar height |
| `ListItemHeight` | `64.dp` | Standard list row height |
| `ListThumbnailSize` | `48.dp` | Thumbnail size in list rows |
| `ThumbnailCornerRadius` | `12.dp` | Standard artwork corner radius |
| `Artwork` Corner | `12.dp` | Squircle continuous corner (`ContinuousRoundedRectangle`) |
| `Control` Corner | `12.dp` | Buttons, switches, pills |
| `CardCorner` | `22.dp` | Standard card corner radius |
| `CardCornerLarge` | `28.dp` | Large sheet / dialog corner radius |
| `CompactPlayerMaxWidth` | `480.dp` | Capped width of expanded player on tablet/wide screens |

## C5. Motion & Physics Language (`ui/utils/Motion.kt` & `ui/utils/IosOverscroll.kt`)

| Interaction | Spec / Parameter | Value | Reference / Notes |
|---|---|---|---|
| Container Morph | `MorphStiffness` | `950f` | Critically damped spring (`DampingRatioNoBouncy`) |
| Morph Enter | `MorphEnterMillis` / `Easing` | `320ms`, `FastOutSlowInEasing` | Card push transition |
| Morph Exit | `MorphExitMillis` / `Easing` | `240ms`, `FastOutSlowInEasing` | Card pop return transition |
| Tab Selection / Puck | `SelectStiffness` | `750f` | Critically damped spring (`DampingRatioNoBouncy`) |
| Press Feedback | `PressStiffness` | `1220f` | Snappy touch scale down/up |
| In-place Appearance | `AppearStiffness` | `450f` | Sheets, chips, menus |
| Appearance Exit | `AppearExitMillis` | `240ms` | Predictive back / dismiss |
| Navigation Push/Pop | `PushMillis` / `Easing` | `260ms`, `CubicBezier(0.32, 0.72, 0, 1)` | iOS navigation curve |
| Push Parallax | `PushParallax` | `0.30f` (30%) | Outgoing screen travel distance |
| Push Dim | `PushDimAlpha` | `0.85f` | Dim behind incoming push |
| Rubber-band Constant | `RubberBandConstant` | `0.55f` | Apple UIKit rubber-band coefficient |
| Overscroll Fling Scale | `FlingBounceScale` | `1.0f` | Full unconsumed fling velocity to spring |
| Bounce Spring Stiffness | `BounceSpringStiffness` | `247f` | Standard settle (~0.4s period, 300-500ms) |
| High Velocity Threshold | `HighVelocityThreshold` | `5000f` | Switches to soft settle for hard flick |
| High Velocity Stiffness | `HighVelocityBounceStiffness` | `130f` | Slower bounce (~0.55s period) |
| Gooey Transition | `GooeyPeakBlur` / `Duration` | `12.dp`, `300ms` | Nav pill INLINE ↔ EXPANDED |

## C6. Iconography
- **Base Icon Library**: Lucide Icons (SF Symbols aesthetic)
- **Vector format**: Compose VectorPainter / XML VectorDrawables
- **Custom slots**: V1 / V2 player glyph customization supported via `PlayerIconsKey` & `V2PlayerIconsKey`.
