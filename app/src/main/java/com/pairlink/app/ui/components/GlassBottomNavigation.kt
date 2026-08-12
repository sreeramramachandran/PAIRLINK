package com.pairlink.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens

enum class BottomNavTab {
    HOME,
    MOOD,
    UPDATES,
    SETTINGS
}

/**
 * Reusable Floating Capsule Bottom Navigation Bar for PairLink with dynamic unread badge count.
 */
@Composable
fun GlassBottomNavigation(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    unreadNotificationsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(64.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(DesignTokens.Radius.Full),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = DesignTokens.Colors.SoftPink.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(DesignTokens.Radius.Full))
                .background(Color.White.copy(alpha = 0.12f))
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(DesignTokens.Radius.Full)
                )
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavTabItem(
                selected = currentTab == BottomNavTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                contentDescription = "Home Sanctuary",
                onClick = { onTabSelected(BottomNavTab.HOME) }
            )

            NavTabItem(
                selected = currentTab == BottomNavTab.MOOD,
                selectedIcon = Icons.Filled.Mood,
                unselectedIcon = Icons.Outlined.Mood,
                contentDescription = "Mood & Status",
                onClick = { onTabSelected(BottomNavTab.MOOD) }
            )

            NavTabItem(
                selected = currentTab == BottomNavTab.UPDATES,
                selectedIcon = Icons.Filled.Notifications,
                unselectedIcon = Icons.Outlined.Notifications,
                contentDescription = "Updates & Notification Center",
                badgeCount = unreadNotificationsCount,
                onClick = { onTabSelected(BottomNavTab.UPDATES) }
            )

            NavTabItem(
                selected = currentTab == BottomNavTab.SETTINGS,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                contentDescription = "Settings & Profile",
                onClick = { onTabSelected(BottomNavTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    contentDescription: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "NavTabScale"
    )

    val tint by animateColorAsState(
        targetValue = if (selected) DesignTokens.Colors.PrimaryPink else DesignTokens.Colors.TextSecondary,
        label = "NavTabTint"
    )

    val backgroundBrush = if (selected) {
        Brush.radialGradient(
            colors = listOf(
                DesignTokens.Colors.PrimaryPink.copy(alpha = 0.30f),
                Color.Transparent
            )
        )
    } else {
        SolidColorBrush(Color.Transparent)
    }

    // Bouncing badge animation
    val infiniteTransition = rememberInfiniteTransition(label = "BadgeBounce")
    val badgeBounceScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BadgeScale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = DesignTokens.Colors.PrimaryPink),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        // Unread Badge
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .scale(badgeBounceScale)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(DesignTokens.Colors.DangerRose),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun SolidColorBrush(color: Color): Brush = Brush.linearGradient(listOf(color, color))
