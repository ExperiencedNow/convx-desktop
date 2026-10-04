package com.convx.desktop.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.desktop.ui.component.shapes.ContinuousRoundedRectangle
import kotlin.math.abs

/**
 * Apple Music design tokens — the single source of truth for colors, shapes, and
 * adaptive contrast helpers. Every screen references these instead of hardcoding values.
 */
object AppleTokens {
    // Accent
    val AccentRed = Color(0xFFFA2D48)

    // Surfaces — Apple-style soft dark greys, not pure black (pure black stays an
    // opt-in via the PureBlack setting for OLED). Monotonic elevation ladder.
    val Bg = Color(0xFF121212)
    val BgElevated = Color(0xFF1A1A1A)
    val Card = Color(0xFF1C1C1E)
    val CardSecondary = Color(0xFF2C2C2E)

    /**
     * Hairline rule on whatever surface the caller sits on. Derived from the
     * content colour rather than fixed to white: a constant white rule is
     * invisible on the light theme, and on an artwork-tinted screen it reads as
     * a foreign line instead of one belonging to the surface.
     */
    val divider: Color
        @Composable get() = LocalContentColor.current.copy(alpha = 0.12f)

    /**
     * Metadata text — artist names, subtitles, durations. One step down from
     * on-surface, never full white: the hierarchy on Home is carried by this
     * contrast step rather than by size, since title and subtitle share a size.
     */
    val Metadata = Color(0xFF8E8E93)

    // Spacing — the whole scale. Screens reference these, never a dp literal.

    /** Horizontal screen gutter. The only gutter. */
    val Gutter = 20.dp
    /** Gap between siblings in a grid or list. */
    val ItemGap = 16.dp
    /** Gap between a section and the next section's header. */
    val SectionGap = 24.dp
    /** Gap between stacked text lines inside one item. */
    val TextGap = 2.dp

    // Type scale — Home's headers and tile text. Sizes are fixed rather than
    // taken from the Material scale: the design pins them to specific values and
    // the tile grid's alignment depends on title and subtitle matching exactly.

    // Each size carries its own line height. Compose otherwise derives leading from
    // the font's own metrics, which lands taller than the design at every step and
    // shows up as sections that drift out of alignment the further down you scroll.

    /** Screen title ("Listen Now"). */
    val TitleLarge = 34.sp
    val TitleLargeLineHeight = 41.sp
    /** Section header ("Recently Played"). */
    val SectionHeader = 22.sp
    val SectionHeaderLineHeight = 28.sp
    /** Tile title and list-row primary text. */
    val ItemTitle = 15.sp
    val ItemTitleLineHeight = 20.sp
    /** Tile subtitle, row metadata. */
    val ItemSubtitle = 13.sp
    val ItemSubtitleLineHeight = 18.sp
    /** Speed dial captions. */
    val Caption = 12.sp
    val CaptionLineHeight = 16.sp

    // Shapes — four corners, no more. Artwork is the only rounded thing on a
    // content tile; a card corner means the object is genuinely a card.
    // Every one of them is drawn with continuous (squircle) curvature — see
    // [AppShapes] — so nothing in the app mixes circular and continuous corners.
    val Artwork = 12.dp
    val Control = 12.dp
    val CardCorner = 22.dp
    val CardCornerLarge = 28.dp

    // Motion — one spring, three tempers, modelled on SwiftUI's `.smooth`: a spring
    // with a 0.5s natural period and zero bounce. That is the default here, and
    // bounce is reserved for selection feedback and hero entrances.
    //
    // Compose takes stiffness where SwiftUI takes a period: stiffness =
    // (2*pi / period)^2. SwiftUI `bounce` maps to `1 - dampingRatio`.
    object Motion {
        /** (2*pi / 0.5s)^2. The period behind every spec below. */
        const val Stiffness = 158f

        /** SwiftUI `.smooth` — the default for essentially everything. */
        fun <T> standard() = spring<T>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Stiffness)

        /** SwiftUI `.snappy` / `bounce: 0.15` — selection, toggles, emphasis. */
        fun <T> emphasis() = spring<T>(dampingRatio = 0.85f, stiffness = Stiffness)

        /** `bounce: 0.24` — hero and entrance transitions only. */
        fun <T> expressive() = spring<T>(dampingRatio = 0.76f, stiffness = Stiffness)

