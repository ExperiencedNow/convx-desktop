package com.convx.desktop.db

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convx.desktop.audio.RepeatMode
import com.convx.desktop.ui.component.GlassStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class SettingsManager(
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { StoragePaths.preferencesFile }
    )
) {
    companion object {
        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager().also { instance = it }
            }
        }

        private val KEY_VOLUME = floatPreferencesKey("volume")
        private val KEY_IS_MUTED = booleanPreferencesKey("is_muted")
        private val KEY_REPEAT_MODE = stringPreferencesKey("repeat_mode")
        private val KEY_IS_SHUFFLE = booleanPreferencesKey("is_shuffle")
        private val KEY_GLASS_VIBRANCY = floatPreferencesKey("glass_vibrancy")
        private val KEY_GLASS_BLUR_RADIUS = floatPreferencesKey("glass_blur_radius")
        private val KEY_GLASS_LENS_HEIGHT = floatPreferencesKey("glass_lens_height")
        private val KEY_GLASS_LENS_AMOUNT = floatPreferencesKey("glass_lens_amount")
        private val KEY_GLASS_STYLE = stringPreferencesKey("glass_style")
        private val KEY_PURE_BLACK = booleanPreferencesKey("pure_black")
        private val KEY_DISCORD_RPC_ENABLED = booleanPreferencesKey("discord_rpc_enabled")
        private val KEY_DISCORD_TOKEN = stringPreferencesKey("discord_token")
        private val KEY_LASTFM_ENABLED = booleanPreferencesKey("lastfm_enabled")
        private val KEY_LASTFM_SESSION_KEY = stringPreferencesKey("lastfm_session_key")
        private val KEY_LASTFM_USERNAME = stringPreferencesKey("lastfm_username")
    }

    val volume: Flow<Float> = dataStore.data.map { it[KEY_VOLUME] ?: 0.75f }
    val isMuted: Flow<Boolean> = dataStore.data.map { it[KEY_IS_MUTED] ?: false }
    val repeatMode: Flow<RepeatMode> = dataStore.data.map {
        try {
            RepeatMode.valueOf(it[KEY_REPEAT_MODE] ?: "OFF")
        } catch (_: Exception) {
            RepeatMode.OFF
        }
    }
    val isShuffle: Flow<Boolean> = dataStore.data.map { it[KEY_IS_SHUFFLE] ?: false }
    val glassVibrancy: Flow<Float> = dataStore.data.map { it[KEY_GLASS_VIBRANCY] ?: 1.2f }
    val glassBlurRadius: Flow<Float> = dataStore.data.map { it[KEY_GLASS_BLUR_RADIUS] ?: 2f }
    val glassLensHeight: Flow<Float> = dataStore.data.map { it[KEY_GLASS_LENS_HEIGHT] ?: 0.4f }
    val glassLensAmount: Flow<Float> = dataStore.data.map { it[KEY_GLASS_LENS_AMOUNT] ?: 0.6f }
    val glassStyle: Flow<GlassStyle> = dataStore.data.map {
        try {
            GlassStyle.valueOf(it[KEY_GLASS_STYLE] ?: "LIQUID")
        } catch (_: Exception) {
            GlassStyle.LIQUID
        }
    }
    val pureBlack: Flow<Boolean> = dataStore.data.map { it[KEY_PURE_BLACK] ?: false }
    val discordRpcEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_DISCORD_RPC_ENABLED] ?: false }
    val discordToken: Flow<String> = dataStore.data.map { it[KEY_DISCORD_TOKEN] ?: "" }
    val lastfmEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_LASTFM_ENABLED] ?: false }
    val lastfmSessionKey: Flow<String> = dataStore.data.map { it[KEY_LASTFM_SESSION_KEY] ?: "" }
    val lastfmUsername: Flow<String> = dataStore.data.map { it[KEY_LASTFM_USERNAME] ?: "" }

    suspend fun setVolume(vol: Float) {
        dataStore.edit { it[KEY_VOLUME] = vol.coerceIn(0f, 1f) }
    }

    suspend fun setMuted(muted: Boolean) {
        dataStore.edit { it[KEY_IS_MUTED] = muted }
    }

    suspend fun setRepeatMode(mode: RepeatMode) {
        dataStore.edit { it[KEY_REPEAT_MODE] = mode.name }
    }

    suspend fun setShuffle(shuffle: Boolean) {
        dataStore.edit { it[KEY_IS_SHUFFLE] = shuffle }
    }

    suspend fun setGlassVibrancy(vibrancy: Float) {
        dataStore.edit { it[KEY_GLASS_VIBRANCY] = vibrancy }
    }

    suspend fun setGlassBlurRadius(blurRadius: Float) {
        dataStore.edit { it[KEY_GLASS_BLUR_RADIUS] = blurRadius }
    }

    suspend fun setGlassStyle(style: GlassStyle) {
        dataStore.edit { it[KEY_GLASS_STYLE] = style.name }
    }

    suspend fun setPureBlack(pureBlack: Boolean) {
        dataStore.edit { it[KEY_PURE_BLACK] = pureBlack }
    }

    suspend fun setDiscordRpcEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_DISCORD_RPC_ENABLED] = enabled }
    }

    suspend fun setDiscordToken(token: String) {
        dataStore.edit { it[KEY_DISCORD_TOKEN] = token }
    }

    suspend fun setLastfmEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_LASTFM_ENABLED] = enabled }
    }

    suspend fun setLastfmSession(sessionKey: String, username: String) {
        dataStore.edit {
            it[KEY_LASTFM_SESSION_KEY] = sessionKey
            it[KEY_LASTFM_USERNAME] = username
        }
    }
}
