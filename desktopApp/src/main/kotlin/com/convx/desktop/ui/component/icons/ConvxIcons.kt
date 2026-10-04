package com.convx.desktop.ui.component.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * High-fidelity vector icons for Convx Desktop, matching the iOS / Lucide aesthetic
 * from Convx r52.
 */
object ConvxIcons {

    val Home: ImageVector by lazy {
        ImageVector.Builder(
            name = "Home",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10f, 20f)
                verticalLineToRelative(-6f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(-8f)
                horizontalLineToRelative(3f)
                lineTo(12f, 3f)
                lineTo(2f, 12f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(8f)
                close()
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1f,
                stroke = SolidColor(Color.White),
                strokeAlpha = 1f,
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11f, 19f)
                curveTo(15.418f, 19f, 19f, 15.418f, 19f, 11f)
                curveTo(19f, 6.582f, 15.418f, 3f, 11f, 3f)
                curveTo(6.582f, 3f, 3f, 6.582f, 3f, 11f)
                curveTo(3f, 15.418f, 6.582f, 19f, 11f, 19f)
                close()
                moveTo(21f, 21f)
                lineTo(16.65f, 16.65f)
            }
        }.build()
    }

    val Library: ImageVector by lazy {
        ImageVector.Builder(
            name = "Library",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1f,
                stroke = SolidColor(Color.White),
                strokeAlpha = 1f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16f, 6f)
                lineTo(8f, 6f)
                moveTo(18f, 10f)
                lineTo(6f, 10f)
                moveTo(4f, 14f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
                verticalLineToRelative(4f)
                curveToRelative(0f, 1.1f, -0.9f, 2f, -2f, 2f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                verticalLineToRelative(-4f)
                curveToRelative(0f, -1.1f, 0.9f, -2f, 2f, -2f)
                close()
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1f,
                stroke = SolidColor(Color.White),
                strokeAlpha = 1f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.22f, 2f)
                horizontalLineToRelative(-0.44f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, 1.61f)
                lineToRelative(-0.16f, 0.93f)
                arcToRelative(2f, 2f, 0f, false, true, -1.18f, 1.48f)
                lineToRelative(-0.84f, 0.35f)
                arcToRelative(2f, 2f, 0f, false, true, -1.9f, -0.3f)
                lineToRelative(-0.73f, -0.58f)
                arcToRelative(2f, 2f, 0f, false, false, -2.52f, 0.22f)
                lineToRelative(-0.31f, 0.31f)
                arcToRelative(2f, 2f, 0f, false, false, -0.22f, 2.52f)
                lineToRelative(0.58f, 0.73f)
                arcToRelative(2f, 2f, 0f, false, true, 0.3f, 1.9f)
                lineToRelative(-0.35f, 0.84f)
                arcToRelative(2f, 2f, 0f, false, true, -1.48f, 1.18f)
                lineToRelative(-0.93f, 0.16f)
                arcToRelative(2f, 2f, 0f, false, false, -1.61f, 2f)
                verticalLineToRelative(0.44f)
                arcToRelative(2f, 2f, 0f, false, false, 1.61f, 2f)
                lineToRelative(0.93f, 0.16f)
                arcToRelative(2f, 2f, 0f, false, true, 1.48f, 1.18f)
                lineToRelative(0.35f, 0.84f)
                arcToRelative(2f, 2f, 0f, false, true, -0.3f, 1.9f)
                lineToRelative(-0.58f, 0.73f)
                arcToRelative(2f, 2f, 0f, false, false, 0.22f, 2.52f)
                lineToRelative(0.31f, 0.31f)
                arcToRelative(2f, 2f, 0f, false, false, 2.52f, -0.22f)
                lineToRelative(0.73f, -0.58f)
                arcToRelative(2f, 2f, 0f, false, true, 1.9f, -0.3f)
                lineToRelative(0.84f, 0.35f)
                arcToRelative(2f, 2f, 0f, false, true, 1.18f, 1.48f)
                lineToRelative(0.16f, 0.93f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, 1.61f)
                horizontalLineToRelative(0.44f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -1.61f)
                lineToRelative(0.16f, -0.93f)
                arcToRelative(2f, 2f, 0f, false, true, 1.18f, -1.48f)
                lineToRelative(0.84f, -0.35f)
                arcToRelative(2f, 2f, 0f, false, true, 1.9f, 0.3f)
                lineToRelative(0.73f, 0.58f)
                arcToRelative(2f, 2f, 0f, false, false, 2.52f, -0.22f)
                lineToRelative(0.31f, -0.31f)
                arcToRelative(2f, 2f, 0f, false, false, 0.22f, -2.52f)
                lineToRelative(-0.58f, -0.73f)
                arcToRelative(2f, 2f, 0f, false, true, -0.3f, -1.9f)
                lineToRelative(0.35f, -0.84f)
                arcToRelative(2f, 2f, 0f, false, true, 1.48f, -1.18f)
                lineToRelative(0.93f, -0.16f)
                arcToRelative(2f, 2f, 0f, false, false, 1.61f, -2f)
                verticalLineToRelative(-0.44f)
                arcToRelative(2f, 2f, 0f, false, false, -1.61f, -2f)
                lineToRelative(-0.93f, -0.16f)
                arcToRelative(2f, 2f, 0f, false, true, -1.48f, -1.18f)
                lineToRelative(-0.35f, -0.84f)
                arcToRelative(2f, 2f, 0f, false, true, 0.3f, -1.9f)
                lineToRelative(0.58f, -0.73f)
                arcToRelative(2f, 2f, 0f, false, false, -0.22f, -2.52f)
                lineToRelative(-0.31f, -0.31f)
                arcToRelative(2f, 2f, 0f, false, false, -2.52f, 0.22f)
                lineToRelative(-0.73f, 0.58f)
                arcToRelative(2f, 2f, 0f, false, true, -1.9f, 0.3f)
                lineToRelative(-0.84f, -0.35f)
                arcToRelative(2f, 2f, 0f, false, true, -1.18f, -1.48f)
                lineToRelative(-0.16f, -0.93f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, -1.61f)
                close()
                moveTo(12f, 15f)
                arcToRelative(3f, 3f, 0f, true, false, 0f, -6f)
                arcToRelative(3f, 3f, 0f, false, false, 0f, 6f)
                close()
            }
        }.build()
    }

    val Play: ImageVector by lazy {
        ImageVector.Builder(
            name = "Play",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(5f, 3f)
                lineTo(19f, 12f)
                lineTo(5f, 21f)
                close()
            }
        }.build()
    }

    val Pause: ImageVector by lazy {
        ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(6f, 4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(16f)
                horizontalLineTo(6f)
                close()
                moveTo(14f, 4f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(16f)
                horizontalLineToRelative(-4f)
                close()
            }
        }.build()
    }

    val SkipForward: ImageVector by lazy {
        ImageVector.Builder(
            name = "SkipForward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(5f, 4f)
                lineTo(14.5f, 12f)
                lineTo(5f, 20f)
                verticalLineTo(4f)
                close()
                moveTo(16.5f, 4f)
                horizontalLineToRelative(2.5f)
                verticalLineToRelative(16f)
                horizontalLineToRelative(-2.5f)
                close()
            }
        }.build()
    }

    val SkipBack: ImageVector by lazy {
        ImageVector.Builder(
            name = "SkipBack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 1f,
                stroke = null,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19f, 20f)
                lineTo(9.5f, 12f)
                lineTo(19f, 4f)
                verticalLineToRelative(16f)
                close()
                moveTo(5f, 4f)
                horizontalLineToRelative(2.5f)
                verticalLineToRelative(16f)
                horizontalLineTo(5f)
                close()
            }
        }.build()
    }

    val VolumeUp: ImageVector by lazy {
        ImageVector.Builder(
            name = "VolumeUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1f,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11f, 5f)
                lineTo(6f, 9f)
                horizontalLineTo(2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(4f)
                lineToRelative(5f, 4f)
                verticalLineTo(5f)
                close()
                moveTo(15.54f, 8.46f)
                arcToRelative(5f, 5f, 0f, false, true, 0f, 7.07f)
                moveTo(19.07f, 4.93f)
                arcToRelative(10f, 10f, 0f, false, true, 0f, 14.14f)
            }
        }.build()
    }

    val VolumeMute: ImageVector by lazy {
        ImageVector.Builder(
            name = "VolumeMute",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1f,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11f, 5f)
                lineTo(6f, 9f)
                horizontalLineTo(2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(4f)
                lineToRelative(5f, 4f)
                verticalLineTo(5f)
                close()
                moveTo(23f, 9f)
                lineToRelative(-6f, 6f)
                moveTo(17f, 9f)
                lineToRelative(6f, 6f)
            }
        }.build()
    }

    val Heart: ImageVector by lazy {
        ImageVector.Builder(
            name = "Heart",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20.84f, 4.61f)
                arcToRelative(5.5f, 5.5f, 0f, false, false, -7.78f, 0f)
                lineTo(12f, 5.67f)
                lineToRelative(-1.06f, -1.06f)
                arcToRelative(5.5f, 5.5f, 0f, false, false, -7.78f, 7.78f)
                lineToRelative(1.06f, 1.06f)
                lineTo(12f, 21.23f)
                lineToRelative(7.78f, -7.78f)
                lineToRelative(1.06f, -1.06f)
                arcToRelative(5.5f, 5.5f, 0f, false, false, 0f, -7.78f)
                close()
            }
        }.build()
    }

    val HeartFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "HeartFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFA2D48)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20.84f, 4.61f)
                arcToRelative(5.5f, 5.5f, 0f, false, false, -7.78f, 0f)
                lineTo(12f, 5.67f)
                lineToRelative(-1.06f, -1.06f)
                arcToRelative(5.5f, 5.5f, 0f, false, false, -7.78f, 7.78f)
                lineToRelative(1.06f, 1.06f)
                lineTo(12f, 21.23f)
                lineToRelative(7.78f, -7.78f)
                lineToRelative(1.06f, -1.06f)
                arcToRelative(5.5f, 5.5f, 0f, false, false, 0f, -7.78f)
                close()
            }
        }.build()
    }

    val Shuffle: ImageVector by lazy {
        ImageVector.Builder(
            name = "Shuffle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16f, 3f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(5f)
                moveTo(4f, 20f)
                lineTo(21f, 3f)
                moveTo(21f, 16f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(-5f)
                moveTo(15f, 15f)
                lineToRelative(6f, 6f)
                moveTo(4f, 4f)
                lineToRelative(5f, 5f)
            }
        }.build()
    }

    val Repeat: ImageVector by lazy {
        ImageVector.Builder(
            name = "Repeat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(17f, 1f)
                lineToRelative(4f, 4f)
                lineToRelative(-4f, 4f)
                moveTo(3f, 11f)
                verticalLineTo(9f)
                arcToRelative(4f, 4f, 0f, false, true, 4f, -4f)
                horizontalLineToRelative(14f)
                moveTo(7f, 23f)
                lineToRelative(-4f, -4f)
                lineToRelative(4f, -4f)
                moveTo(21f, 13f)
                verticalLineToRelative(2f)
                arcToRelative(4f, 4f, 0f, false, true, -4f, 4f)
                horizontalLineTo(3f)
            }
        }.build()
    }

    // Windows custom title bar controls
    val WindowMinimize: ImageVector by lazy {
        ImageVector.Builder(
            name = "WindowMinimize",
            defaultWidth = 12.dp,
            defaultHeight = 12.dp,
            viewportWidth = 12f,
            viewportHeight = 12f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Square
            ) {
                moveTo(1f, 6f)
                lineTo(11f, 6f)
            }
        }.build()
    }

    val WindowMaximize: ImageVector by lazy {
        ImageVector.Builder(
            name = "WindowMaximize",
            defaultWidth = 12.dp,
            defaultHeight = 12.dp,
            viewportWidth = 12f,
            viewportHeight = 12f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Square
            ) {
                moveTo(1.5f, 1.5f)
                horizontalLineToRelative(9f)
                verticalLineToRelative(9f)
                horizontalLineToRelative(-9f)
                close()
            }
        }.build()
    }

    val WindowRestore: ImageVector by lazy {
        ImageVector.Builder(
            name = "WindowRestore",
            defaultWidth = 12.dp,
            defaultHeight = 12.dp,
            viewportWidth = 12f,
            viewportHeight = 12f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Square
            ) {
                moveTo(3.5f, 3.5f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(7f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(-2f)
                moveTo(1.5f, 3.5f)
                horizontalLineToRelative(7f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(-7f)
                close()
            }
        }.build()
    }

    val WindowClose: ImageVector by lazy {
        ImageVector.Builder(
            name = "WindowClose",
            defaultWidth = 12.dp,
            defaultHeight = 12.dp,
            viewportWidth = 12f,
            viewportHeight = 12f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Square
            ) {
                moveTo(1.5f, 1.5f)
                lineTo(10.5f, 10.5f)
                moveTo(10.5f, 1.5f)
                lineTo(1.5f, 10.5f)
            }
        }.build()
    }

    val Lyrics: ImageVector by lazy {
        ImageVector.Builder(
            name = "Lyrics",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Speech bubble outline
                moveTo(21f, 15f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                horizontalLineTo(7f)
                lineToRelative(-4f, 4f)
                verticalLineTo(5f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
                horizontalLineToRelative(14f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
                close()
                // Horizontal quote/lyrics lines inside
                moveTo(8f, 9f)
                horizontalLineToRelative(8f)
                moveTo(8f, 13f)
                horizontalLineToRelative(5f)
            }
        }.build()
    }
}
