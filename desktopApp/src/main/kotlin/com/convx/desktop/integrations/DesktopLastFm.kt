package com.convx.desktop.integrations

import com.music.lastfm.LastFM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DesktopLastFm private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isEnabled: Boolean = false
    private var currentSessionKey: String = ""

    fun updateConfig(enabled: Boolean, sessionKey: String) {
        isEnabled = enabled
        currentSessionKey = sessionKey
        LastFM.sessionKey = sessionKey.takeIf { it.isNotBlank() }
    }

    fun updateNowPlaying(
        title: String,
        artist: String,
        album: String? = null,
        durationSeconds: Int? = null
    ) {
        if (!isEnabled || currentSessionKey.isBlank() || !LastFM.isInitialized()) return

        scope.launch {
            runCatching {
                LastFM.updateNowPlaying(
                    artist = artist,
                    track = title,
                    album = album,
                    duration = durationSeconds
                )
            }
        }
    }

    fun scrobble(
        title: String,
        artist: String,
        durationSeconds: Int? = null,
        album: String? = null
    ) {
        if (!isEnabled || currentSessionKey.isBlank() || !LastFM.isInitialized()) return

        val timestamp = System.currentTimeMillis() / 1000L
        scope.launch {
            runCatching {
                LastFM.scrobble(
                    artist = artist,
                    track = title,
                    timestamp = timestamp,
                    album = album,
                    duration = durationSeconds
                )
            }
        }
    }

    companion object {
        @Volatile
        private var instance: DesktopLastFm? = null

        fun getInstance(): DesktopLastFm {
            return instance ?: synchronized(this) {
                instance ?: DesktopLastFm().also { instance = it }
            }
        }
    }
}
