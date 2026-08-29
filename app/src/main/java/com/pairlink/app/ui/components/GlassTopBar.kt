package com.pairlink.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens

/**
 * Reusable Glassmorphism Top App Bar displaying greeting, couple names, and action buttons.
 * Supports statusBarsPadding for full compatibility across device notch & status bar heights.
 */
@Composable
fun GlassTopBar(
    title: String = "Sreeram & Chakkara ❤️",
    userAvatarUrl: String? = null,
    partnerAvatarUrl: String? = null,
    showUserAvatar: Boolean = true,
    isPartnerOnline: Boolean = true,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onSendHeartClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    actions: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeAvatarUrl = partnerAvatarUrl ?: userAvatarUrl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.95f))
            .border(width = 1.dp, color = Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Section: Back Button or Greeting & Couple Names
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (showBack) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = DesignTokens.Colors.PrimaryCrimson),
                                onClick = onBackClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1D1418),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = title,
                        color = Color(0xFF1D1418),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    )
                } else {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Good Evening,",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DesignTokens.Colors.PrimaryCrimson
                            )
                            Text(text = "👋", fontSize = 12.sp)
                        }
                        Text(
                            text = title,
                            color = Color(0xFF1D1418),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.3).sp
                        )
                    }
                }
            }

            // Right Section: Action Buttons (Notifications, Calendar, Avatar)
            if (actions != null) {
                actions()
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = DesignTokens.Colors.PrimaryCrimson),
                                onClick = onNotificationClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFF5A4E53),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Calendar Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = DesignTokens.Colors.PrimaryCrimson),
                                onClick = onCalendarClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendar",
                            tint = Color(0xFF5A4E53),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Avatar Circle Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onAvatarClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!activeAvatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(activeAvatarUrl),
                                contentDescription = "User Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(2.dp, DesignTokens.Colors.PrimaryCrimson, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = DesignTokens.Colors.PrimaryCrimson,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
