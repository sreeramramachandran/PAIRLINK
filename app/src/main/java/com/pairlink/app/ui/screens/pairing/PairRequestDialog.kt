package com.pairlink.app.ui.screens.pairing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.core.util.ImageUtils
import com.pairlink.app.domain.model.PairRequest
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassDialog

/**
 * Pair Request Dialog popup with partner name, avatar, partner ID, and accept/reject actions.
 */
@Composable
fun PairRequestDialog(
    partner: PartnerProfile = PartnerProfile(),
    pairRequest: PairRequest? = null,
    isLoading: Boolean = false,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val username = pairRequest?.senderUsername?.ifBlank { partner.name }?.ifBlank { "Partner" }
        ?: partner.name.ifBlank { "Partner" }
    val avatarUrl = pairRequest?.senderProfileImage?.ifBlank { partner.avatarUrl }
        ?: partner.avatarUrl
    val partnerId = pairRequest?.senderPartnerId?.ifBlank { partner.partnerId }
        ?: partner.partnerId

    GlassDialog(onDismissRequest = onReject) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
            contentPadding = 28.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Partner Avatar with glowing ring
                Box(
                    modifier = Modifier.size(116.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .shadow(24.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                            .clip(CircleShape)
                            .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.25f))
                    )

                    val imageModel = ImageUtils.getAvatarModel(avatarUrl)
                    if (imageModel != null) {
                        AsyncImage(
                            model = imageModel,
                            contentDescription = username,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.35f))
                                .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = username.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Partner Name & Message
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = username,
                        color = DesignTokens.Colors.TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$username wants to connect with you.",
                        color = DesignTokens.Colors.TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Partner ID and Request Time tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (partnerId.isNotBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "ID",
                                tint = DesignTokens.Colors.TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = partnerId,
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Time",
                            tint = DesignTokens.Colors.TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Just now",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        text = if (isLoading) "Connecting..." else "Accept",
                        enabled = !isLoading,
                        onClick = onAccept,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DesignTokens.Colors.PrimaryPink.copy(alpha = 0.35f),
                        borderColor = DesignTokens.Colors.PrimaryPink.copy(alpha = 0.6f),
                        leadingIcon = if (isLoading) {
                            {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = DesignTokens.Colors.PrimaryPink,
                                    strokeWidth = 2.dp
                                )
                            }
                        } else null
                    )

                    GlassButton(
                        text = "Reject",
                        enabled = !isLoading,
                        onClick = onReject,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White.copy(alpha = 0.08f),
                        textColor = DesignTokens.Colors.TextSecondary
                    )
                }
            }
        }
    }
}
