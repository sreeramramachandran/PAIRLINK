package com.pairlink.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens

/**
 * Bento Feature Card displaying current Mood, Status, Together duration, or Birthday
 * with a continuous travelling red line moving along the box border perimeter.
 */
@Composable
fun BentoFeatureCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    iconBackground: Color,
    onClick: (() -> Unit)? = null,
    isSlideAnimation: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TravelingRedBorder")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BorderProgress"
    )

    val cardRadiusPx = DesignTokens.Radius.Large

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                val totalPerimeter = 2f * (size.width + size.height)
                val dashLength = totalPerimeter * 0.35f
                val gapLength = totalPerimeter * 0.65f
                val phase = -progress * (dashLength + gapLength)

                // 1. Base subtle border outline
                drawRoundRect(
                    color = Color(0xFFFFB3C1).copy(alpha = 0.40f),
                    style = Stroke(width = 1.5.dp.toPx()),
                    cornerRadius = CornerRadius(cardRadiusPx.toPx(), cardRadiusPx.toPx())
                )

                // 2. Traveling vibrant crimson red line moving along the box perimeter
                drawRoundRect(
                    color = Color(0xFFE60039),
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(dashLength, gapLength),
                            phase = phase
                        )
                    ),
                    cornerRadius = CornerRadius(cardRadiusPx.toPx(), cardRadiusPx.toPx())
                )
            },
        shape = RoundedCornerShape(DesignTokens.Radius.Large),
        contentPadding = 16.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBackground)
                    .border(1.dp, Color(0xFFFFB3C1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title.uppercase(),
                    color = DesignTokens.Colors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                AnimatedContent(
                    targetState = value,
                    transitionSpec = {
                        if (isSlideAnimation) {
                            (slideInVertically { height -> height } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> -height } + fadeOut())
                        } else {
                            fadeIn(animationSpec = tween(400)) togetherWith
                                    fadeOut(animationSpec = tween(400))
                        }
                    },
                    label = "BentoCardValueTransition"
                ) { targetVal ->
                    Text(
                        text = targetVal,
                        color = Color(0xFF1D1418),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    currentStatus: String,
    title: String = "Current Status",
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val statusIcon = when {
        currentStatus.contains("Home", ignoreCase = true) -> Icons.Default.Home
        currentStatus.contains("Driv", ignoreCase = true) -> Icons.Default.DirectionsCar
        else -> Icons.Default.Work
    }

    BentoFeatureCard(
        title = title,
        value = currentStatus,
        icon = statusIcon,
        iconColor = DesignTokens.Colors.PrimaryCrimson,
        iconBackground = Color(0xFFFFF0F3),
        onClick = onClick,
        isSlideAnimation = true,
        modifier = modifier
    )
}

@Composable
fun RelationshipCard(
    togetherTime: String,
    title: String = "Together For",
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BentoFeatureCard(
        title = title,
        value = togetherTime,
        icon = Icons.Default.Favorite,
        iconColor = DesignTokens.Colors.PrimaryCrimson,
        iconBackground = Color(0xFFFFF0F3),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun BirthdayCard(
    daysLeft: String,
    title: String = "Birthday In",
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BentoFeatureCard(
        title = title,
        value = daysLeft,
        icon = Icons.Default.Cake,
        iconColor = DesignTokens.Colors.PrimaryCrimson,
        iconBackground = Color(0xFFFFF0F3),
        onClick = onClick,
        modifier = modifier
    )
}
