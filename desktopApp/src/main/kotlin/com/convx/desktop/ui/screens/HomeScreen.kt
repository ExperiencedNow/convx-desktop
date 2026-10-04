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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.desktop.audio.DesktopAudioPlayer
import com.convx.desktop.audio.MediaTrack
import com.convx.desktop.db.ConvxDatabase
import com.convx.desktop.db.SongRecord
import com.convx.desktop.ui.component.AsyncArtwork
import com.convx.desktop.ui.component.icons.ConvxIcons
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.desktop.ui.theme.AppleTokens
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    player: DesktopAudioPlayer?,
    database: ConvxDatabase,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val recentSongs by database.songDao().getRecentlyPlayed(12).collectAsState(initial = emptyList())
    val currentTrack by (player?.currentTrack?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(null) })

    val quickPicks = remember {
        listOf(
            MediaTrack("3_g2un5M350", "Starboy", "The Weeknd ft. Daft Punk", "3:50", 230, "https://i.ytimg.com/vi/3_g2un5M350/hqdefault.jpg"),
            MediaTrack("4NRXx6U8ABQ", "Blinding Lights", "The Weeknd", "3:20", 200, "https://i.ytimg.com/vi/4NRXx6U8ABQ/hqdefault.jpg"),
            MediaTrack("ygTZZpVHNmA", "After Hours", "The Weeknd", "6:01", 361, "https://i.ytimg.com/vi/ygTZZpVHNmA/hqdefault.jpg"),
            MediaTrack("4D7u5KF7SP8", "Get Lucky", "Daft Punk ft. Pharrell Williams", "4:08", 248, "https://i.ytimg.com/vi/4D7u5KF7SP8/hqdefault.jpg"),
            MediaTrack("BSTsnWoslP4", "Bohemian Rhapsody", "Queen", "5:55", 355, "https://i.ytimg.com/vi/BSTsnWoslP4/hqdefault.jpg"),
            MediaTrack("BciS5krYL80", "Hotel California", "Eagles", "6:30", 390, "https://i.ytimg.com/vi/BciS5krYL80/hqdefault.jpg")
        )
    }

    val playTrack: (MediaTrack) -> Unit = { track ->
        player?.playTrack(track)
        scope.launch {
            val record = SongRecord(
                id = track.id,
                title = track.title,
                artists = track.artists,
                durationSeconds = track.durationSeconds,
                durationText = track.durationText,
                thumbnailUrl = track.thumbnailUrl,
                isLiked = false,
                playCount = 1,
                lastPlayedTime = System.currentTimeMillis()
            )
            database.songDao().insertOrUpdate(record)
            database.songDao().recordPlay(track.id)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        // Page Header
        Text(
            text = "Listen Now",
            fontSize = AppleTokens.TitleLarge,
            lineHeight = AppleTokens.TitleLargeLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Hand-picked for you · High Fidelity Skia & LibVLC Engine",
            fontSize = AppleTokens.ItemSubtitle,
            color = AppleTokens.Metadata
        )

        Spacer(modifier = Modifier.height(AppleTokens.SectionGap))

        // Hero Featured Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
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
                    playTrack(quickPicks.first())
                }
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = "FEATURED ALBUM · THE WEEKND",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.75f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Starboy (Deluxe Edition)",
                    fontSize = AppleTokens.SectionHeader,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stream in 128KB chunked Opus audio directly via LibVLC player.",
                    fontSize = AppleTokens.ItemSubtitle,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(52.dp)
                    .clip(ContinuousRoundedRectangle(16.dp))
                    .background(Color.White)
                    .clickable { playTrack(quickPicks.first()) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ConvxIcons.Play,
                    contentDescription = "Play Featured",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppleTokens.SectionGap))

        // Section: Quick Picks
        Text(
            text = "Quick Picks",
            fontSize = AppleTokens.SectionHeader,
            lineHeight = AppleTokens.SectionHeaderLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(AppleTokens.ItemGap))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppleTokens.ItemGap)
        ) {
            items(quickPicks) { track ->
                SongCardItem(
                    track = track,
                    isPlaying = currentTrack?.id == track.id,
                    onPlay = { playTrack(track) }
                )
            }
        }

        Spacer(modifier = Modifier.height(AppleTokens.SectionGap))

        // Section: Recently Played
        Text(
            text = "Recently Played",
            fontSize = AppleTokens.SectionHeader,
            lineHeight = AppleTokens.SectionHeaderLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(AppleTokens.ItemGap))

        if (recentSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(ContinuousRoundedRectangle(AppleTokens.CardCorner))
                    .background(AppleTokens.Card),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No songs played yet. Click any track above or search to start listening!",
                    color = AppleTokens.Metadata,
                    fontSize = AppleTokens.ItemSubtitle
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppleTokens.ItemGap)
            ) {
                items(recentSongs) { song ->
                    val track = MediaTrack(
                        id = song.id,
                        title = song.title,
                        artists = song.artists,
                        durationText = song.durationText,
                        durationSeconds = song.durationSeconds,
                        thumbnailUrl = song.thumbnailUrl
                    )
                    SongCardItem(
                        track = track,
                        isPlaying = currentTrack?.id == track.id,
                        onPlay = { playTrack(track) }
                    )
                }
            }
        }

        // Buffer space for bottom floating dock
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun SongCardItem(
    track: MediaTrack,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(170.dp)
            .clip(ContinuousRoundedRectangle(AppleTokens.Artwork))
            .clickable(onClick = onPlay)
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(ContinuousRoundedRectangle(AppleTokens.Artwork))
        ) {
            AsyncArtwork(
                url = track.thumbnailUrl,
                modifier = Modifier.fillMaxSize(),
                shape = ContinuousRoundedRectangle(AppleTokens.Artwork)
            )

            // Play overlay on hover or active
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(38.dp)
                    .clip(ContinuousRoundedRectangle(12.dp))
                    .background(if (isPlaying) AppleTokens.AccentRed else Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) ConvxIcons.Pause else ConvxIcons.Play,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = track.title,
            fontSize = AppleTokens.ItemTitle,
            fontWeight = FontWeight.SemiBold,
            color = if (isPlaying) AppleTokens.AccentRed else Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = track.artists,
            fontSize = AppleTokens.ItemSubtitle,
            color = AppleTokens.Metadata,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