        /** SwiftUI `.easeInOut(duration: 0.2)` — cross-fades, where a spring has nothing to overshoot. */
        fun <T> fade() = tween<T>(durationMillis = 200)
    }

    // Adaptive contrast helpers

    /**
     * Primary text/icon color for content sitting on [bg] — typically a screen's
     * own hero tint, extracted from its artwork.
     */
    fun onColor(bg: Color): Color = bg.shiftedForContrast(saturation = 0.16f, lightness = 0.96f to 0.10f)

    /**
     * Secondary text on [bg]: same hue, but the tint is allowed to show. Use for
     * subtitles, counts and metadata — the role that reads as washed-out grey
     * when it comes from Material's neutral palette.
     */
    fun onColorSecondary(bg: Color): Color =
        bg.shiftedForContrast(saturation = 0.42f, lightness = 0.76f to 0.34f)

    /**
     * Headings on [bg] — artist names, section titles, the top-bar title. Carries
     * the most tint of the three: a heading is short and large, so it can be
     * clearly the screen's colour without costing legibility the way body copy
     * would.
     */
    fun onColorHeading(bg: Color): Color =
        bg.shiftedForContrast(saturation = 0.55f, lightness = 0.88f to 0.18f)

    /**
     * @param lightness (on-dark, on-light) target lightness.
     */
    private fun Color.shiftedForContrast(saturation: Float, lightness: Pair<Float, Float>): Color {
        val hsl = FloatArray(3)
        colorToHSL(toArgb(), hsl)
        val onDark = luminance() <= 0.5f
        val target = if (onDark) lightness.first else lightness.second
        if (hsl[1] < 0.08f) {
            hsl[1] = 0f
            hsl[2] = if (target > 0.9f) 1f else if (target < 0.12f) 0.04f else target
        } else {
            hsl[1] = minOf(hsl[1], saturation)
            hsl[2] = target
        }
        return Color(hslToColor(hsl))
    }

    private fun colorToHSL(color: Int, outHsl: FloatArray) {
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val h: Float
        val s: Float
        val l = (max + min) / 2f

        if (max == min) {
            h = 0f
            s = 0f
        } else {
            s = if (l <= 0.5f) delta / (max + min) else delta / (2f - 2f * l)
            h = when (max) {
                r -> ((g - b) / delta + (if (g < b) 6f else 0f)) * 60f
                g -> ((b - r) / delta + 2f) * 60f
                else -> ((r - g) / delta + 4f) * 60f
            }
        }
        outHsl[0] = h.coerceIn(0f, 360f)
        outHsl[1] = s.coerceIn(0f, 1f)
        outHsl[2] = l.coerceIn(0f, 1f)
    }

    private fun hslToColor(hsl: FloatArray): Int {
        val h = hsl[0]
        val s = hsl[1]
        val l = hsl[2]

        val c = (1f - abs(2f * l - 1f)) * s
        val m = l - 0.5f * c
        val x = c * (1f - abs((h / 60f % 2f) - 1f))

        val hueSegment = (h.toInt() / 60) % 6
        var r = 0f
        var g = 0f
        var b = 0f

        when (hueSegment) {
            0 -> { r = c; g = x; b = 0f }
            1 -> { r = x; g = c; b = 0f }
            2 -> { r = 0f; g = c; b = x }
            3 -> { r = 0f; g = x; b = c }
            4 -> { r = x; g = 0f; b = c }
            5 -> { r = c; g = 0f; b = x }
        }

        val ir = ((r + m) * 255f + 0.5f).toInt().coerceIn(0, 255)
        val ig = ((g + m) * 255f + 0.5f).toInt().coerceIn(0, 255)
        val ib = ((b + m) * 255f + 0.5f).toInt().coerceIn(0, 255)

        return (0xFF shl 24) or (ir shl 16) or (ig shl 8) or ib
    }
}

/**
 * The M3 shape scale, bound to [AppleTokens]' four corners so that anything
 * reaching for `MaterialTheme.shapes` lands on the same vocabulary as code that
 * names the token directly.
 */
val AppShapes = Shapes(
    extraSmall = ContinuousRoundedRectangle(4.dp),
    small = ContinuousRoundedRectangle(AppleTokens.Artwork),
    medium = ContinuousRoundedRectangle(AppleTokens.Control),
    large = ContinuousRoundedRectangle(AppleTokens.CardCorner),
    extraLarge = ContinuousRoundedRectangle(AppleTokens.CardCornerLarge),
)
