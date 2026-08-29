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
 * Light neumorphic soft warm blush palette matching reference screen.
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
            .background(Color(0xFFFAF5F5))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Blob 1: Soft Rose Ambient Glow
            val blob1X = width * (0.35f + 0.15f * sin(time))
            val blob1Y = height * (0.25f + 0.10f * cos(time * 0.8f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFEBF0).copy(alpha = 0.80f),
                        Color(0xFFFFF0F3).copy(alpha = 0.40f),
                        Color.Transparent
                    ),
                    center = Offset(blob1X, blob1Y),
                    radius = width * 0.85f
                ),
                center = Offset(blob1X, blob1Y),
                radius = width * 0.85f
            )

            // Blob 2: Crimson Subtle Ambient Glow
            val blob2X = width * (0.65f + 0.20f * cos(time * 0.6f))
            val blob2Y = height * (0.45f + 0.15f * sin(time * 0.9f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE60039).copy(alpha = 0.08f),
                        Color(0xFFFF809B).copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(blob2X, blob2Y),
                    radius = width * 0.70f
                ),
                center = Offset(blob2X, blob2Y),
                radius = width * 0.70f
            )

            // Soft White Overlay
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFAF5F5).copy(alpha = 0.30f),
                        Color(0xFFFAF5F5).copy(alpha = 0.60f),
                        Color(0xFFFAF5F5).copy(alpha = 0.90f)
                    )
                )
            )
        }

        // Screen content layer
        content()
    }
}
