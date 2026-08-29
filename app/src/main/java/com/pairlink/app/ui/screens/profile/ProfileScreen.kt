package com.pairlink.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTopBar

/**
 * Partner Profile Screen displaying partner details, nickname, ID, real registered birthday, mood, and activity status.
 */
@Composable
fun ProfileScreen(
    partner: PartnerProfile,
    userNickname: String,
    onNavigateToEditProfile: () -> Unit,
    onNavigateBack: () -> Unit,
    currentTab: BottomNavTab = BottomNavTab.SETTINGS,
    onTabSelected: (BottomNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val displayNickname = userNickname.ifBlank { partner.nickname }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Partner Profile",
                    showBack = true,
                    onBackClick = onNavigateBack
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Profile Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                        contentPadding = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .shadow(24.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                                        .clip(CircleShape)
                                        .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.20f))
                                )
                                if (partner.avatarUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(partner.avatarUrl),
                                        contentDescription = partner.name,
                                        modifier = Modifier
                                            .size(108.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(108.dp)
                                            .clip(CircleShape)
                                            .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Partner Avatar",
                                            tint = DesignTokens.Colors.PrimaryPink,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = partner.name.ifBlank { "Partner" },
                                color = DesignTokens.Colors.TextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (displayNickname.isNotBlank()) {
                                Text(
                                    text = "\"$displayNickname\"",
                                    color = DesignTokens.Colors.PrimaryPink,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Italic
                                )
                            }

                            GlassButton(
                                text = "Edit Profile & Nickname",
                                onClick = onNavigateToEditProfile,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Bento Grid Details (Partner ID and Real Birthday)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Partner ID Card
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(DesignTokens.Radius.Large),
                            contentPadding = 18.dp
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = "Partner ID",
                                    tint = DesignTokens.Colors.TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "PARTNER ID",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFFF0F3))
                                        .border(1.dp, Color(0xFFFFB3C1), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = partner.partnerId.ifBlank { "Not set" },
                                        color = DesignTokens.Colors.PrimaryCrimson,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        // Birthday Card (Real Partner Birthday from Registration)
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(DesignTokens.Radius.Large),
                            contentPadding = 18.dp
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Cake,
                                    contentDescription = "Birthday",
                                    tint = DesignTokens.Colors.TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "BIRTHDAY",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = partner.dob.ifBlank { "Not set" },
                                    color = DesignTokens.Colors.TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Mood & Status Live Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 20.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Mood Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(DesignTokens.Colors.Lavender.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mood,
                                        contentDescription = "Mood",
                                        tint = DesignTokens.Colors.Lavender,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "CURRENT MOOD",
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = partner.currentMood.ifBlank { "Happy" },
                                        color = DesignTokens.Colors.Lavender,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Status Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(DesignTokens.Colors.VibrantPink.copy(alpha = 0.20f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Work,
                                        contentDescription = "Status",
                                        tint = DesignTokens.Colors.VibrantPink,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "CURRENT ACTIVITY",
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = partner.currentStatus.ifBlank { "Available" },
                                        color = DesignTokens.Colors.TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(1.dp))
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "STATUS MESSAGE",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "\"${partner.statusMessage.ifBlank { "Connected to our private sanctuary. ✨" }}\"",
                                    color = DesignTokens.Colors.TextPrimary.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.padding(bottom = 80.dp))
                }
            }

            GlassBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
