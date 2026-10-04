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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.desktop.audio.DesktopAudioPlayer
import com.convx.desktop.audio.MediaTrack
import com.convx.desktop.db.ConvxDatabase
import com.convx.desktop.db.SearchHistoryRecord
import com.convx.desktop.db.SongRecord
import com.convx.desktop.ui.component.AsyncArtwork
import com.convx.desktop.ui.component.GlassEffectConfig
import com.convx.desktop.ui.component.icons.ConvxIcons
import com.convx.desktop.ui.component.liquidGlass
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.desktop.ui.theme.AppleTokens
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(
    player: DesktopAudioPlayer?,
    database: ConvxDatabase,
    glassConfig: GlassEffectConfig,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<MediaTrack>>(emptyList()) }
    var hasSearched by remember { mutableStateOf(false) }

    val recentQueries by database.searchHistoryDao().getRecentQueries(8).collectAsState(initial = emptyList())
    val currentTrack by (player?.currentTrack?.collectAsState() ?: remember { mutableStateOf(null) })
    val likedSongs by database.songDao().getLikedSongs().collectAsState(initial = emptyList())
    val likedSongIds = remember(likedSongs) { likedSongs.map { it.id }.toSet() }

    val performSearch: (String) -> Unit = { q ->
        if (q.isNotBlank()) {
            isLoading = true
            hasSearched = true
            scope.launch {
                // Record search query in database
                database.searchHistoryDao().insertQuery(
                    SearchHistoryRecord(query = q.trim(), timestamp = System.currentTimeMillis())
                )
                val results = withContext(Dispatchers.IO) {
                    try {
                        val response = YouTube.search(q.trim(), YouTube.SearchFilter.FILTER_SONG).getOrNull()
                        response?.items.orEmpty().filterIsInstance<SongItem>().map { item ->
                            val durationSecs = item.duration ?: 0
                            val minutes = durationSecs / 60
                            val seconds = durationSecs % 60
                            MediaTrack(
                                id = item.id,
                                title = item.title,
                                artists = item.artists.joinToString(", ") { it.name },
                                durationText = if (durationSecs > 0) "%d:%02d".format(minutes, seconds) else "",
                                durationSeconds = durationSecs,
                                thumbnailUrl = item.thumbnail
                            )
                        }
                    } catch (_: Exception) {
                        emptyList()
                    }
                }
                searchResults = results
                isLoading = false
            }
        }
    }

    val playTrack: (MediaTrack) -> Unit = { track ->
        player?.playTrack(track)
        scope.launch {
            val isLiked = likedSongIds.contains(track.id)
            val record = SongRecord(
                id = track.id,
                title = track.title,
                artists = track.artists,
                durationSeconds = track.durationSeconds,
                durationText = track.durationText,
                thumbnailUrl = track.thumbnailUrl,
                isLiked = isLiked,
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
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        // Page Header
        Text(
            text = "Search",
            fontSize = AppleTokens.TitleLarge,
            lineHeight = AppleTokens.TitleLargeLineHeight,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Search YouTube Music catalog · Instant stream resolution",
            fontSize = AppleTokens.ItemSubtitle,
            color = AppleTokens.Metadata
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Liquid Glass Search Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .liquidGlass(
                    config = glassConfig,
                    shape = ContinuousRoundedRectangle(AppleTokens.CardCorner)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ConvxIcons.Search,
                contentDescription = "Search Icon",
                tint = AppleTokens.Metadata,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Songs, artists, or albums...",
                        color = AppleTokens.Metadata,
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(AppleTokens.AccentRed),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { performSearch(query) }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(ContinuousRoundedRectangle(8.dp))
                        .clickable { query = "" },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ConvxIcons.WindowClose,
                        contentDescription = "Clear",
                        tint = AppleTokens.Metadata,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Search Action Button
            Box(
                modifier = Modifier
                    .clip(ContinuousRoundedRectangle(10.dp))
                    .background(AppleTokens.AccentRed)
                    .clickable { performSearch(query) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Search Queries Chips
        if (recentQueries.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Searches:",
                    fontSize = 13.sp,
                    color = AppleTokens.Metadata,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(10.dp))
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentQueries) { item ->
                        Row(
                            modifier = Modifier
                                .clip(ContinuousRoundedRectangle(8.dp))
                                .background(AppleTokens.Card)
                                .clickable {
                                    query = item
                                    performSearch(item)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = ConvxIcons.WindowClose,
                                contentDescription = "Delete query",
                                tint = AppleTokens.Metadata,
                                modifier = Modifier
                                    .size(10.dp)
                                    .clickable {
                                        scope.launch {
                                            database.searchHistoryDao().deleteQuery(item)
                                        }
                                    }
                            )
                        }
                    }
                }

                Text(
                    text = "Clear All",
                    fontSize = 12.sp,
                    color = AppleTokens.AccentRed,
                    modifier = Modifier.clickable {
                        scope.launch { database.searchHistoryDao().clearHistory() }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Content / Results State
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AppleTokens.AccentRed, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Searching InnerTube...",
                            color = AppleTokens.Metadata,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            hasSearched && searchResults.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tracks found for \"$query\". Try a different query.",
                        color = AppleTokens.Metadata,
                        fontSize = 15.sp
                    )
                }
            }

            searchResults.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { track ->
                        val isPlaying = currentTrack?.id == track.id
                        val isLiked = likedSongIds.contains(track.id)

                        TrackRowItem(
                            track = track,
                            isPlaying = isPlaying,
                            isLiked = isLiked,
                            onPlay = { playTrack(track) },
                            onToggleLike = {
                                scope.launch {
                                    val newLiked = !isLiked
                                    val record = SongRecord(
                                        id = track.id,
                                        title = track.title,
                                        artists = track.artists,
                                        durationSeconds = track.durationSeconds,
                                        durationText = track.durationText,
                                        thumbnailUrl = track.thumbnailUrl,
                                        isLiked = newLiked,
                                        playCount = 1,
                                        lastPlayedTime = System.currentTimeMillis()
                                    )
                                    database.songDao().insertOrUpdate(record)
                                    database.songDao().setLiked(track.id, newLiked)
                                }
                            }
                        )
                    }

                    // Leave space for floating dock
                    item {
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            }

            else -> {
                // Initial prompt before search
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = ConvxIcons.Search,
                            contentDescription = null,
                            tint = AppleTokens.Metadata.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Explore millions of songs and artists",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Type a search query above to begin listening",
                            color = AppleTokens.Metadata,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackRowItem(
    track: MediaTrack,
    isPlaying: Boolean,
    isLiked: Boolean,
    onPlay: () -> Unit,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(ContinuousRoundedRectangle(12.dp))
            .background(if (isPlaying) AppleTokens.AccentRed.copy(alpha = 0.15f) else AppleTokens.Card)
            .clickable(onClick = onPlay)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork
        AsyncArtwork(
            url = track.thumbnailUrl,
            modifier = Modifier.size(46.dp),
            shape = ContinuousRoundedRectangle(AppleTokens.Artwork)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title & Artist
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPlaying) AppleTokens.AccentRed else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artists,
                fontSize = 12.sp,
                color = AppleTokens.Metadata,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Duration
        if (track.durationText.isNotEmpty()) {
            Text(
                text = track.durationText,
                fontSize = 12.sp,
                color = AppleTokens.Metadata
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Like / Favorite Heart Button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(ContinuousRoundedRectangle(8.dp))
                .clickable(onClick = onToggleLike),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLiked) ConvxIcons.HeartFilled else ConvxIcons.Heart,
                contentDescription = "Like",
                tint = if (isLiked) AppleTokens.AccentRed else AppleTokens.Metadata,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Play Button
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(ContinuousRoundedRectangle(8.dp))
                .background(if (isPlaying) AppleTokens.AccentRed else Color(0x22FFFFFF))
                .clickable(onClick = onPlay),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) ConvxIcons.Pause else ConvxIcons.Play,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
