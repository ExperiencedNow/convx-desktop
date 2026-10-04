package com.convx.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    val windowState = WindowState(size = DpSize(1200.dp, 800.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "Convx Desktop (Nightly r52)",
        state = windowState
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme(
                background = Color(0xFF0A0A0C),
                surface = Color(0xFF141416),
                primary = Color(0xFFFF2D55),
                onBackground = Color.White,
                onSurface = Color.White
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0A0A0C)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Ambient gradient background
                    Box(
                        modifier = Modifier
                            .size(500.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x33FF2D55),
                                        Color(0x1A8E2DE2),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    // Card with Convx liquid glass styling
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0x33FFFFFF))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0x66FFFFFF),
                                        Color(0x1AFFFFFF)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 48.dp, vertical = 40.dp)
                    ) {
                        Text(
                            text = "Convx Desktop",
                            fontSize = 32.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Port of Nightly r52 · Compose Multiplatform (JVM/Skia)",
                            fontSize = 14.sp,
                            color = Color(0xAAFFFFFF)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0x22FFFFFF))
                                .border(0.8.dp, Color(0x44FFFFFF), RoundedCornerShape(50))
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF34C759), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Phase 0 Gate: Compose Window Active",
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
