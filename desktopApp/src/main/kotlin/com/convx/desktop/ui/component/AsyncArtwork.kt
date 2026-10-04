package com.convx.desktop.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.convx.desktop.ui.component.icons.ConvxIcons
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.desktop.ui.theme.AppleTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

object ArtworkCache {
    private val memoryCache = ConcurrentHashMap<String, ImageBitmap>()

    fun get(url: String): ImageBitmap? = memoryCache[url]
    fun put(url: String, bitmap: ImageBitmap) {
        memoryCache[url] = bitmap
    }
}

/**
 * High-performance async image loader for desktop with in-memory caching
 * and Apple-style squircle clip fallback.
 */
@Composable
fun AsyncArtwork(
    url: String?,
    modifier: Modifier = Modifier,
    shape: Shape = ContinuousRoundedRectangle(AppleTokens.Artwork),
    contentDescription: String? = null
) {
    var imageBitmap by remember(url) {
        mutableStateOf(if (!url.isNullOrBlank()) ArtworkCache.get(url) else null)
    }

    LaunchedEffect(url) {
        if (url.isNullOrBlank() || imageBitmap != null) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                val connection = URI(url).toURL().openConnection()
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                val bytes = connection.getInputStream().use { it.readBytes() }
                val skiaImage = Image.makeFromEncoded(bytes)
                val bitmap = skiaImage.toComposeImageBitmap()
                ArtworkCache.put(url, bitmap)
                imageBitmap = bitmap
            } catch (_: Exception) {
                // Ignore network timeouts or errors and keep fallback aesthetic gradient
            }
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2A0845),
                        Color(0xFF6441A5),
                        Color(0xFFFA2D48)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val currentBitmap = imageBitmap
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = ConvxIcons.Library,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
