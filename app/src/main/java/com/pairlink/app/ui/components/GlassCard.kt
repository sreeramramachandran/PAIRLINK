package com.pairlink.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pairlink.app.core.designsystem.DesignTokens

/**
 * Reusable Light Neumorphic & Glassmorphism Card for PairLink.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
    backgroundColor: Color = Color.White.copy(alpha = 0.90f),
    borderColor: Color = Color.White,
    borderWidth: Dp = DesignTokens.Glass.BorderWidthThin,
    elevation: Dp = DesignTokens.Elevation.Card,
    contentPadding: Dp = DesignTokens.Spacing.Large,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val baseModifier = modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color(0xFFE60039).copy(alpha = 0.08f),
            spotColor = Color.Black.copy(alpha = 0.04f)
        )
        .clip(shape)
        .background(backgroundColor)
        .border(
            border = BorderStroke(
                width = borderWidth,
                color = borderColor
            ),
            shape = shape
        )

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = ripple(color = DesignTokens.Colors.PrimaryCrimson.copy(alpha = 0.15f)),
            onClick = onClick
        )
    } else {
        baseModifier
    }

    Box(
        modifier = finalModifier.padding(contentPadding),
        content = content
    )
}
