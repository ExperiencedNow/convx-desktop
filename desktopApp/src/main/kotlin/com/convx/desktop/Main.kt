package com.convx.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.convx.desktop.audio.DesktopAudioPlayer
import com.convx.desktop.ui.shell.DesktopShell
import com.convx.desktop.ui.theme.AppleTokens
import com.convx.desktop.ui.theme.AppShapes

fun main() = application {
    val windowState = WindowState(size = DpSize(1280.dp, 840.dp))
    val player = remember { DesktopAudioPlayer() }

    val handleClose = {
        try {
            player.release()
        } catch (_: Exception) {}
        exitApplication()
    }

    Window(
        onCloseRequest = handleClose,
        title = "Convx Desktop (Nightly r52)",
        state = windowState,
        undecorated = true
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme(
                background = AppleTokens.Bg,
                surface = AppleTokens.Card,
                primary = AppleTokens.AccentRed,
                onBackground = Color.White,
                onSurface = Color.White
            ),
            shapes = AppShapes
        ) {
            DesktopShell(
                windowState = windowState,
                onClose = handleClose,
                player = player
            )
        }
    }
}
