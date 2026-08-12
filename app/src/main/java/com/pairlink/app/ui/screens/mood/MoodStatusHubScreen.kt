package com.pairlink.app.ui.screens.mood

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.UserProfile
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTopBar

/**
 * Mood & Status Hub Screen containing two interactive boxes:
 * 1. User's Current Mood Box (navigates to MoodScreen)
 * 2. User's Current Status Box (navigates to UpdateStatusScreen)
 */
@Composable
fun MoodStatusHubScreen(
    user: UserProfile,
    onNavigateToMood: () -> Unit,
    onNavigateToStatus: () -> Unit,
    onBackClick: () -> Unit,
    currentTab: BottomNavTab = BottomNavTab.MOOD,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HubPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Glass Top Bar
                GlassTopBar(
                    title = "My Mood & Status",
                    onBackClick = onBackClick
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header Subtitle
                    Text(
                        text = "YOUR SANCTUARY AURA",
                        color = DesignTokens.Colors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // 1. YOUR MOOD BOX
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                        contentPadding = 22.dp,
                        onClick = onNavigateToMood
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.20f))
                                            .border(1.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mood,
                                            contentDescription = "Mood Icon",
                                            tint = DesignTokens.Colors.PrimaryPink,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "YOUR CURRENT MOOD",
                                            color = DesignTokens.Colors.TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = user.currentMood.ifBlank { "Happy" },
                                            color = DesignTokens.Colors.TextPrimary,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (-0.3).sp
                                        )
                                    }
                                }

                                // Interactive Pill Indicator
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.15f))
                                        .border(1.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Update",
                                        color = DesignTokens.Colors.PrimaryPink,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Mood Description & Prompt
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Express how you're feeling to your partner.",
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Go to Mood",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 2. YOUR STATUS BOX
                    val statusIcon = when {
                        user.currentStatus.contains("Home", ignoreCase = true) -> Icons.Default.Home
                        user.currentStatus.contains("Driv", ignoreCase = true) -> Icons.Default.DirectionsCar
                        user.currentStatus.contains("Sleep", ignoreCase = true) -> Icons.Default.Nightlight
                        else -> Icons.Default.Work
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                        contentPadding = 22.dp,
                        onClick = onNavigateToStatus
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(DesignTokens.Colors.Lavender.copy(alpha = 0.20f))
                                            .border(1.dp, DesignTokens.Colors.Lavender.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = statusIcon,
                                            contentDescription = "Status Icon",
                                            tint = DesignTokens.Colors.Lavender,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "YOUR CURRENT STATUS",
                                            color = DesignTokens.Colors.TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = user.currentStatus.ifBlank { "Available" },
                                            color = DesignTokens.Colors.TextPrimary,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (-0.3).sp
                                        )
                                    }
                                }

                                // Interactive Pill Indicator
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(DesignTokens.Colors.Lavender.copy(alpha = 0.15f))
                                        .border(1.dp, DesignTokens.Colors.Lavender.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Change",
                                        color = DesignTokens.Colors.Lavender,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Status Description & Prompt
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(DesignTokens.Radius.Medium))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = user.statusMessage.ifBlank { "Let your partner know what you're doing right now." },
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Go to Status",
                                        tint = DesignTokens.Colors.Lavender,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Extra bottom padding for floating bottom navigation
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Floating Capsule Bottom Navigation Bar
            GlassBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
