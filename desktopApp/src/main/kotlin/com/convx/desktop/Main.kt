package com.convx.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.convx.desktop.ui.shell.DesktopShell
import com.convx.desktop.ui.theme.AppleTokens
import com.convx.desktop.ui.theme.AppShapes

fun main() = application {
    val windowState = WindowState(size = DpSize(1280.dp, 840.dp))
    Window(
        onCloseRequest = ::exitApplication,
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
                onClose = ::exitApplication
            )
        }
    }
}
