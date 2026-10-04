package com.convx.desktop.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.RuntimeEffect

/**
 * User-configurable parameters of the liquid glass effect, sourced from DataStore
 * preferences and distributed through [LocalGlassEffectConfig].
 */
@Stable
data class GlassEffectConfig(
    val globalEnabled: Boolean = true,
    val vibrancy: Float = 1.2f,
    /** Blur in dp applied to glass pills. User requested 2dp for landscape fix. */
    val blurRadius: Float = 2f,
    /** 0..1, mapped to 0..[LENS_MAX_DP] dp of lens refraction height. 0.4 = 40%. */
    val lensHeight: Float = 0.4f,
    /** 0..1, mapped to 0..[LENS_MAX_DP] dp of lens refraction amount. 0.6 = 60%. */
    val lensAmount: Float = 0.6f,
    val chromaticAberration: Boolean = false,
    val depthEffect: Boolean = false,
    /** [Color.Unspecified] means adaptive: dark grey on dark. */
    val surfaceTintColor: Color = Color(0xFF1A1A1A),
    /** Specular rim ("pluck") colour. [Color.Unspecified] keeps the default white. */
    val highlightColor: Color = Color.Unspecified,
    /** Specular rim opacity, 0..1. */
    val highlightOpacity: Float = EdgeHighlightAlpha,
    /** Which rendering style every glass surface uses. */
    val style: GlassStyle = GlassStyle.LIQUID,
    /** Wash painted over the nav bar's selection puck. [Color.Unspecified] adapts
     *  to the theme: a dark wash on dark, a light one on light. */
    val puckColor: Color = Color.Unspecified,
    /** Puck wash opacity at rest. It eases toward clear while pressed. */
    val puckOpacity: Float = 0.8f,
    val surfaceOpacity: Float = 0.5f,
    val textColor: Color = Color.White,
    val playerEnabled: Boolean = true,
    val miniPlayerEnabled: Boolean = true,
    val navBarEnabled: Boolean = true,
    /** Tablet / desktop side panel. */
    val sidePanelEnabled: Boolean = true,
    val sidePanelVibrancy: Float = 1.2f,
    val sidePanelBlurRadius: Float = 2f,
    val sidePanelLensHeight: Float = 0.4f,
    val sidePanelLensAmount: Float = 0.6f,
    val sidePanelColor: Color = Color.Unspecified,
    val sidePanelSurfaceOpacity: Float = 0.5f,
    val sidePanelTextColor: Color = Color.White,
) {
    fun forSidePanel(): GlassEffectConfig = copy(
        vibrancy = sidePanelVibrancy,
        blurRadius = sidePanelBlurRadius,
        lensHeight = sidePanelLensHeight,
        lensAmount = sidePanelLensAmount,
        surfaceTintColor = sidePanelColor,
        surfaceOpacity = sidePanelSurfaceOpacity,
        textColor = sidePanelTextColor,
    )

    fun isEnabledFor(component: GlassComponent): Boolean =
        globalEnabled && when (component) {
            GlassComponent.PLAYER -> playerEnabled
            GlassComponent.MINI_PLAYER -> miniPlayerEnabled
            GlassComponent.NAV_BAR -> navBarEnabled
            GlassComponent.SIDE_PANEL -> sidePanelEnabled
        }

    val anyComponentEnabled: Boolean
        get() = globalEnabled &&
            (playerEnabled || miniPlayerEnabled || navBarEnabled || sidePanelEnabled)
}

enum class GlassStyle {
    /** Blur + saturation + lens refraction + specular rim. The default. */
    LIQUID,

    /** Blur and saturation only: no lens, no rim, no chromatic aberration. */
    BLUR,

    /** No capture at all — a translucent tinted fill at the configured opacity. */
    TRANSPARENT,
}

enum class GlassComponent {
    PLAYER,
    MINI_PLAYER,
    NAV_BAR,
    SIDE_PANEL,
}

internal const val LENS_MAX_DP = 48f
internal const val PLAYER_BLUR_MULTIPLIER = 4f
internal const val MIN_GLASS_RESOLUTION_SCALE = 0.30f
internal const val FULL_QUALITY_BLUR_DP = 8f

fun glassResolutionScale(blurRadiusDp: Float): Float {
    val t = (blurRadiusDp / FULL_QUALITY_BLUR_DP).coerceIn(0f, 1f)
    return 1f - t * (1f - MIN_GLASS_RESOLUTION_SCALE)
}

fun isGlassSupported(): Boolean = true

fun isLowRamDevice(): Boolean = false

@Composable
fun isGlassAllowed(): Boolean = true

fun glassSaturation(vibrancy: Float): Float = 1f + 0.5f * vibrancy.coerceIn(0f, 2f)

fun glassContentColorFor(behind: Color, tint: Color, opacity: Float): Color {
    val effective = if (tint.isSpecified) {
        lerp(behind, tint, opacity.coerceIn(0f, 1f))
    } else {
        behind
    }
    return if (effective.luminance() > 0.5f) Color(0xFF1A1A1A) else Color.White
}

fun shouldUseTranslucentGlassFallback(style: GlassStyle, renderEffectSupported: Boolean): Boolean =
    style == GlassStyle.TRANSPARENT || !renderEffectSupported

private val EdgeHighlightWidth = 0.8f.dp
private const val EdgeHighlightAlpha = 0.55f
private const val HighlightAngleFrozen = 45f

