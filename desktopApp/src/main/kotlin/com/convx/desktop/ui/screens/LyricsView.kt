package com.convx.desktop.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.desktop.lyrics.DesktopLyricsManager
import com.convx.desktop.lyrics.LyricLine
import com.convx.desktop.ui.component.AsyncArtwork
import com.convx.desktop.ui.component.GlassEffectConfig
import com.convx.desktop.ui.component.icons.ConvxIcons
import com.convx.desktop.ui.component.liquidGlass
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.desktop.ui.theme.AppleTokens

@Composable
fun LyricsView(
    lyricsManager: DesktopLyricsManager,
    thumbnailUrl: String? = null,
    onSeekToTime: (Long) -> Unit,
    onClose: () -> Unit,
    glassConfig: GlassEffectConfig,
    modifier: Modifier = Modifier
) {
    val state by lyricsManager.lyricsState.collectAsState()
    val listState = rememberLazyListState()

    // Smooth auto-scroll to active lyric line
    LaunchedEffect(state.currentLineIndex) {
        if (state.currentLineIndex >= 0 && state.lines.isNotEmpty()) {
            val targetScrollIndex = (state.currentLineIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScrollIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .liquidGlass(
                config = glassConfig,
                shape = ContinuousRoundedRectangle(AppleTokens.CardCornerLarge)
            )
            .padding(24.dp)
    ) {
        // Ambient background color glow from artwork
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
                        radius = 1000f
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Track Info Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncArtwork(
                        url = thumbnailUrl,
                        modifier = Modifier.size(52.dp),
                        shape = ContinuousRoundedRectangle(AppleTokens.Artwork)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = state.title.ifEmpty { "Lyrics" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.artist.ifEmpty { "Convx Desktop" },
                                fontSize = 13.sp,
                                color = AppleTokens.Metadata,
                                maxLines = 1
                            )
                            if (state.provider != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(ContinuousRoundedRectangle(6.dp))
                                        .background(Color(0x26FFFFFF))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = state.provider ?: "",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppleTokens.AccentRed
                                    )
                                }
                            }
                        }
                    }
                }

                // Close Lyrics Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x26FFFFFF))
                        .clickable(onClick = onClose)
                        .pointerHoverIcon(PointerIcon.Hand),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ConvxIcons.WindowClose,
                        contentDescription = "Close Lyrics",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Body: Content / Loading / Empty
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = AppleTokens.AccentRed,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Finding time-synced lyrics...",
                                fontSize = AppleTokens.ItemTitle,
                                color = AppleTokens.Metadata
                            )
                        }
                    }
                }

                state.lines.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = ConvxIcons.Lyrics,
                                contentDescription = null,
                                tint = AppleTokens.Metadata.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.errorMessage ?: "No lyrics available for this song",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppleTokens.Metadata,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    // Synchronized / Plain Lyrics Scroll List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(vertical = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        itemsIndexed(state.lines) { index, line ->
                            val isActive = index == state.currentLineIndex
                            val isPast = state.currentLineIndex >= 0 && index < state.currentLineIndex

                            val targetAlpha = when {
                                isActive -> 1f
                                isPast -> 0.35f
                                else -> 0.45f
                            }
                            val alpha by animateFloatAsState(
                                targetValue = targetAlpha,
                                animationSpec = spring(stiffness = 300f)
                            )
                            val scale by animateFloatAsState(
                                targetValue = if (isActive) 1.04f else 1.0f,
                                animationSpec = spring(stiffness = 300f)
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isActive) Color.White else AppleTokens.Metadata,
                                animationSpec = spring(stiffness = 300f)
                            )

                            LyricLineRow(
                                line = line,
                                isActive = isActive,
                                alpha = alpha,
                                scale = scale,
                                textColor = textColor,
                                onClick = {
                                    if (line.timeMs >= 0) {
                                        onSeekToTime(line.timeMs)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LyricLineRow(
    line: LyricLine,
    isActive: Boolean,
    alpha: Float,
    scale: Float,
    textColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(ContinuousRoundedRectangle(AppleTokens.CardCorner))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = line.text,
            fontSize = if (isActive) 30.sp else 24.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = textColor.copy(alpha = alpha),
            lineHeight = if (isActive) 38.sp else 32.sp
        )
    }
}
