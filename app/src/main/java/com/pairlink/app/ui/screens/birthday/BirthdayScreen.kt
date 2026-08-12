package com.pairlink.app.ui.screens.birthday

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.WishlistItem
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTextField
import com.pairlink.app.ui.components.GlassTopBar
import com.pairlink.app.ui.components.GradientButton

/**
 * Birthday Screen featuring glowing hero countdown and gift ideas wishlist.
 * Partner's birthday is set at registration and is strictly permanent and read-only.
 */
@Composable
fun BirthdayScreen(
    partner: PartnerProfile,
    daysLeft: Int,
    formattedBirthday: String = "Oct 24th",
    wishlist: List<WishlistItem>,
    onAddWishlistItem: (String) -> Unit,
    onDeleteWishlistItem: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    currentTab: BottomNavTab = BottomNavTab.UPDATES,
    onTabSelected: (BottomNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showWishlistItems by remember { mutableStateOf(true) }
    var newWishlistTitle by remember { mutableStateOf("") }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Birthday",
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
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Hero Profile Avatar
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(130.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .shadow(28.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                                    .clip(CircleShape)
                                    .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.25f))
                            )
                            AsyncImage(
                                model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(partner.avatarUrl),
                                contentDescription = partner.name,
                                modifier = Modifier
                                    .size(112.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            text = "${partner.name.ifBlank { "Partner" }}'s Birthday",
                            color = DesignTokens.Colors.TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Let's make it unforgettable. ✨",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    // Large Glowing Countdown Card
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
                            Text(
                                text = "COUNTDOWN",
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )

                            Text(
                                text = "$daysLeft",
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 64.sp
                            )

                            Text(
                                text = "Days Left",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(1.dp))
                            )

                            // Read-only Birthday Date Display
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cake,
                                        contentDescription = "Date",
                                        tint = DesignTokens.Colors.VibrantPink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = formattedBirthday,
                                        color = DesignTokens.Colors.TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(0.8.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = DesignTokens.Colors.TextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Registered Date",
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Gift Ideas Wishlist Section
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 20.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showWishlistItems = !showWishlistItems },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Redeem,
                                        contentDescription = "Gift Ideas",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Gift Ideas & Notes (${wishlist.size})",
                                        color = DesignTokens.Colors.TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Icon(
                                    imageVector = if (showWishlistItems) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Toggle",
                                    tint = DesignTokens.Colors.TextSecondary
                                )
                            }

                            AnimatedVisibility(
                                visible = showWishlistItems,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        GlassTextField(
                                            value = newWishlistTitle,
                                            onValueChange = { newWishlistTitle = it },
                                            placeholder = "Add gift idea...",
                                            modifier = Modifier.weight(1f)
                                        )

                                        GradientButton(
                                            text = "Add",
                                            onClick = {
                                                if (newWishlistTitle.isNotBlank()) {
                                                    onAddWishlistItem(newWishlistTitle.trim())
                                                    newWishlistTitle = ""
                                                }
                                            },
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }

                                    if (wishlist.isEmpty()) {
                                        Text(
                                            text = "No gift ideas yet. Add items above to surprise your partner!",
                                            color = DesignTokens.Colors.TextSecondary,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    } else {
                                        wishlist.forEach { item ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                                    .background(Color.White.copy(alpha = 0.05f))
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = item.title,
                                                    color = DesignTokens.Colors.TextPrimary,
                                                    fontSize = 14.sp,
                                                    modifier = Modifier.weight(1f)
                                                )

                                                IconButton(
                                                    onClick = { onDeleteWishlistItem(item.id) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Delete item",
                                                        tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
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