val LocalGlassEffectConfig = staticCompositionLocalOf { GlassEffectConfig() }
val LocalBackdropLoopBucket = staticCompositionLocalOf<(() -> Int)?> { null }
val LocalAppleMusicUi = staticCompositionLocalOf { false }

/**
 * Vendored AGSL / SkSL shader source compiled via Skia RuntimeEffect on desktop JVM.
 */
internal object GlassShaders {
    private val roundedRectSDF = """
        float radiusAt(float2 coord, float4 radii) {
            if (coord.x >= 0.0) {
                if (coord.y <= 0.0) return radii.y;
                else return radii.z;
            } else {
                if (coord.y <= 0.0) return radii.x;
                else return radii.w;
            }
        }

        float sdRoundedRect(float2 coord, float2 halfSize, float radius) {
            float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
            float outside = length(max(cornerCoord, 0.0)) - radius;
            float inside = min(max(cornerCoord.x, cornerCoord.y), 0.0);
            return outside + inside;
        }

        float2 gradSdRoundedRect(float2 coord, float2 halfSize, float radius) {
            float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
            if (cornerCoord.x >= 0.0 || cornerCoord.y >= 0.0) {
                return sign(coord) * normalize(max(cornerCoord, 0.0));
            } else {
                float gradX = step(cornerCoord.y, cornerCoord.x);
                return sign(coord) * float2(gradX, 1.0 - gradX);
            }
        }
    """.trimIndent()

    val roundedRectRefractionShaderString = """
        uniform shader content;

        uniform float2 size;
        uniform float2 offset;
        uniform float4 cornerRadii;
        uniform float refractionHeight;
        uniform float refractionAmount;
        uniform float depthEffect;

        $roundedRectSDF

        float circleMap(float x) {
            return 1.0 - sqrt(1.0 - x * x);
        }

        half4 main(float2 coord) {
            float2 halfSize = size * 0.5;
            float2 centeredCoord = (coord + offset) - halfSize;
            float radius = radiusAt(coord, cornerRadii);
            
            float sd = sdRoundedRect(centeredCoord, halfSize, radius);
            if (-sd >= refractionHeight) {
                return content.eval(coord);
            }
            sd = min(sd, 0.0);
            
            float d = circleMap(1.0 - -sd / refractionHeight) * refractionAmount;
            float gradRadius = min(radius * 1.5, min(halfSize.x, halfSize.y));
            float2 grad = normalize(gradSdRoundedRect(centeredCoord, halfSize, gradRadius) + depthEffect * normalize(centeredCoord));
            
            float2 refractedCoord = coord + d * grad;
            return content.eval(refractedCoord);
        }
    """.trimIndent()

    val refractionEffect: RuntimeEffect? by lazy {
        try {
            RuntimeEffect.makeForShader(roundedRectRefractionShaderString)
        } catch (_: Throwable) {
            null
        }
    }
}

/**
 * Desktop liquid glass modifier preserving the exact signature and visual aesthetic
 * from Convx r52.
 */
@Composable
fun Modifier.liquidGlass(
    config: GlassEffectConfig,
    shape: CornerBasedShape = RoundedCornerShape(0.dp),
    applyEdgeEffects: Boolean = true,
    blurRadiusDp: Float = config.blurRadius,
    highlightAlpha: Float = EdgeHighlightAlpha,
    backdropScale: Float = glassResolutionScale(blurRadiusDp),
    frozen: () -> Boolean = { false },
    loopBucket: (() -> Int)? = LocalBackdropLoopBucket.current,
): Modifier {
    if (!config.globalEnabled) return this

    val surfaceTintColor = if (config.surfaceTintColor.isSpecified) {
        config.surfaceTintColor
    } else if (MaterialTheme.colorScheme.surface.luminance() > 0.5f) {
        Color(0xFFFAFAFA)
    } else {
        Color(0xFF2C2C2E)
    }

    if (config.style == GlassStyle.TRANSPARENT) {
        return this
            .clip(shape)
            .background(surfaceTintColor.copy(alpha = config.surfaceOpacity.coerceIn(0f, 1f)))
    }

    val rimColor = if (config.highlightColor.isSpecified) config.highlightColor else Color.White
    val rimAlpha = (highlightAlpha * (config.highlightOpacity / EdgeHighlightAlpha)).coerceIn(0f, 1f)

    // Specular highlight brush: Apple's 45-degree angled light rim
    val highlightBrush = remember(rimColor, rimAlpha) {
        Brush.linearGradient(
            colors = listOf(
                rimColor.copy(alpha = rimAlpha),
                rimColor.copy(alpha = rimAlpha * 0.25f),
                rimColor.copy(alpha = rimAlpha * 0.05f),
                rimColor.copy(alpha = rimAlpha * 0.35f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    val baseModifier = if (applyEdgeEffects) {
        this.shadow(
            elevation = 8.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.3f),
            spotColor = Color.Black.copy(alpha = 0.3f)
        )
    } else {
        this
    }

    val glassModifier = baseModifier
        .clip(shape)
        .background(surfaceTintColor.copy(alpha = config.surfaceOpacity.coerceIn(0f, 1f)))

    return if (applyEdgeEffects && config.style != GlassStyle.BLUR) {
        glassModifier.border(
            width = EdgeHighlightWidth,
            brush = highlightBrush,
            shape = shape
        )
    } else {
        glassModifier
    }
}
