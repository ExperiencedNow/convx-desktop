package com.convx.desktop.ui.component.shapes

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import com.convx.desktop.ui.component.shapes.core.Point
import com.convx.desktop.ui.component.shapes.path.PathSegments
import com.convx.desktop.ui.component.shapes.path.buildCirclePathSegments
import com.convx.desktop.ui.component.shapes.path.toPath

@Immutable
abstract class AdvancedContinuity : Continuity {

    protected abstract fun createStandardRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments

    protected open fun createStandardRoundedRectangleOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline {
        val path =
            createStandardRoundedRectanglePathSegments(
                width = size.width.toDouble(),
                height = size.height.toDouble(),
                topLeft = topLeft.toDouble(),
                topRight = topRight.toDouble(),
                bottomRight = bottomRight.toDouble(),
                bottomLeft = bottomLeft.toDouble()
            ).toPath()
        return Outline.Generic(path)
    }

    protected open fun createHorizontalCapsulePathSegments(width: Double, height: Double): PathSegments {
        val cornerRadius = width * 0.5
        return createStandardRoundedRectanglePathSegments(
            width = width,
            height = height,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    protected open fun createHorizontalCapsuleOutline(size: Size): Outline {
        val path =
            createHorizontalCapsulePathSegments(
                width = size.width.toDouble(),
                height = size.height.toDouble()
            ).toPath()
        return Outline.Generic(path)
    }

    protected open fun createVerticalCapsulePathSegments(width: Double, height: Double): PathSegments {
        val cornerRadius = height * 0.5
        return createStandardRoundedRectanglePathSegments(
            width = width,
            height = height,
            topLeft = cornerRadius,
            topRight = cornerRadius,
            bottomRight = cornerRadius,
            bottomLeft = cornerRadius
        )
    }

    protected open fun createVerticalCapsuleOutline(size: Size): Outline {
        val path =
            createVerticalCapsulePathSegments(
                width = size.width.toDouble(),
                height = size.height.toDouble()
            ).toPath()
        return Outline.Generic(path)
    }

    protected open fun createCirclePathSegments(size: Double): PathSegments {
        val radius = size * 0.5
        return buildCirclePathSegments(
            center = Point(radius, radius),
            radius = radius
        )
    }

    protected open fun createCircleOutline(size: Size): Outline {
        val path = createCirclePathSegments(size.minDimension.toDouble()).toPath()
        return Outline.Generic(path)
    }

    override fun createRoundedRectanglePathSegments(
        width: Double,
        height: Double,
        topLeft: Double,
        topRight: Double,
        bottomRight: Double,
        bottomLeft: Double
    ): PathSegments {
        val maxRadius = width * 0.5
        if (width == height &&
            topLeft >= maxRadius &&
            topRight >= maxRadius &&
            bottomRight >= maxRadius &&
            bottomLeft >= maxRadius
        ) {
            return createCirclePathSegments(width)
        }

        if (width < height &&
            topLeft >= maxRadius &&
            topRight >= maxRadius &&
            bottomRight >= maxRadius &&
            bottomLeft >= maxRadius
        ) {
            return createHorizontalCapsulePathSegments(width, height)
        }

        val maxRadiusY = height * 0.5
        if (width > height &&
            topLeft >= maxRadiusY &&
            topRight >= maxRadiusY &&
            bottomRight >= maxRadiusY &&
            bottomLeft >= maxRadiusY
        ) {
            return createVerticalCapsulePathSegments(width, height)
        }

        return createStandardRoundedRectanglePathSegments(
            width = width,
            height = height,
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft
        )
    }

    override fun createRoundedRectangleOutline(
        size: Size,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float
    ): Outline {
        val width = size.width
        val height = size.height
        val maxRadius = width * 0.5f

        if (width == height &&
            topLeft >= maxRadius &&
            topRight >= maxRadius &&
            bottomRight >= maxRadius &&
            bottomLeft >= maxRadius
        ) {
            return createCircleOutline(size)
        }

        if (width < height &&
            topLeft >= maxRadius &&
            topRight >= maxRadius &&
            bottomRight >= maxRadius &&
            bottomLeft >= maxRadius
        ) {
            return createHorizontalCapsuleOutline(size)
        }

        val maxRadiusY = height * 0.5f
        if (width > height &&
            topLeft >= maxRadiusY &&
            topRight >= maxRadiusY &&
            bottomRight >= maxRadiusY &&
            bottomLeft >= maxRadiusY
        ) {
            return createVerticalCapsuleOutline(size)
        }

        return createStandardRoundedRectangleOutline(
            size = size,
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft
        )
    }
}
