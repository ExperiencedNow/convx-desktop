package com.convx.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.desktop.audio.DesktopAudioPlayer
import com.convx.desktop.audio.RepeatMode
import com.convx.desktop.db.SettingsManager
import com.convx.desktop.db.StoragePaths
import com.convx.desktop.ui.component.GlassEffectConfig
import com.convx.desktop.ui.component.GlassStyle
import com.convx.desktop.ui.component.liquidGlass
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.desktop.ui.theme.AppleTokens
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    player: DesktopAudioPlayer?,
    settingsManager: SettingsManager,
    glassConfig: GlassEffectConfig,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val volume by settingsManager.volume.collectAsState(initial = 0.75f)
    val isMuted by settingsManager.isMuted.collectAsState(initial = false)
    val repeatMode by settingsManager.repeatMode.collectAsState(initial = RepeatMode.OFF)
    val glassStyle by settingsManager.glassStyle.collectAsState(initial = GlassStyle.LIQUID)
    val glassVibrancy by settingsManager.glassVibrancy.collectAsState(initial = 1.2f)
    val glassBlurRadius by settingsManager.glassBlurRadius.collectAsState(initial = 2f)
    val discordRpcEnabled by settingsManager.discordRpcEnabled.collectAsState(initial = false)
    val lastfmEnabled by settingsManager.lastfmEnabled.collectAsState(initial = false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        // Page Header
        Text(
            text = "Settings",
            fontSize = AppleTokens.TitleLarge,
            lineHeight = AppleTokens.TitleLargeLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Preferences, visual styling, audio engine, and system integration",
            fontSize = AppleTokens.ItemSubtitle,
            color = AppleTokens.Metadata
        )

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 1: APPEARANCE & LIQUID GLASS
        SettingsSectionHeader(title = "Appearance & Liquid Glass")

        SettingsCard(glassConfig = glassConfig) {
            // Glass Style Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Glass Surface Style",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "SkSL runtime shader refraction model",
                        fontSize = 12.sp,
                        color = AppleTokens.Metadata
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(GlassStyle.LIQUID, GlassStyle.BLUR, GlassStyle.TRANSPARENT).forEach { style ->
                        val isSelected = glassStyle == style
                        Box(
                            modifier = Modifier
                                .clip(ContinuousRoundedRectangle(8.dp))
                                .background(if (isSelected) AppleTokens.AccentRed else AppleTokens.Card)
                                .clickable {
                                    scope.launch {
                                        settingsManager.setGlassStyle(style)
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = style.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Blur Radius Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Blur Strength", fontSize = 14.sp, color = Color.White)
                    Text(text = "${glassBlurRadius.toInt()} dp", fontSize = 13.sp, color = AppleTokens.Metadata)
                }
                Slider(
                    value = glassBlurRadius,
                    onValueChange = {
                        scope.launch { settingsManager.setGlassBlurRadius(it) }
                    },
                    valueRange = 0f..24f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = AppleTokens.AccentRed,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vibrancy Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Glass Vibrancy", fontSize = 14.sp, color = Color.White)
                    Text(text = "${(glassVibrancy * 100).toInt()}%", fontSize = 13.sp, color = AppleTokens.Metadata)
                }
                Slider(
                    value = glassVibrancy,
                    onValueChange = {
                        scope.launch { settingsManager.setGlassVibrancy(it) }
                    },
                    valueRange = 0f..2f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = AppleTokens.AccentRed,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 2: AUDIO ENGINE
        SettingsSectionHeader(title = "Audio Engine (LibVLC 3.0.23)")

        SettingsCard(glassConfig = glassConfig) {
            // Player Backend Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Native Engine Backend",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "LibVLC 3.0.23 (x86_64) + JDK 21 LocalStreamProxy",
                        fontSize = 12.sp,
                        color = AppleTokens.Metadata
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(ContinuousRoundedRectangle(8.dp))
                        .background(Color(0xFF107C41))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Volume Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Master Output Volume", fontSize = 14.sp, color = Color.White)
                    Text(text = "${(volume * 100).toInt()}%", fontSize = 13.sp, color = AppleTokens.Metadata)
                }
                Slider(
                    value = volume,
                    onValueChange = {
                        player?.setVolume(it)
                        scope.launch { settingsManager.setVolume(it) }
                    },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = AppleTokens.AccentRed,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mute Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Mute Audio Output", fontSize = 14.sp, color = Color.White)
                    Text(text = "Silence audio while continuing playback clock", fontSize = 12.sp, color = AppleTokens.Metadata)
                }
                Switch(
                    checked = isMuted,
                    onCheckedChange = {
                        player?.toggleMute()
                        scope.launch { settingsManager.setMuted(it) }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppleTokens.AccentRed
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 3: INTEGRATIONS & SUBSYSTEMS
        SettingsSectionHeader(title = "Integrations & Subsystems")

        SettingsCard(glassConfig = glassConfig) {
            // Discord Rich Presence Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Discord Rich Presence", fontSize = 14.sp, color = Color.White)
                    Text(text = "Broadcast \"Listening to ...\" status via Kizzy RPC", fontSize = 12.sp, color = AppleTokens.Metadata)
                }
                Switch(
                    checked = discordRpcEnabled,
                    onCheckedChange = {
                        scope.launch { settingsManager.setDiscordRpcEnabled(it) }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppleTokens.AccentRed
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Last.fm Scrobbler Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Last.fm Scrobbler", fontSize = 14.sp, color = Color.White)
                    Text(text = "Track listening history and now playing status", fontSize = 12.sp, color = AppleTokens.Metadata)
                }
                Switch(
                    checked = lastfmEnabled,
                    onCheckedChange = {
                        scope.launch { settingsManager.setLastfmEnabled(it) }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppleTokens.AccentRed
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lyrics Subsystem Attribution
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Time-Synced Lyrics Providers", fontSize = 14.sp, color = Color.White)
                    Text(text = "Multi-provider waterfall engine", fontSize = 12.sp, color = AppleTokens.Metadata)
                }
                Text(
                    text = "LrcLib · Simp · YouLy · KuGou",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppleTokens.AccentRed
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 4: STORAGE & PERSISTENCE
        SettingsSectionHeader(title = "Storage & Local Data")

        SettingsCard(glassConfig = glassConfig) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsPathRow(label = "Application Data", path = StoragePaths.appDataDir.absolutePath)
                SettingsPathRow(label = "SQLite Database", path = StoragePaths.databaseFile.absolutePath)
                SettingsPathRow(label = "Preferences File", path = StoragePaths.preferencesFile.absolutePath)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECTION 4: ABOUT CONVX DESKTOP
        SettingsSectionHeader(title = "About Convx Desktop")

        SettingsCard(glassConfig = glassConfig) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Convx Desktop (Nightly r52)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "A full, faithful desktop port of Convx YouTube Music client with iOS-like liquid glass aesthetic.",
                    fontSize = 13.sp,
                    color = AppleTokens.Metadata
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Built with Kotlin Compose Multiplatform (JVM Skia), Room KMP 2.8.4, and LibVLC 3.0.23.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "© 2026 Convx contributors · GPL-3.0 License",
                    fontSize = 11.sp,
                    color = AppleTokens.Metadata
                )
            }
        }

        // Buffer space for bottom floating dock
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = AppleTokens.SectionHeader,
        lineHeight = AppleTokens.SectionHeaderLineHeight,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Spacer(modifier = Modifier.height(AppleTokens.ItemGap))
}

@Composable
private fun SettingsCard(
    glassConfig: GlassEffectConfig,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                config = glassConfig,
                shape = ContinuousRoundedRectangle(AppleTokens.CardCorner)
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsPathRow(label: String, path: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
        Text(
            text = path,
            fontSize = 11.sp,
            color = AppleTokens.Metadata,
            maxLines = 1
        )
    }
}
