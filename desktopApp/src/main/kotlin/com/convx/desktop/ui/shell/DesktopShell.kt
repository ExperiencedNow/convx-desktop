package com.convx.desktop.ui.shell

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import com.convx.desktop.audio.DesktopAudioPlayer
import com.convx.desktop.audio.MediaTrack
import com.convx.desktop.audio.PlaybackStatus
import com.convx.desktop.audio.RepeatMode
import com.convx.desktop.ui.component.GlassEffectConfig
import com.convx.desktop.ui.component.LocalGlassEffectConfig
import com.convx.desktop.ui.component.icons.ConvxIcons
import com.convx.desktop.ui.component.liquidGlass
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.desktop.ui.theme.AppleTokens

data class NavItem(
    val id: String,
    val title: String,
    val icon: ImageVector
)

fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WindowScope.DesktopShell(
    windowState: WindowState,
    onClose: () -> Unit,
    player: DesktopAudioPlayer? = null,
    modifier: Modifier = Modifier
) {
    val glassConfig = remember { GlassEffectConfig() }
    var selectedRoute by remember { mutableStateOf("home") }

    // Reactive Player State
    val status = player?.status?.collectAsState()?.value ?: PlaybackStatus.IDLE
    val currentTrack = player?.currentTrack?.collectAsState()?.value
    val positionMs = player?.positionMs?.collectAsState()?.value ?: 0L
    val durationMs = player?.durationMs?.collectAsState()?.value ?: 0L
    val volume = player?.volume?.collectAsState()?.value ?: 0.75f
    val isMuted = player?.isMuted?.collectAsState()?.value ?: false
    val isShuffle = player?.isShuffle?.collectAsState()?.value ?: false
    val repeatMode = player?.repeatMode?.collectAsState()?.value ?: RepeatMode.OFF

    val isPlaying = status == PlaybackStatus.PLAYING
    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val navItems = remember {
        listOf(
            NavItem("home", "Listen Now", ConvxIcons.Home),
            NavItem("songs", "Songs", ConvxIcons.Library),
            NavItem("search", "Search", ConvxIcons.Search),
            NavItem("library", "Library", ConvxIcons.Library),
            NavItem("settings", "Settings", ConvxIcons.Settings)
        )
    }

    val demoTracks = remember {
        listOf(
            MediaTrack("3_g2un5M350", "Starboy", "The Weeknd ft. Daft Punk", "3:50", 230),
            MediaTrack("4NRXx6U8ABQ", "Blinding Lights", "The Weeknd", "3:20", 200),
            MediaTrack("ygTZZpVHNmA", "After Hours", "The Weeknd", "6:01", 361),
            MediaTrack("4D7u5KF7SP8", "Get Lucky", "Daft Punk ft. Pharrell Williams", "4:08", 248)
        )
    }

    CompositionLocalProvider(LocalGlassEffectConfig provides glassConfig) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = AppleTokens.Bg
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Windows Custom Title Bar
                CustomTitleBar(
                    windowState = windowState,
                    onClose = onClose
                )

                // Main body: Sidebar + Content
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    // Ambient Background Gradient Glow
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x33FA2D48),
                                        Color(0x1F8E2DE2),
                                        Color.Transparent
                                    ),
                                    radius = 1200f
                                )
                            )
                    )

                    Row(modifier = Modifier.fillMaxSize()) {
                        // Desktop / Tablet Sidebar
                        DesktopSidebar(
                            items = navItems,
                            selectedId = selectedRoute,
                            onSelect = { selectedRoute = it },
                            glassConfig = glassConfig
                        )

                        // Main Content View
                        DesktopContentView(
                            selectedRoute = selectedRoute,
                            featuredTracks = demoTracks,
                            onPlayTrack = { track ->
                                player?.playTrack(track)
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }

                    // Floating Liquid Glass Mini Player Dock at bottom
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = AppleTokens.Gutter, start = AppleTokens.Gutter, end = AppleTokens.Gutter)
                    ) {
                        DesktopMiniPlayerDock(
                            trackTitle = currentTrack?.title ?: "Convx Music",
                            trackArtist = currentTrack?.artists ?: "Select a track to play",
                            isPlaying = isPlaying,
                            isBuffering = status == PlaybackStatus.BUFFERING,
                            onTogglePlay = {
                                if (player != null) {
                                    player.togglePlayPause()
                                }
                            },
                            progress = progress,
                            positionMs = positionMs,
                            durationMs = durationMs,
                            onSeek = { fraction ->
                                player?.seekTo(fraction)
                            },
                            volume = volume,
                            onVolumeChange = { vol ->
                                player?.setVolume(vol)
                            },
                            isMuted = isMuted,
                            onToggleMute = {
                                player?.toggleMute()
                            },
                            onNext = {
                                player?.skipNext()
                            },
                            onPrevious = {
                                player?.skipPrevious()
                            },
                            isShuffle = isShuffle,
                            onToggleShuffle = {
                                player?.toggleShuffle()
                            },
                            repeatMode = repeatMode,
                            onCycleRepeat = {
                                player?.cycleRepeat()
                            },
                            glassConfig = glassConfig
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WindowScope.CustomTitleBar(
    windowState: WindowState,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(Color(0xFF0F0F11)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Draggable window area
        WindowDraggableArea(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo Badge
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(ContinuousRoundedRectangle(6.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AppleTokens.AccentRed, Color(0xFFFF5E7E))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ConvxIcons.Play,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Convx",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "r52 · Desktop",
                    fontSize = 11.sp,
                    color = AppleTokens.Metadata
                )
            }
        }

        // Window Control Buttons (Minimize, Maximize / Restore, Close)
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleBarButton(
                icon = ConvxIcons.WindowMinimize,
                onClick = { windowState.isMinimized = true }
            )
            TitleBarButton(
                icon = if (windowState.placement == WindowPlacement.Maximized) {
                    ConvxIcons.WindowRestore
                } else {
                    ConvxIcons.WindowMaximize
                },
                onClick = {
                    windowState.placement = if (windowState.placement == WindowPlacement.Maximized) {
                        WindowPlacement.Floating
                    } else {
                        WindowPlacement.Maximized
                    }
                }
            )
            TitleBarButton(
                icon = ConvxIcons.WindowClose,
                isClose = true,
                onClick = onClose
            )
        }
    }
}

