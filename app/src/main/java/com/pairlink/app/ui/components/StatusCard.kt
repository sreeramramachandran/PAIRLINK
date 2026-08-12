package com.pairlink.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import kotlinx.coroutines.delay

/**
 * Bento Feature Card for displaying current Mood, Status, Together duration, or Birthday
 * with soft fade/slide animations and subtle glow when values update.
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
    var isRecentlyUpdated by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        isRecentlyUpdated = true
        delay(2000)
        isRecentlyUpdated = false
    }

    val glowBorderColor by animateColorAsState(
        targetValue = if (isRecentlyUpdated) iconColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.20f),
        animationSpec = tween(durationMillis = 500),
        label = "GlowBorder"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, glowBorderColor, RoundedCornerShape(DesignTokens.Radius.Large)),
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
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title.uppercase(),
                    color = DesignTokens.Colors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
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
                        color = DesignTokens.Colors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
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
        iconColor = DesignTokens.Colors.VibrantPink,
        iconBackground = DesignTokens.Colors.VibrantPink.copy(alpha = 0.20f),
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
        iconColor = DesignTokens.Colors.Lavender,
        iconBackground = DesignTokens.Colors.Lavender.copy(alpha = 0.20f),
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
        iconColor = DesignTokens.Colors.SkyBlue,
        iconBackground = DesignTokens.Colors.SkyBlue.copy(alpha = 0.20f),
        onClick = onClick,
        modifier = modifier
    )
}
