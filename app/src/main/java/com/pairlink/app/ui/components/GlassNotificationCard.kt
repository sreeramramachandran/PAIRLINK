package com.pairlink.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.NotificationColorType
import com.pairlink.app.domain.model.NotificationItem
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.PresenceRingState
import com.pairlink.app.domain.model.UpdateNotification

/**
 * Reusable Glass Notification Card with colored glowing indicators for NotificationItem.
 */
@Composable
fun NotificationItemCard(
    item: NotificationItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (iconBg, iconTint, icon) = when (item.type) {
        NotificationType.PRESENCE -> Triple(
            DesignTokens.Colors.DangerRose.copy(alpha = 0.20f),
            DesignTokens.Colors.DangerRose,
            Icons.Default.Favorite
        )
        NotificationType.MOOD_UPDATED -> Triple(
            DesignTokens.Colors.SoftPink.copy(alpha = 0.20f),
            DesignTokens.Colors.PrimaryPink,
            Icons.Default.Mood
        )
        NotificationType.REACHED_HOME -> Triple(
            DesignTokens.Colors.OnlineGreen.copy(alpha = 0.20f),
            DesignTokens.Colors.OnlineGreen,
            Icons.Default.Home
        )
        NotificationType.STATUS_UPDATED -> Triple(
            DesignTokens.Colors.MutedPurple.copy(alpha = 0.25f),
            DesignTokens.Colors.Lavender,
            Icons.Default.Work
        )
        NotificationType.BIRTHDAY_REMINDER, NotificationType.BIRTHDAY_TODAY -> Triple(
            DesignTokens.Colors.SkyBlue.copy(alpha = 0.20f),
            DesignTokens.Colors.SkyBlue,
            Icons.Default.Cake
        )
        NotificationType.ANNIVERSARY_REMINDER, NotificationType.ANNIVERSARY_TODAY -> Triple(
            DesignTokens.Colors.VibrantPink.copy(alpha = 0.20f),
            DesignTokens.Colors.PrimaryPink,
            Icons.Default.Celebration
        )
        else -> Triple(
            Color.White.copy(alpha = 0.15f),
            DesignTokens.Colors.TextPrimary,
            Icons.Default.Notifications
        )
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.Radius.Medium),
        contentPadding = 16.dp,
        backgroundColor = if (!item.isRead) DesignTokens.Colors.PrimaryPink.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.08f),
        borderColor = if (!item.isRead) DesignTokens.Colors.PrimaryPink.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.15f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg)
                    .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = item.title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Notification Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        if (!item.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(DesignTokens.Colors.PrimaryPink, CircleShape)
                            )
                        }
                        Text(
                            text = item.title,
                            color = DesignTokens.Colors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = item.formattedTime,
                        color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = item.message,
                    color = DesignTokens.Colors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Notification",
                    tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Reusable Glass Notification Card with colored glowing indicators.
 */
@Composable
fun GlassNotificationCard(
    notification: UpdateNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (dotColor, iconBg, iconTint) = when (notification.colorType) {
        NotificationColorType.PRIMARY -> Triple(
            DesignTokens.Colors.SoftPink,
            DesignTokens.Colors.SoftPink.copy(alpha = 0.20f),
            DesignTokens.Colors.PrimaryPink
        )
        NotificationColorType.SECONDARY -> Triple(
            DesignTokens.Colors.Lavender,
            DesignTokens.Colors.MutedPurple.copy(alpha = 0.35f),
            DesignTokens.Colors.Lavender
        )
        NotificationColorType.TERTIARY -> Triple(
            DesignTokens.Colors.VibrantPink,
            DesignTokens.Colors.VibrantPink.copy(alpha = 0.20f),
            DesignTokens.Colors.PrimaryPink
        )
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.Radius.Medium),
        contentPadding = 16.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = notification.title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = DesignTokens.Colors.TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = notification.time,
                        color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = notification.description,
                    color = DesignTokens.Colors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Reusable Large Partner Profile Hero Card on the Home Screen with dynamic PresenceRingState.
 */
@Composable
fun PartnerProfileCard(
    partner: PartnerProfile,
    partnerDisplayName: String = "",
    ringState: PresenceRingState = PresenceRingState.ONLINE,
    onMoodClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val nameToShow = partnerDisplayName.ifBlank { partner.name.ifBlank { "Partner" } }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Partner Avatar + Mood Badge with Dynamic Presence Ring
        Box(
            modifier = Modifier.size(136.dp),
            contentAlignment = Alignment.Center
        ) {
            PresenceRing(size = 136.dp, ringState = ringState) {
                AsyncImage(
                    model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(partner.avatarUrl),
                    contentDescription = partner.name,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // Mood Badge Button (optional)
            if (onMoodClick != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                        .clickable(onClick = onMoodClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = "Update Mood",
                        tint = DesignTokens.Colors.PrimaryPink,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Greeting and Online Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val titleText = when {
                nameToShow.isNotBlank() && nameToShow != "Partner" -> "Connected with $nameToShow"
                partner.id.isNotBlank() -> "Connected with Partner"
                else -> "Your Private Sanctuary"
            }

            Text(
                text = titleText,
                color = DesignTokens.Colors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val indicatorColor = when (ringState) {
                    PresenceRingState.OFFLINE -> Color(0xFF6B7280)
                    PresenceRingState.ONLINE -> DesignTokens.Colors.OnlineGreen
                    PresenceRingState.SLEEPING -> Color(0xFF60A5FA)
                    PresenceRingState.BUSY -> Color(0xFFFBBF24)
                    PresenceRingState.DRIVING -> Color(0xFFFB923C)
                    PresenceRingState.HOLDING -> DesignTokens.Colors.DangerRose
                }

                val statusText = when (ringState) {
                    PresenceRingState.HOLDING -> "Holding with you ❤️"
                    PresenceRingState.SLEEPING -> "Sleeping"
                    PresenceRingState.BUSY -> "Busy"
                    PresenceRingState.DRIVING -> "Driving"
                    PresenceRingState.ONLINE -> "Online"
                    PresenceRingState.OFFLINE -> "Away"
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(indicatorColor, CircleShape)
                )
                Text(
                    text = statusText,
                    color = DesignTokens.Colors.TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
