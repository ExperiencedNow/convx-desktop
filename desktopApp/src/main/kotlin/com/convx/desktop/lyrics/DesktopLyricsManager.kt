package com.convx.desktop.lyrics

import com.convx.music.betterlyrics.BetterLyrics
import com.music.kugou.KuGou
import com.music.lrclib.LrcLib
import com.music.simpmusic.SimpMusicLyrics
import com.music.youlyplus.YouLyPlus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

data class LyricLine(
    val timeMs: Long,
    val text: String
)

data class LyricsState(
    val trackId: String? = null,
    val title: String = "",
    val artist: String = "",
    val isLoading: Boolean = false,
    val lines: List<LyricLine> = emptyList(),
    val currentLineIndex: Int = -1,
    val provider: String? = null,
    val isSynced: Boolean = false,
    val errorMessage: String? = null
)

class DesktopLyricsManager private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var loadJob: Job? = null

    private val _lyricsState = MutableStateFlow(LyricsState())
    val lyricsState: StateFlow<LyricsState> = _lyricsState.asStateFlow()

    // In-memory cache for fast lookup during session: key = "artist_title"
    private val lyricsCache = ConcurrentHashMap<String, CachedLyrics>()

    private data class CachedLyrics(
        val lines: List<LyricLine>,
        val provider: String,
        val isSynced: Boolean
    )

    fun loadLyrics(
        trackId: String,
        title: String,
        artist: String,
        durationSeconds: Int = -1,
        album: String? = null
    ) {
        val cacheKey = buildCacheKey(title, artist)

        loadJob?.cancel()

        // Check cache first
        val cached = lyricsCache[cacheKey]
        if (cached != null) {
            _lyricsState.value = LyricsState(
                trackId = trackId,
                title = title,
                artist = artist,
                isLoading = false,
                lines = cached.lines,
                currentLineIndex = -1,
                provider = cached.provider,
                isSynced = cached.isSynced
            )
            return
        }

        _lyricsState.value = LyricsState(
            trackId = trackId,
            title = title,
            artist = artist,
            isLoading = true,
            lines = emptyList(),
            currentLineIndex = -1
        )

        loadJob = scope.launch {
            val result = fetchFromProviders(trackId, title, artist, durationSeconds, album)
            if (result != null) {
                lyricsCache[cacheKey] = result
                _lyricsState.update { current ->
                    if (current.trackId == trackId) {
                        current.copy(
                            isLoading = false,
                            lines = result.lines,
                            provider = result.provider,
                            isSynced = result.isSynced,
                            errorMessage = null
                        )
                    } else {
                        current
                    }
                }
            } else {
                _lyricsState.update { current ->
                    if (current.trackId == trackId) {
                        current.copy(
                            isLoading = false,
                            lines = emptyList(),
                            errorMessage = "Lyrics not available for this track"
                        )
                    } else {
                        current
                    }
                }
            }
        }
    }

    private suspend fun fetchFromProviders(
        trackId: String,
        title: String,
        artist: String,
        durationSeconds: Int,
        album: String?
    ): CachedLyrics? {
        // Strategy 1: SimpMusic (matches directly by YouTube videoId)
        if (trackId.isNotBlank()) {
            runCatching {
                SimpMusicLyrics.getLyrics(trackId, durationSeconds).getOrNull()
            }.getOrNull()?.let { lrcText ->
                val parsed = parseLrc(lrcText)
                if (parsed.lines.isNotEmpty()) {
                    return CachedLyrics(parsed.lines, "SimpMusic", parsed.isSynced)
                }
            }
        }

        // Strategy 2: LrcLib (high quality crowdsourced synced & plain lyrics)
        runCatching {
            LrcLib.getLyrics(title, artist, durationSeconds, album).getOrNull()
        }.getOrNull()?.let { lrcText ->
            val parsed = parseLrc(lrcText)
            if (parsed.lines.isNotEmpty()) {
                return CachedLyrics(parsed.lines, "LrcLib", parsed.isSynced)
            }
        }

        // Strategy 3: YouLyPlus (multi-server community mirror)
        runCatching {
            YouLyPlus.getLyrics(title, artist, durationSeconds, album, id = trackId).getOrNull()
        }.getOrNull()?.let { lrcText ->
            val parsed = parseLrc(lrcText)
            if (parsed.lines.isNotEmpty()) {
                return CachedLyrics(parsed.lines, "YouLyPlus", parsed.isSynced)
            }
        }

        // Strategy 4: BetterLyrics (TTML word-synced parsed to LRC)
        runCatching {
            BetterLyrics.getLyrics(title, artist, durationSeconds, album).getOrNull()
        }.getOrNull()?.let { lrcText ->
            val parsed = parseLrc(lrcText)
            if (parsed.lines.isNotEmpty()) {
                return CachedLyrics(parsed.lines, "BetterLyrics", parsed.isSynced)
            }
        }

        // Strategy 5: KuGou (Asian and international fallback)
        runCatching {
            KuGou.getLyrics(title, artist, durationSeconds, album).getOrNull()
        }.getOrNull()?.let { lrcText ->
            val parsed = parseLrc(lrcText)
            if (parsed.lines.isNotEmpty()) {
                return CachedLyrics(parsed.lines, "KuGou", parsed.isSynced)
            }
        }

        return null
    }

    fun updatePosition(positionMs: Long) {
        val state = _lyricsState.value
        if (!state.isSynced || state.lines.isEmpty()) return

        val lines = state.lines
        var activeIndex = -1

        // Find the last line where timeMs <= positionMs
        for (i in lines.indices) {
            if (lines[i].timeMs <= positionMs) {
                activeIndex = i
            } else {
                break
            }
        }

        if (activeIndex != state.currentLineIndex) {
            _lyricsState.update { it.copy(currentLineIndex = activeIndex) }
        }
    }

    fun clear() {
        loadJob?.cancel()
        _lyricsState.value = LyricsState()
    }

    private fun buildCacheKey(title: String, artist: String): String {
        return "${artist.trim().lowercase()}___${title.trim().lowercase()}"
    }

    companion object {
        @Volatile
        private var instance: DesktopLyricsManager? = null

        fun getInstance(): DesktopLyricsManager {
            return instance ?: synchronized(this) {
                instance ?: DesktopLyricsManager().also { instance = it }
            }
        }

        private val LRC_LINE_REGEX = Regex("""^\[(\d{1,2}):(\d{2})(?:[.:](\d{2,3}))?\](.*)$""")
        private val SYLLABLE_TAG_REGEX = Regex("""<[^>]+>""")
        private val METADATA_TAG_REGEX = Regex("""^\[(ti|ar|al|by|offset|length|re|ve):.*\]$""", RegexOption.IGNORE_CASE)

        data class ParsedLrc(
            val lines: List<LyricLine>,
            val isSynced: Boolean
        )

        fun parseLrc(lrcContent: String): ParsedLrc {
            val lines = lrcContent.lines()
            val parsedLines = mutableListOf<LyricLine>()
            var hasSynced = false

            for (rawLine in lines) {
                val trimmed = rawLine.trim()
                if (trimmed.isEmpty() || trimmed.matches(METADATA_TAG_REGEX)) {
                    continue
                }

                val match = LRC_LINE_REGEX.find(trimmed)
                if (match != null) {
                    val minutes = match.groupValues[1].toLongOrNull() ?: 0L
                    val seconds = match.groupValues[2].toLongOrNull() ?: 0L
                    val millisStr = match.groupValues[3]
                    val millis = when (millisStr.length) {
                        2 -> (millisStr.toLongOrNull() ?: 0L) * 10L
                        3 -> millisStr.toLongOrNull() ?: 0L
                        else -> 0L
                    }
                    val timeMs = minutes * 60_000L + seconds * 1000L + millis
                    // Strip any word/syllable tags from line text
                    val cleanText = match.groupValues[4]
                        .replace(SYLLABLE_TAG_REGEX, "")
                        .replace("{bg}", "")
                        .trim()

                    if (cleanText.isNotEmpty()) {
                        parsedLines.add(LyricLine(timeMs = timeMs, text = cleanText))
                        hasSynced = true
                    }
                } else {
                    // Plain line without timestamps
                    val cleanText = trimmed.replace(SYLLABLE_TAG_REGEX, "").trim()
                    if (cleanText.isNotEmpty()) {
                        parsedLines.add(LyricLine(timeMs = -1L, text = cleanText))
                    }
                }
            }

            // Sort synced lines by timestamp
            if (hasSynced) {
                parsedLines.sortBy { it.timeMs }
            }

            return ParsedLrc(
                lines = parsedLines,
                isSynced = hasSynced
            )
        }
    }
}
