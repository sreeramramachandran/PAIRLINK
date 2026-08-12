package com.pairlink.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.pairlink.app.core.designsystem.DesignTokens
import kotlin.math.cos
import kotlin.math.sin

/**
 * Reusable animated romantic mesh gradient background for PairLink.
 * Recreates the WebGL shader background natively in Jetpack Compose using
 * multi-layered animated radial gradients and floating ambient light blobs.
 */
@Composable
fun AnimatedMeshBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MeshBackgroundAnimation")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = DesignTokens.Animation.DurationMeshMovement, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MeshTime"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DesignTokens.Colors.BackgroundMidnight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Blob 1: Deep Romantic Purple (moving in upper-left quadrant)
            val blob1X = width * (0.35f + 0.20f * sin(time))
            val blob1Y = height * (0.30f + 0.15f * cos(time * 0.8f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DesignTokens.Colors.DeepPurple.copy(alpha = 0.85f),
                        DesignTokens.Colors.MutedPurple.copy(alpha = 0.40f),
                        Color.Transparent
                    ),
                    center = Offset(blob1X, blob1Y),
                    radius = width * 0.85f
                ),
                center = Offset(blob1X, blob1Y),
                radius = width * 0.85f
            )

            // Blob 2: Soft Radiant Pink (moving across center and right)
            val blob2X = width * (0.65f + 0.25f * cos(time * 0.6f))
            val blob2Y = height * (0.55f + 0.20f * sin(time * 0.9f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DesignTokens.Colors.SoftPink.copy(alpha = 0.35f),
                        DesignTokens.Colors.Lavender.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = Offset(blob2X, blob2Y),
                    radius = width * 0.70f
                ),
                center = Offset(blob2X, blob2Y),
                radius = width * 0.70f
            )

            // Blob 3: Lavender / Violet Aura (moving lower-left)
            val blob3X = width * (0.25f + 0.20f * cos(time * 0.5f + 1.5f))
            val blob3Y = height * (0.75f + 0.18f * sin(time * 0.7f + 1.0f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DesignTokens.Colors.Lavender.copy(alpha = 0.30f),
                        DesignTokens.Colors.DeepPurple.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(blob3X, blob3Y),
                    radius = width * 0.75f
                ),
                center = Offset(blob3X, blob3Y),
                radius = width * 0.75f
            )

            // Blob 4: Intense Pink Center Glow Pulse
            val glowRadius = width * (0.45f + 0.08f * sin(time * 1.5f))
            val glowCenterX = width * (0.50f + 0.10f * sin(time * 0.4f))
            val glowCenterY = height * (0.40f + 0.08f * cos(time * 0.5f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DesignTokens.Colors.PrimaryPink.copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(glowCenterX, glowCenterY),
                    radius = glowRadius
                ),
                center = Offset(glowCenterX, glowCenterY),
                radius = glowRadius
            )

            // Ambient Dark Overlay for contrast & depth
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DesignTokens.Colors.BackgroundMidnight.copy(alpha = 0.30f),
                        DesignTokens.Colors.BackgroundMidnight.copy(alpha = 0.55f),
                        DesignTokens.Colors.BackgroundMidnight.copy(alpha = 0.85f)
                    )
                )
            )
        }

        // Screen content layer
        content()
    }
}
