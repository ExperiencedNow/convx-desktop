# Settings & Preference Keys Inventory (Phase 1 Audit)

Extracted from `app/src/main/kotlin/com/convx/music/constants/PreferenceKeys.kt` (323 total preference keys across all subsystems).

## Key Categories & Desktop Verdict

### 1. Appearance & Liquid Glass (`Settings -> Appearance`)
| Preference Key | Type | Default | Effect / Description | Android-only? | Desktop Strategy |
|---|---|---|---|---|---|
| `GlassEffectConfig` (composite) | Object / Keys | See `design-tokens.md` | Blur, lens, vibrancy, rim highlight, surface tint | No | Fully supported via Skia runtime shader & backdrop |
| `DynamicThemeKey` | Boolean | `true` | Derive palette from active track artwork | No | Direct port (materialkolor KMP) |
| `SelectedThemeColorKey` | Int | Unspecified | Custom accent color when dynamic theme is off | No | Direct port |
| `DarkModeKey` | String | `"system"` | `"follow_system"`, `"on"`, `"off"` | No | System theme detection on Windows |
| `PureBlackKey` | Boolean | `false` | OLED pure black backgrounds | No | Direct port |
| `SelectedFontKey` | String | `"system"` | Bundled fonts: Google Sans, Outfit, etc. | No | Direct font loading from bundled assets |
| `DensityScaleKey` | Float | `1.0f` | App UI scaling factor | No | Directly scales Compose density |
| `SlimNavBarKey` | Boolean | `false` | Slim vs standard nav bar | No | Direct port |
| `OverlayMenuStyleKey` | Boolean | `true` | Full screen dim overlay vs bottom sheet | No | Direct port |
| `CompactPlayerInTabViewKey`| Boolean| `true` | Caps expanded player on wide screens | No | Primary layout mode on desktop! |
| `DiyLayoutKey` | String | `"{}"` | Custom stickers layout on player | No | Direct port (JSON format preserved) |
| `PlayerIconsKey` | String | `"{}"` | Custom glyphs for player controls | No | Direct port (JSON format preserved) |

### 2. Player & Playback Core (`Settings -> Player`)
| Preference Key | Type | Default | Effect / Description | Android-only? | Desktop Strategy |
|---|---|---|---|---|---|
| `UseAppleMusicPlayerKey` | Boolean | `true` | Player V2 design (Apple Music card morph) | No | Core P0 desktop experience |
| `CrossfadeEnabledKey` | Boolean | `false` | Crossfade between queue tracks | No | Implemented in `PlayerEngine` |
| `CrossfadeDurationKey` | Int | `4` | Seconds of crossfade duration | No | Implemented in `PlayerEngine` |
| `CrossfadeGaplessKey` | Boolean | `true` | Gapless playback trigger | No | Implemented in `PlayerEngine` |
| `AudioNormalizationKey` | Boolean | `false` | Loudness equalization / normalization gain | Android `LoudnessEnhancer` | Replaced by engine-level gain |
| `SkipSilenceKey` | Boolean | `false` | Skip silence in playback streams | Android processor | Engine-level silence skipping |
| `PersistentQueueKey` | Boolean | `true` | Persist playback queue across app launches | No | Direct port (Room/DataStore) |
| `RememberShuffleAndRepeatKey`| Boolean | `true` | Persist shuffle and repeat states | No | Direct port |
| `SleepTimerTimeKey` | Int | `0` | Sleep timer duration in minutes | No | Direct timer with volume ramp-down |
| `SleepTimerFadeOutKey` | Boolean | `true` | Fade out volume over last 30s of timer | No | Direct coroutine volume ramp |

### 3. Content & Stream Resolution (`Settings -> Content`)
| Preference Key | Type | Default | Effect / Description | Android-only? | Desktop Strategy |
|---|---|---|---|---|---|
| `ContentLanguageKey` | String | `"system"` | Preferred YouTube content language | No | Passed to InnerTube headers |
| `ContentCountryKey` | String | `"system"` | Preferred YouTube content country/region | No | Passed to InnerTube headers |
| `AudioQualityKey` | String | `"auto"` | `"auto"`, `"high"` (256k AAC/Opus), `"low"` | No | InnerTube stream format selector |
| `ClientFallbackChainKey` | String | `"VISIONOS,IOS,..."` | Order of fallback clients | No | Preserved exactly (VISIONOS -> IOS -> ...) |
| `ProxyEnabledKey` | Boolean | `false` | Custom HTTP/SOCKS proxy | No | Replaced by OkHttp / JVM proxy selector |
| `CustomDnsKey` | String | `"system"` | Custom DNS resolver IP | No | OkHttp custom Dns implementation |

### 4. Integrations (`Settings -> Integrations`)
| Preference Key | Type | Default | Effect / Description | Android-only? | Desktop Strategy |
|---|---|---|---|---|---|
| `DiscordRpcEnabledKey` | Boolean | `false` | Discord Rich Presence status | No | Directly reusable via `:kizzy` (Ktor RPC) |
| `LastFmEnabledKey` | Boolean | `false` | Last.fm scrobbler | No | Directly reusable via `:lastfm` (Ktor API) |
| `ListenTogetherEnabledKey`| Boolean| `false` | Room sharing with friends | No | Network client portable |

### 5. Android-Only Preferences (Dropped or Adapted)
| Preference Key | Type | Default | Android Purpose | Desktop Adaptation |
|---|---|---|---|---|
| `EnableHighRefreshRateKey` | Boolean | `true` | Android Display.Mode 120Hz request | Handled by OS/Skia refresh rate |
| `AutoDownloadUpdateKey` | Boolean | `true` | Background APK download | Desktop updater reads desktop GitHub releases |
| `RingtoneDialogKey` | Boolean | `false` | Set track as system ringtone | Dropped (no desktop equivalent) |
| `AndroidAutoEnabledKey` | Boolean | `false` | Android Auto media browser service | Dropped (replaced by Windows SMTC / media keys) |
