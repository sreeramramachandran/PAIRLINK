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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
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
    MEMORIES,
    SETTINGS
}

/**
 * Reusable Floating Capsule Bottom Navigation Bar for PairLink.
 * Matches reference UI with white capsule and center raised crimson heart button.
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(DesignTokens.Radius.Full),
                    ambientColor = Color(0xFFE60039).copy(alpha = 0.15f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(DesignTokens.Radius.Full))
                .background(Color.White.copy(alpha = 0.95f))
                .border(
                    border = BorderStroke(width = 1.dp, color = Color.White),
                    shape = RoundedCornerShape(DesignTokens.Radius.Full)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 1. Home
            NavTabItem(
                label = "Home",
                selected = currentTab == BottomNavTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { onTabSelected(BottomNavTab.HOME) }
            )

            // 2. Chat
            NavTabItem(
                label = "Chat",
                selected = currentTab == BottomNavTab.UPDATES,
                selectedIcon = Icons.Filled.ChatBubble,
                unselectedIcon = Icons.Outlined.ChatBubble,
                badgeCount = unreadNotificationsCount,
                onClick = { onTabSelected(BottomNavTab.UPDATES) }
            )

            // 3. Center Raised Floating Heart Action Button
            Box(
                modifier = Modifier
                    .offset(y = (-14).dp)
                    .size(52.dp)
                    .shadow(12.dp, CircleShape, spotColor = Color(0xFFE60039).copy(alpha = 0.4f))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFF3366), Color(0xFFE60039))
                        )
                    )
                    .border(3.5.dp, Color(0xFFFAF5F5), CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White),
                        onClick = { onTabSelected(BottomNavTab.MOOD) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Express Love",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // 4. Memories
            NavTabItem(
                label = "Memories",
                selected = currentTab == BottomNavTab.MEMORIES,
                selectedIcon = Icons.Filled.PhotoLibrary,
                unselectedIcon = Icons.Outlined.PhotoLibrary,
                onClick = { onTabSelected(BottomNavTab.MEMORIES) }
            )

            // 5. Profile
            NavTabItem(
                label = "Profile",
                selected = currentTab == BottomNavTab.SETTINGS,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                onClick = { onTabSelected(BottomNavTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val tint by animateColorAsState(
        targetValue = if (selected) DesignTokens.Colors.PrimaryCrimson else DesignTokens.Colors.TextSecondary,
        label = "NavTabTint"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(DesignTokens.Colors.PrimaryCrimson),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = tint,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
