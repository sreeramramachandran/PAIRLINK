package com.pairlink.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mood
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.PresenceRingState
import com.pairlink.app.domain.model.UserProfile
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BentoFeatureCard
import com.pairlink.app.ui.components.BirthdayCard
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTopBar
import com.pairlink.app.ui.components.PartnerProfileCard
import com.pairlink.app.ui.components.PresenceHeartButton
import com.pairlink.app.ui.components.RelationshipCard
import com.pairlink.app.ui.components.StatusCard

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext

/**
 * Signature Home Sanctuary Screen with real-time Presence Heart, user's avatar on top-left, and partner's live display.
 * Partner's Mood and Status cards on Home are display-only to avoid navigation confusion.
 */
@Composable
fun HomeScreen(
    partner: PartnerProfile,
    user: UserProfile = UserProfile(),
    partnerDisplayName: String = "",
    togetherTime: String,
    birthdayDaysLeft: String,
    heartbeatCount: Int,
    isPartnerHolding: Boolean = false,
    presenceRingState: PresenceRingState = PresenceRingState.ONLINE,
    onHeartPressed: () -> Unit = {},
    onHeartReleased: () -> Unit = {},
    onSendHeart: () -> Unit = {},
    onNavigateToBirthday: () -> Unit,
    onNavigateToRelationship: () -> Unit,
    onNavigateToProfile: () -> Unit,
    currentTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler(enabled = true) {
        (context as? Activity)?.moveTaskToBack(true)
    }

    val displayName = partnerDisplayName.ifBlank { partner.name.ifBlank { "Partner" } }
    val userAvatar = user.profileImageUrl.ifBlank { user.avatarUrl.ifBlank { null } }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar with User's Own Avatar Circle and Username on Top-Left
                GlassTopBar(
                    title = user.username.ifBlank { "You" },
                    userAvatarUrl = userAvatar,
                    showUserAvatar = true,
                    onAvatarClick = onNavigateToProfile,
                    onSendHeartClick = onSendHeart
                )

                // Scrollable Sanctuary Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Large Frosted Hero Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.SuperLarge),
                        contentPadding = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            PartnerProfileCard(
                                partner = partner,
                                partnerDisplayName = displayName,
                                ringState = presenceRingState
                            )

                            // Heart Pulse Button with Press-Hold-Release Presence
                            PresenceHeartButton(
                                heartbeatCount = heartbeatCount,
                                isPartnerHolding = isPartnerHolding,
                                partnerName = displayName,
                                onHeartPressed = onHeartPressed,
                                onHeartReleased = onHeartReleased
                            )
                        }
                    }

                    // Bento Grid: 2 Columns of Display Cards for Partner's Live State
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Left Column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            BentoFeatureCard(
                                title = "$displayName's Mood",
                                value = partner.currentMood.ifBlank { "Happy" },
                                icon = Icons.Default.Mood,
                                iconColor = DesignTokens.Colors.PrimaryPink,
                                iconBackground = DesignTokens.Colors.PrimaryPink.copy(alpha = 0.20f),
                                onClick = null // Display-only on Home
                            )

                            RelationshipCard(
                                togetherTime = togetherTime,
                                title = "Together For",
                                onClick = onNavigateToRelationship
                            )
                        }

                        // Right Column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            StatusCard(
                                currentStatus = partner.currentStatus.ifBlank { "Available" },
                                title = "$displayName's Status",
                                onClick = null // Display-only on Home
                            )

                            BirthdayCard(
                                daysLeft = birthdayDaysLeft,
                                title = "$displayName's Birthday",
                                onClick = onNavigateToBirthday
                            )
                        }
                    }

                    // Extra bottom spacing to clear floating bottom bar
                    Box(modifier = Modifier.padding(bottom = 80.dp))
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
