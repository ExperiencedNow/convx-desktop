package com.convx.desktop.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.convx.desktop.ui.component.GlassEffectConfig
import com.convx.desktop.ui.component.GlassStyle
import com.convx.desktop.ui.component.glassContentColorFor
import com.convx.desktop.ui.component.glassResolutionScale
import com.convx.desktop.ui.component.glassSaturation
import com.convx.desktop.ui.component.icons.ConvxIcons
import com.convx.desktop.ui.component.shouldUseTranslucentGlassFallback
import com.convx.desktop.ui.theme.AppleTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase3FoundationTest {

    @Test
    fun testAppleTokensAndContrastHelpers() {
        println("=== PHASE 3 TEST: APPLE TOKENS & CONTRAST HELPERS ===")
        assertEquals(Color(0xFFFA2D48), AppleTokens.AccentRed)
        assertEquals(20.dp, AppleTokens.Gutter)
        assertEquals(158f, AppleTokens.Motion.Stiffness, 0.01f)

        // Dark background contrast
        val darkBg = Color(0xFF121212)
        val onDark = AppleTokens.onColor(darkBg)
        println("onDark for #121212 -> $onDark")
        assertNotNull(onDark)

        // Vibrant / light background contrast
        val redBg = Color(0xFFE52D27)
        val onRed = AppleTokens.onColor(redBg)
        val onRedHeading = AppleTokens.onColorHeading(redBg)
        println("onRed for #E52D27 -> $onRed, onRedHeading -> $onRedHeading")
        assertNotNull(onRed)
        assertNotNull(onRedHeading)

        // White background contrast
        val onWhite = AppleTokens.onColor(Color.White)
        println("onWhite -> $onWhite")
        assertNotNull(onWhite)
    }

    @Test
    fun testContinuousRoundedRectangleOutline() {
        println("=== PHASE 3 TEST: CONTINUOUS ROUNDED RECTANGLE SQUIRCLE ===")
        val squircle = com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle(22.dp)
        val outline = squircle.createOutline(
            size = Size(300f, 120f),
            layoutDirection = LayoutDirection.Ltr,
            density = Density(2f)
        )
        assertTrue("Outline should be Generic (Path-based squircle curve)", outline is Outline.Generic)
        println("SUCCESS: Continuous squircle path outline generated successfully!")
    }

    @Test
    fun testGlassEffectConfigAndFormulas() {
        println("=== PHASE 3 TEST: GLASS EFFECT CONFIG & RESOLUTION SCALE ===")
        val config = GlassEffectConfig()
        assertTrue(config.globalEnabled)
        assertEquals(2f, config.blurRadius, 0.01f)
        assertEquals(GlassStyle.LIQUID, config.style)

        val sidePanelConfig = config.forSidePanel()
        assertEquals(config.sidePanelVibrancy, sidePanelConfig.vibrancy, 0.01f)
        assertEquals(config.sidePanelBlurRadius, sidePanelConfig.blurRadius, 0.01f)

        // Test glassResolutionScale ramp
        val scale0 = glassResolutionScale(0f)
        val scale2 = glassResolutionScale(2f)
        val scale8 = glassResolutionScale(8f)
        val scale16 = glassResolutionScale(16f)

        assertEquals(1f, scale0, 0.001f)
        assertTrue("scale at 2dp should be between 0.3 and 1.0", scale2 in 0.3f..1.0f)
        assertEquals(0.30f, scale8, 0.001f)
        assertEquals(0.30f, scale16, 0.001f)
        println("glassResolutionScale: 0dp=$scale0, 2dp=$scale2, 8dp=$scale8, 16dp=$scale16")

        // Test saturation multiplier
        assertEquals(1.5f, glassSaturation(1f), 0.01f)
        assertEquals(1.6f, glassSaturation(1.2f), 0.01f)

        // Test glassContentColorFor
        val textOnDarkGlass = glassContentColorFor(Color(0xFF121212), Color(0xFF1A1A1A), 0.5f)
        assertEquals(Color.White, textOnDarkGlass)

        val textOnLightGlass = glassContentColorFor(Color.White, Color.White, 0.8f)
        assertEquals(Color(0xFF1A1A1A), textOnLightGlass)

        // Test fallback logic
        assertTrue(shouldUseTranslucentGlassFallback(GlassStyle.TRANSPARENT, true))
        assertTrue(shouldUseTranslucentGlassFallback(GlassStyle.LIQUID, false))
        assertTrue(!shouldUseTranslucentGlassFallback(GlassStyle.LIQUID, true))
    }

    @Test
    fun testConvxIconsInitialization() {
        println("=== PHASE 3 TEST: CONVX VECTOR ICONS ===")
        val icons = listOf(
            "Home" to ConvxIcons.Home,
            "Search" to ConvxIcons.Search,
            "Library" to ConvxIcons.Library,
            "Settings" to ConvxIcons.Settings,
            "Play" to ConvxIcons.Play,
            "Pause" to ConvxIcons.Pause,
            "SkipForward" to ConvxIcons.SkipForward,
            "SkipBack" to ConvxIcons.SkipBack,
            "VolumeUp" to ConvxIcons.VolumeUp,
            "VolumeMute" to ConvxIcons.VolumeMute,
            "Heart" to ConvxIcons.Heart,
            "HeartFilled" to ConvxIcons.HeartFilled,
            "Shuffle" to ConvxIcons.Shuffle,
            "Repeat" to ConvxIcons.Repeat,
            "WindowMinimize" to ConvxIcons.WindowMinimize,
            "WindowMaximize" to ConvxIcons.WindowMaximize,
            "WindowRestore" to ConvxIcons.WindowRestore,
            "WindowClose" to ConvxIcons.WindowClose
        )

        icons.forEach { (name, icon) ->
            assertNotNull("Icon $name should not be null", icon)
            val expectedSize = if (name.startsWith("Window")) 12.dp else 24.dp
            assertEquals(expectedSize, icon.defaultWidth)
            println("Checked icon: $name (${icon.defaultWidth}x${icon.defaultHeight})")
        }
        println("SUCCESS: All 18 Convx desktop vector icons verified!")
    }
}
