package com.convx.desktop.integrations

import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.rpc.RpcImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DesktopDiscordRpc private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var rpcClient: KizzyRPC? = null
    private var currentToken: String = ""
    private var isEnabled: Boolean = false
    private var activeJob: Job? = null

    fun updateConfig(enabled: Boolean, token: String) {
        isEnabled = enabled
        if (token != currentToken || !enabled) {
            currentToken = token
            activeJob?.cancel()
            scope.launch {
                try {
                    rpcClient?.close()
                } catch (_: Exception) {}
                rpcClient = null
            }
        }

        if (enabled && token.isNotBlank() && rpcClient == null) {
            rpcClient = KizzyRPC(
                token = token,
                os = "Windows",
                browser = "Convx Desktop",
                device = "Desktop PC",
                userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
            )
        }
    }

    fun updatePresence(
        title: String,
        artist: String,
        durationMs: Long,
        positionMs: Long,
        isPlaying: Boolean
    ) {
        if (!isEnabled || currentToken.isBlank()) return

        activeJob?.cancel()
        activeJob = scope.launch {
            try {
                val client = rpcClient ?: KizzyRPC(
                    token = currentToken,
                    os = "Windows",
                    browser = "Convx Desktop",
                    device = "Desktop PC"
                ).also { rpcClient = it }

                if (!isPlaying) {
                    client.setActivity(
                        name = "Convx Music",
                        state = "Paused: $artist",
                        details = title,
                        largeImage = RpcImage.DiscordImage("app_icon"),
                        smallImage = null,
                        largeText = "Convx Desktop",
                        type = KizzyRPC.Type.LISTENING
                    )
                } else {
                    val now = System.currentTimeMillis()
                    val startTime = now - positionMs
                    val endTime = if (durationMs > 0) startTime + durationMs else null

                    client.setActivity(
                        name = "Convx Music",
                        state = "by $artist",
                        details = title,
                        largeImage = RpcImage.DiscordImage("app_icon"),
                        smallImage = null,
                        largeText = "Convx Desktop",
                        startTime = startTime,
                        endTime = endTime,
                        type = KizzyRPC.Type.LISTENING
                    )
                }
            } catch (_: Exception) {
                // Ignore transient network errors or closed socket
            }
        }
    }

    fun clearPresence() {
        activeJob?.cancel()
        if (isEnabled && rpcClient != null) {
            scope.launch {
                try {
                    rpcClient?.close()
                } catch (_: Exception) {}
            }
        }
    }

    fun shutdown() {
        activeJob?.cancel()
        try {
            rpcClient?.closeRPC()
        } catch (_: Exception) {}
        rpcClient = null
    }

    companion object {
        @Volatile
        private var instance: DesktopDiscordRpc? = null

        fun getInstance(): DesktopDiscordRpc {
            return instance ?: synchronized(this) {
                instance ?: DesktopDiscordRpc().also { instance = it }
            }
        }
    }
}
