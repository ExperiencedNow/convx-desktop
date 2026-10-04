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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
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
fun SongsScreen(
    player: DesktopAudioPlayer?,
    database: ConvxDatabase,
    glassConfig: GlassEffectConfig,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var filterText by remember { mutableStateOf("") }

    val allSongs by database.songDao().getAllSongs().collectAsState(initial = emptyList())
    val currentTrack by (player?.currentTrack?.collectAsState() ?: remember { mutableStateOf(null) })

    val filteredSongs = remember(allSongs, filterText) {
        if (filterText.isBlank()) allSongs
        else allSongs.filter {
            it.title.contains(filterText, ignoreCase = true) ||
            it.artists.contains(filterText, ignoreCase = true)
        }
    }

    val playTrack: (MediaTrack) -> Unit = { track ->
        player?.playTrack(track)
        scope.launch {
            database.songDao().recordPlay(track.id)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        // Page Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Songs",
                    fontSize = AppleTokens.TitleLarge,
                    lineHeight = AppleTokens.TitleLargeLineHeight,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${allSongs.size} tracks stored in local database",
                    fontSize = AppleTokens.ItemSubtitle,
                    color = AppleTokens.Metadata
                )
            }

            // Quick Filter Box
            Row(
                modifier = Modifier
                    .width(260.dp)
                    .height(40.dp)
                    .liquidGlass(
                        config = glassConfig,
                        shape = ContinuousRoundedRectangle(AppleTokens.Control)
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ConvxIcons.Search,
                    contentDescription = null,
                    tint = AppleTokens.Metadata,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (filterText.isEmpty()) {
                        Text(
                            text = "Filter songs...",
                            color = AppleTokens.Metadata,
                            fontSize = 13.sp
                        )
                    }
                    BasicTextField(
                        value = filterText,
                        onValueChange = { filterText = it },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 13.sp
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(AppleTokens.AccentRed),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (filteredSongs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = ConvxIcons.Library,
                        contentDescription = null,
                        tint = AppleTokens.Metadata.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (filterText.isNotEmpty()) "No matching songs found" else "No songs in library",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (filterText.isNotEmpty()) "Try searching for a different keyword" else "Play any song from Home or Search to populate your library.",
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
                itemsIndexed(filteredSongs) { index, song ->
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

                // Buffer space for bottom floating dock
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}
