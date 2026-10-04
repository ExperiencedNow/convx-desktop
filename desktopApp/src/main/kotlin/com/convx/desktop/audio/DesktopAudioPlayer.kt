package com.convx.desktop.audio

import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter

data class MediaTrack(
    val id: String,
    val title: String,
    val artists: String,
    val durationText: String = "",
    val durationSeconds: Int = 0,
    val thumbnailUrl: String = ""
)

enum class PlaybackStatus {
    IDLE,
    BUFFERING,
    PLAYING,
    PAUSED,
    ERROR
}

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

/**
 * Robust desktop audio player integrating vlcj (LibVLC) and LocalStreamProxy
 * with YouTube InnerTube stream resolution and reactive StateFlows.
 */
class DesktopAudioPlayer(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
) {
    private val proxy = LocalStreamProxy()
    private var factory: MediaPlayerFactory? = null
    private var mediaPlayer: MediaPlayer? = null
    private var positionTickerJob: Job? = null

    private val _status = MutableStateFlow(PlaybackStatus.IDLE)
    val status: StateFlow<PlaybackStatus> = _status.asStateFlow()

    private val _currentTrack = MutableStateFlow<MediaTrack?>(null)
    val currentTrack: StateFlow<MediaTrack?> = _currentTrack.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _volume = MutableStateFlow(0.8f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _queue = MutableStateFlow<List<MediaTrack>>(emptyList())
    val queue: StateFlow<List<MediaTrack>> = _queue.asStateFlow()

    private val _queueIndex = MutableStateFlow(0)
    val queueIndex: StateFlow<Int> = _queueIndex.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    init {
        initializeEngine()
    }

    private fun initializeEngine() {
        try {
            NativeDiscovery().discover()
            val userAgent = YouTubeClient.IOS.userAgent
            val f = MediaPlayerFactory(
                "--no-video",
                "--intf=dummy",
                "--no-stats",
                "--http-user-agent=$userAgent"
            )
            val mp = f.mediaPlayers().newMediaPlayer()
            mp.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
                override fun playing(mediaPlayer: MediaPlayer) {
                    _status.value = PlaybackStatus.PLAYING
                }

                override fun paused(mediaPlayer: MediaPlayer) {
                    _status.value = PlaybackStatus.PAUSED
                }

                override fun stopped(mediaPlayer: MediaPlayer) {
                    _status.value = PlaybackStatus.IDLE
                }

                override fun finished(mediaPlayer: MediaPlayer) {
                    onTrackFinished()
                }

                override fun error(mediaPlayer: MediaPlayer) {
                    _status.value = PlaybackStatus.ERROR
                }

                override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
                    _positionMs.value = newTime
                }

                override fun lengthChanged(mediaPlayer: MediaPlayer, newLength: Long) {
                    if (newLength > 0) {
                        _durationMs.value = newLength
                    }
                }
            })

            proxy.start()
            mp.audio().setVolume((_volume.value * 100).toInt())
            factory = f
            mediaPlayer = mp
            startPositionTicker()
        } catch (e: Exception) {
            println("[DesktopAudioPlayer] Engine initialization failed: ${e.message}")
            _status.value = PlaybackStatus.ERROR
        }
    }

    private fun startPositionTicker() {
        positionTickerJob?.cancel()
        positionTickerJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.status().isPlaying) {
                        val pos = mp.status().time()
                        val len = mp.status().length()
                        if (pos >= 0) _positionMs.value = pos
                        if (len > 0) _durationMs.value = len
                    }
                }
                delay(250)
            }
        }
    }

    fun playTrack(track: MediaTrack) {
        _queue.value = listOf(track)
        _queueIndex.value = 0
        loadAndPlay(track)
    }

    fun playQueue(tracks: List<MediaTrack>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        _queue.value = tracks
        _queueIndex.value = startIndex.coerceIn(0, tracks.size - 1)
        loadAndPlay(tracks[_queueIndex.value])
    }

    fun setQueue(tracks: List<MediaTrack>, startIndex: Int = 0) = playQueue(tracks, startIndex)

    private fun loadAndPlay(track: MediaTrack) {
        _currentTrack.value = track
        _status.value = PlaybackStatus.BUFFERING
        _positionMs.value = 0L
        _durationMs.value = track.durationSeconds * 1000L

        scope.launch {
            try {
                val streamUrl = withContext(Dispatchers.IO) {
                    resolveStreamUrl(track.id)
                }

                if (streamUrl != null) {
                    val proxyUrl = proxy.registerStream(streamUrl, YouTubeClient.IOS.userAgent)
                    mediaPlayer?.media()?.play(proxyUrl)
                } else {
                    println("[DesktopAudioPlayer] Failed to resolve audio stream for track ${track.id}")
                    _status.value = PlaybackStatus.ERROR
                }
            } catch (e: Exception) {
                println("[DesktopAudioPlayer] Playback error: ${e.message}")
                _status.value = PlaybackStatus.ERROR
            }
        }
    }

    private suspend fun resolveStreamUrl(videoId: String): String? {
        val client = YouTubeClient.IOS
        val response = YouTube.player(videoId, client = client).getOrNull() ?: return null
        val streamingData = response.streamingData ?: return null

        val format = streamingData.adaptiveFormats
            .filter { it.isAudio }
            .maxByOrNull { it.bitrate }
            ?: streamingData.formats?.firstOrNull()

        return format?.url
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.status().isPlaying) {
            mp.controls().pause()
            _status.value = PlaybackStatus.PAUSED
        } else {
            if (_currentTrack.value != null) {
                mp.controls().play()
                _status.value = PlaybackStatus.PLAYING
            } else if (_queue.value.isNotEmpty()) {
                loadAndPlay(_queue.value[_queueIndex.value])
            }
        }
    }

    fun pause() {
        mediaPlayer?.controls()?.pause()
        _status.value = PlaybackStatus.PAUSED
    }

    fun resume() {
        mediaPlayer?.controls()?.play()
        _status.value = PlaybackStatus.PLAYING
    }

    fun stop() {
        mediaPlayer?.controls()?.stop()
        _status.value = PlaybackStatus.IDLE
        _positionMs.value = 0L
    }

    fun seekTo(fraction: Float) {
        val len = _durationMs.value
        if (len > 0) {
            val targetMs = (len * fraction.coerceIn(0f, 1f)).toLong()
            seekToMs(targetMs)
        }
    }

    fun seekToMs(positionMs: Long) {
        mediaPlayer?.controls()?.setTime(positionMs)
        _positionMs.value = positionMs
    }

    fun skipNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val nextIdx = (_queueIndex.value + 1) % q.size
        _queueIndex.value = nextIdx
        loadAndPlay(q[nextIdx])
    }

    fun skipPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        // If > 3 seconds in, seek to beginning; otherwise go to previous
        if (_positionMs.value > 3000L) {
            seekToMs(0L)
        } else {
            val prevIdx = if (_queueIndex.value - 1 < 0) q.size - 1 else _queueIndex.value - 1
            _queueIndex.value = prevIdx
            loadAndPlay(q[prevIdx])
        }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        if (clamped > 0f && _isMuted.value) {
            _isMuted.value = false
        }
        val targetVol = if (_isMuted.value) 0 else (clamped * 100).toInt()
        mediaPlayer?.audio()?.setVolume(targetVol)
    }

    fun toggleMute() {
        val newMuted = !_isMuted.value
        _isMuted.value = newMuted
        val targetVol = if (newMuted) 0 else (_volume.value * 100).toInt()
        mediaPlayer?.audio()?.setVolume(targetVol)
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun cycleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    private fun onTrackFinished() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                _currentTrack.value?.let { loadAndPlay(it) }
            }
            RepeatMode.ALL -> {
                skipNext()
            }
            RepeatMode.OFF -> {
                if (_queueIndex.value < _queue.value.size - 1) {
                    skipNext()
                } else {
                    _status.value = PlaybackStatus.IDLE
                }
            }
        }
    }

    fun release() {
        positionTickerJob?.cancel()
        mediaPlayer?.controls()?.stop()
        mediaPlayer?.release()
        factory?.release()
        proxy.stop()
    }
}
