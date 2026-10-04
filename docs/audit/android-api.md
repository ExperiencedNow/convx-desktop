# Android API Inventory (Phase 1 Audit)

Inventory of files in `app/` that import Android-specific APIs and their desktop porting strategy.

| File Path | Android APIs Used | What It Is Used For | Desktop Strategy | Effort | Priority Tier |
|---|---|---|---|---|---|
| `ui/component/GlassEffect.kt` | `android.app.ActivityManager`, `android.os.Build`, `LocalContext` | Check API level & low-RAM device; fallback to translucent | Remove API check; on desktop glass is always allowed; reduced glass toggle for low GPUs | S | P0 |
| `ui/component/backdrop/Platform.kt` | `android.graphics.RenderEffect`, `android.graphics.RenderNode`, `android.os.Build` | Hardware blur & shader offscreen rendering on Android 12+ (API 31/33) | Replace with Skia `org.jetbrains.skia.ImageFilter` blur & `RuntimeEffect` SkSL shaders | M | P0 |
| `ui/component/backdrop/RuntimeShader.kt` | `android.graphics.RuntimeShader` | AGSL lens refraction & chromatic aberration | Replace with Skia `RuntimeEffect.makeForShader` (SkSL) | M | P0 |
| `ui/component/backdrop/internal/LayerRecorder.kt` | `androidx.compose.ui.graphics.layer.GraphicsLayer` | Capture backdrop pixels | Multiplatform `GraphicsLayer` in Compose 1.10+ is multiplatform | S | P0 |
| `playback/MusicService.kt` | `androidx.media3.session.MediaLibraryService`, `ExoPlayer`, `AudioManager` | Android foreground playback service & dual player engine | Replace with `PlayerEngine` interface + bundled engine (libVLC / vlcj / MPV) | L | P0 |
| `playback/MediaLibrarySessionCallback.kt` | `MediaLibrarySession.Callback`, `SessionCommand` | Media session commands, Android Auto, Bluetooth | Replace with Windows SMTC (System Media Transport Controls) + tray controls | M | P1 |
| `playback/DownloadUtil.kt` | `androidx.media3.datasource.cache.SimpleCache` | Media3 download cache | Replace with standard file-based stream cache on disk | M | P1 |
| `playback/audio/LosslessStallWatchdogAudioProcessor.kt` | `androidx.media3.common.audio.AudioProcessor` | Detect stream stalls where position advances without audio | Engine-level position watchdog in `PlayerEngine` | S | P0 |
| `playback/audio/SilenceSkippingAudioProcessor.kt` | Media3 SilenceSkipping processor | Skip silence in playback tracks | Engine-level silence skipping (libVLC DSP) | S | P1 |
| `playback/audio/SonicAudioProcessor.kt` | Sonic speed/pitch processor | Playback rate change with pitch correction | Built-in libVLC playback rate & pitch support | S | P1 |
| `db/MusicDatabase.kt` | `androidx.room.RoomDatabase` | Room database schema and DAO access | Port to Room KMP (`androidx.room:room-runtime:2.8.4` with bundled SQLite) | M | P0 |
| `utils/DataStore.kt` | `androidx.datastore.preferences.core.Preferences` | Settings and queue persistence | Multiplatform DataStore KMP (same `PreferenceKeys` and JSON structures) | S | P0 |
| `utils/LocalAudioScanner.kt` | `android.provider.MediaStore`, `ContentResolver` | Scan local music files from Android MediaStore | Replace with Java NIO file tree walker + audio metadata reader (JAudiotagger) | M | P1 |
| `utils/YTPlayerUtils.kt` | Android OkHttp client, DNS, PoToken | YouTube player client resolution & stream decrypt | Port to JVM OkHttp client; persistent QuickJS for cipher deobfuscation | M | P0 |
| `di/` (all modules) | `dagger.hilt.android.*`, `HiltViewModel` | Dependency injection on Android | Manual wiring / Koin for Desktop ViewModels & singletons | M | P0 |
| `eq/AudioEffect.kt` | `android.media.audiofx.Equalizer`, `LoudnessEnhancer` | System audio equalizer & volume boost | Engine-level 10-band equalizer (libVLC equalizer API) | M | P1 |
| `vivimusic/updater/vivimusicupdater.kt` | `android.app.DownloadManager`, `FileProvider`, `Intent` | Download APK and launch package installer | Replace with desktop updater querying GitHub releases of desktop repo | M | P1 |
| `widget/*` | `androidx.glance.appwidget.*`, `RemoteViews` | Android home screen widgets | Dropped (no desktop equivalent; replaced by Windows desktop tray / mini-window) | S | Dropped |
| `constants/Donation.kt` | Android Play Billing / UPI intents | In-app donation prompts | Web browser link opens donation URLs | S | P1 |
