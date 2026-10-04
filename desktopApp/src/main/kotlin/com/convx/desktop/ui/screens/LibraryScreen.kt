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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.desktop.audio.DesktopAudioPlayer
import com.convx.desktop.audio.MediaTrack
import com.convx.desktop.db.ConvxDatabase
import com.convx.desktop.ui.component.GlassEffectConfig
import com.convx.desktop.ui.component.icons.ConvxIcons
import com.convx.desktop.ui.component.liquidGlass
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.desktop.ui.theme.AppleTokens
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    player: DesktopAudioPlayer?,
    database: ConvxDatabase,
    glassConfig: GlassEffectConfig,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("liked") }

    val likedSongs by database.songDao().getLikedSongs().collectAsState(initial = emptyList())
    val recentSongs by database.songDao().getRecentlyPlayed(50).collectAsState(initial = emptyList())
    val currentTrack by (player?.currentTrack?.collectAsState() ?: remember { mutableStateOf(null) })

    val playTrack: (MediaTrack) -> Unit = { track ->
        player?.playTrack(track)
        scope.launch {
            database.songDao().recordPlay(track.id)
        }
    }

    val playAllLiked: () -> Unit = {
        if (likedSongs.isNotEmpty()) {
            val tracks = likedSongs.map {
                MediaTrack(
                    id = it.id,
                    title = it.title,
                    artists = it.artists,
                    durationSeconds = it.durationSeconds,
                    durationText = it.durationText,
                    thumbnailUrl = it.thumbnailUrl
                )
            }
            player?.setQueue(tracks, 0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        // Page Header
        Text(
            text = "Library",
            fontSize = AppleTokens.TitleLarge,
            lineHeight = AppleTokens.TitleLargeLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Your saved songs, play history, and offline database",
            fontSize = AppleTokens.ItemSubtitle,
            color = AppleTokens.Metadata
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tabs Selector (Liked Songs vs Recently Played)
        Row(
            modifier = Modifier
                .height(44.dp)
                .liquidGlass(
                    config = glassConfig,
                    shape = ContinuousRoundedRectangle(AppleTokens.Control)
                )
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LibraryTabButton(
                title = "Liked Songs (${likedSongs.size})",
                isSelected = selectedTab == "liked",
                onClick = { selectedTab = "liked" }
            )
            Spacer(modifier = Modifier.width(4.dp))
            LibraryTabButton(
                title = "Recently Played (${recentSongs.size})",
                isSelected = selectedTab == "recent",
                onClick = { selectedTab = "recent" }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions Bar (Play All, Shuffle)
        if (selectedTab == "liked" && likedSongs.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(ContinuousRoundedRectangle(12.dp))
                        .background(AppleTokens.AccentRed)
                        .clickable(onClick = playAllLiked)
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ConvxIcons.Play,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play All",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    modifier = Modifier
                        .clip(ContinuousRoundedRectangle(12.dp))
                        .background(AppleTokens.Card)
                        .clickable {
                            if (likedSongs.isNotEmpty()) {
                                val tracks = likedSongs.shuffled().map {
                                    MediaTrack(
                                        id = it.id,
                                        title = it.title,
                                        artists = it.artists,
                                        durationSeconds = it.durationSeconds,
                                        durationText = it.durationText,
                                        thumbnailUrl = it.thumbnailUrl
                                    )
                                }
                                player?.setQueue(tracks, 0)
                            }
                        }
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ConvxIcons.Shuffle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shuffle",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // List Content
        val itemsList = if (selectedTab == "liked") likedSongs else recentSongs

        if (itemsList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedTab == "liked") ConvxIcons.Heart else ConvxIcons.Library,
                        contentDescription = null,
                        tint = AppleTokens.Metadata.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == "liked") "No liked songs yet" else "No recently played songs",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (selectedTab == "liked")
                            "Tap the heart icon on any song in Home or Search to add it here."
                        else
                            "Songs you play will automatically appear in your history.",
                        color = AppleTokens.Metadata,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(itemsList) { song ->
                    val track = MediaTrack(
                        id = song.id,
                        title = song.title,
                        artists = song.artists,
                        durationSeconds = song.durationSeconds,
                        durationText = song.durationText,
                        thumbnailUrl = song.thumbnailUrl
                    )
                    val isPlaying = currentTrack?.id == song.id

                    TrackRowItem(
                        track = track,
                        isPlaying = isPlaying,
                        isLiked = song.isLiked,
                        onPlay = { playTrack(track) },
                        onToggleLike = {
                            scope.launch {
                                database.songDao().setLiked(song.id, !song.isLiked)
                            }
                        }
                    )
                }

                // Buffer space for floating dock
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
private fun LibraryTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(ContinuousRoundedRectangle(8.dp))
            .background(if (isSelected) AppleTokens.AccentRed else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else AppleTokens.Metadata
        )
    }
}
