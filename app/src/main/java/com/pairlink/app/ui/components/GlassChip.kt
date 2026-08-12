package com.pairlink.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.MoodItem

/**
 * Reusable Glassmorphism Chip supporting optional leading icons and delete action.
 */
@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) DesignTokens.Colors.SoftPink.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.12f),
        label = "ChipBackground"
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected) DesignTokens.Colors.SoftPink.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.20f),
        label = "ChipBorder"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) DesignTokens.Colors.PrimaryPink else DesignTokens.Colors.TextSecondary,
        label = "ChipText"
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = if (selected) 10.dp else 2.dp,
                shape = RoundedCornerShape(DesignTokens.Radius.Full),
                spotColor = if (selected) DesignTokens.Colors.SoftPink.copy(alpha = 0.5f) else Color.Transparent
            )
            .clip(RoundedCornerShape(DesignTokens.Radius.Full))
            .background(backgroundColor)
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(DesignTokens.Radius.Full)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = DesignTokens.Colors.PrimaryPink),
                onClick = onClick
            )
            .padding(start = 16.dp, end = if (onDelete != null) 10.dp else 16.dp, top = 8.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            if (onDelete != null) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = DesignTokens.Colors.DangerRose,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * Reusable Floating Mood Chip with subtle organic floating animation and custom deletion support.
 */
@Composable
fun MoodChip(
    mood: MoodItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableFloatAnimation: Boolean = true,
    onDelete: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MoodFloat")
    val floatOffset by if (enableFloatAnimation) {
        infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3500 + mood.floatDelayMs,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "MoodChipFloatY"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    GlassChip(
        text = "${mood.emoji}  ${mood.label}",
        selected = selected,
        onClick = onClick,
        onDelete = if (mood.isCustom) onDelete else null,
        modifier = modifier.offset(y = floatOffset.dp)
    )
}
