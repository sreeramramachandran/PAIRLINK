package com.pairlink.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

/**
 * Reusable Glassmorphism Top App Bar displaying title, user avatar circle, or back button.
 */
@Composable
fun GlassTopBar(
    title: String = "PairLink",
    userAvatarUrl: String? = null,
    partnerAvatarUrl: String? = null,
    showUserAvatar: Boolean = false,
    isPartnerOnline: Boolean = true,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onSendHeartClick: () -> Unit = {},
    actions: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeAvatarUrl = userAvatarUrl ?: partnerAvatarUrl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f)
            )
            .padding(horizontal = 20.dp)
            .padding(top = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Section: Back Button or User Avatar Circle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showBack) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.10f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = DesignTokens.Colors.PrimaryPink),
                                onClick = onBackClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DesignTokens.Colors.TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else if (showUserAvatar || activeAvatarUrl != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
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
                                    .border(1.5.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.6f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.20f))
                                    .border(1.5.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = title,
                    color = DesignTokens.Colors.PrimaryPink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
            }

            // Right Section: Custom Actions or Send Heart Button
            if (actions != null) {
                actions()
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = DesignTokens.Colors.PrimaryPink),
                            onClick = onSendHeartClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Send Love Burst",
                        tint = DesignTokens.Colors.PrimaryPink,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