@Composable
private fun TitleBarButton(
    icon: ImageVector,
    isClose: Boolean = false,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    val hoverBg = if (isClose) Color(0xFFE81123) else Color(0x22FFFFFF)

    Box(
        modifier = Modifier
            .width(46.dp)
            .fillMaxHeight()
            .background(if (isHovered) hoverBg else Color.Transparent)
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Default)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isHovered && isClose) Color.White else Color(0xCCFFFFFF),
            modifier = Modifier.size(10.dp)
        )
    }
}

@Composable
fun DesktopSidebar(
    items: List<NavItem>,
    selectedId: String,
    onSelect: (String) -> Unit,
    glassConfig: GlassEffectConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .padding(AppleTokens.Gutter)
            .liquidGlass(
                config = glassConfig.forSidePanel(),
                shape = ContinuousRoundedRectangle(AppleTokens.CardCorner)
            )
            .padding(12.dp)
    ) {
        Text(
            text = "LIBRARY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppleTokens.Metadata,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        items.forEach { item ->
            val isSelected = item.id == selectedId
            val puckAlpha by animateFloatAsState(
                targetValue = if (isSelected) 0.25f else 0f,
                animationSpec = AppleTokens.Motion.standard()
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) AppleTokens.AccentRed else Color.White,
                animationSpec = AppleTokens.Motion.standard()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(ContinuousRoundedRectangle(AppleTokens.Artwork))
                    .background(Color.White.copy(alpha = puckAlpha))
                    .clickable { onSelect(item.id) }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.title,
                    fontSize = AppleTokens.ItemTitle,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun DesktopContentView(
    selectedRoute: String,
    featuredTracks: List<MediaTrack>,
    onPlayTrack: (MediaTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = AppleTokens.Gutter, vertical = AppleTokens.Gutter)
    ) {
        // Section Screen Title
        Text(
            text = when (selectedRoute) {
                "home" -> "Listen Now"
                "songs" -> "Songs"
                "search" -> "Search"
                "library" -> "Your Library"
                "settings" -> "Settings"
                else -> "Listen Now"
            },
            fontSize = AppleTokens.TitleLarge,
            lineHeight = AppleTokens.TitleLargeLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(AppleTokens.SectionGap))

        // Hero Card with squircle shape and liquid glass
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(ContinuousRoundedRectangle(AppleTokens.CardCornerLarge))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2A0845),
                            Color(0xFF6441A5),
                            Color(0xFFFA2D48)
                        )
                    )
                )
                .clickable {
                    if (featuredTracks.isNotEmpty()) onPlayTrack(featuredTracks.first())
                }
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = "FEATURED ALBUM · THE WEEKND",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Starboy (Deluxe Edition)",
                    fontSize = AppleTokens.SectionHeader,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Click to play in high-fidelity Opus via LibVLC audio engine.",
                    fontSize = AppleTokens.ItemSubtitle,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppleTokens.SectionGap))

        // Section: Recently Played Grid
        Text(
            text = "Recently Played",
            fontSize = AppleTokens.SectionHeader,
            lineHeight = AppleTokens.SectionHeaderLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(AppleTokens.ItemGap))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppleTokens.ItemGap)
        ) {
            featuredTracks.forEachIndexed { index, track ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(ContinuousRoundedRectangle(AppleTokens.Artwork))
                        .clickable { onPlayTrack(track) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(ContinuousRoundedRectangle(AppleTokens.Artwork))
                            .background(
                                when (index % 4) {
                                    0 -> Color(0xFF3A1C71)
                                    1 -> Color(0xFFD76D77)
                                    2 -> Color(0xFF200122)
                                    else -> Color(0xFF0F2027)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ConvxIcons.Play,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = track.title,
                        fontSize = AppleTokens.ItemTitle,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 1
                    )

                    Text(
                        text = track.artists,
                        fontSize = AppleTokens.ItemSubtitle,
                        color = AppleTokens.Metadata,
                        maxLines = 1
                    )
                }
            }
        }

        // Leave buffer space for the floating bottom mini player
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun DesktopMiniPlayerDock(
    trackTitle: String,
    trackArtist: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onTogglePlay: () -> Unit,
    progress: Float,
    positionMs: Long,
    durationMs: Long,
    onSeek: (Float) -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    isShuffle: Boolean,
    onToggleShuffle: () -> Unit,
    repeatMode: RepeatMode,
    onCycleRepeat: () -> Unit,
    glassConfig: GlassEffectConfig,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .liquidGlass(
                config = glassConfig,
                shape = ContinuousRoundedRectangle(AppleTokens.CardCornerLarge)
            )
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Artwork Thumbnail + Metadata
        Row(
            modifier = Modifier.width(260.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(ContinuousRoundedRectangle(AppleTokens.Artwork))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFE52D27), Color(0xFFB31217))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) ConvxIcons.Pause else ConvxIcons.Play,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = trackTitle,
                    fontSize = AppleTokens.ItemTitle,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(AppleTokens.TextGap))
                Text(
                    text = if (isBuffering) "Buffering stream..." else trackArtist,
                    fontSize = AppleTokens.ItemSubtitle,
                    color = if (isBuffering) AppleTokens.AccentRed else AppleTokens.Metadata,
                    maxLines = 1
                )
            }
        }

        // Playback Controls + Progress Scrubber
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Control buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = ConvxIcons.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffle) AppleTokens.AccentRed else AppleTokens.Metadata,
                    modifier = Modifier.size(16.dp).clickable(onClick = onToggleShuffle)
                )
                Icon(
                    imageVector = ConvxIcons.SkipBack,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp).clickable(onClick = onPrevious)
                )
                // Play / Pause Circle Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onTogglePlay),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) ConvxIcons.Pause else ConvxIcons.Play,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Icon(
                    imageVector = ConvxIcons.SkipForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp).clickable(onClick = onNext)
                )
                Icon(
                    imageVector = ConvxIcons.Repeat,
                    contentDescription = "Repeat",
                    tint = if (repeatMode != RepeatMode.OFF) AppleTokens.AccentRed else AppleTokens.Metadata,
                    modifier = Modifier.size(16.dp).clickable(onClick = onCycleRepeat)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Scrubber
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTime(positionMs),
                    fontSize = 11.sp,
                    color = AppleTokens.Metadata
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = progress,
                    onValueChange = onSeek,
                    modifier = Modifier.weight(1f).height(12.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = AppleTokens.AccentRed,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTime(durationMs),
                    fontSize = 11.sp,
                    color = AppleTokens.Metadata
                )
            }
        }

        // Volume Controls
        Row(
            modifier = Modifier.width(200.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = if (isMuted) ConvxIcons.VolumeMute else ConvxIcons.VolumeUp,
                contentDescription = "Mute",
                tint = Color.White,
                modifier = Modifier.size(18.dp).clickable(onClick = onToggleMute)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = if (isMuted) 0f else volume,
                onValueChange = onVolumeChange,
                modifier = Modifier.width(110.dp).height(12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color(0x33FFFFFF)
                )
            )
        }
    }
}
