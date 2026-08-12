package com.pairlink.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.ui.navigation.Screen

data class ScreenItem(
    val route: String,
    val label: String,
    val group: String
)

val AllScreenItems = listOf(
    // Auth & Onboarding
    ScreenItem(Screen.Splash.route, "1. Splash Screen", "Auth & Onboarding"),
    ScreenItem(Screen.Welcome.route, "2. Welcome Screen", "Auth & Onboarding"),
    ScreenItem(Screen.Login.route, "3. Login", "Auth & Onboarding"),
    ScreenItem(Screen.Register.route, "4. Register", "Auth & Onboarding"),
    ScreenItem(Screen.RegisterSuccess.route, "5. Registration Success", "Auth & Onboarding"),
    ScreenItem(Screen.Connect.route, "6. Connect Partner ID", "Auth & Onboarding"),
    ScreenItem(Screen.PairRequest.route, "7. Pair Request Dialog", "Auth & Onboarding"),
    ScreenItem(Screen.ConnectedForever.route, "8. Connected Forever", "Auth & Onboarding"),
    ScreenItem(Screen.RelationshipSetup.route, "9. Personalize Journey", "Auth & Onboarding"),

    // Main Sanctuary
    ScreenItem(Screen.Home.route, "10. Home Sanctuary", "Main Sanctuary"),
    ScreenItem(Screen.Mood.route, "11. Mood Selector", "Main Sanctuary"),
    ScreenItem(Screen.Status.route, "12. Quick Update Status", "Main Sanctuary"),
    ScreenItem(Screen.Updates.route, "13. Updates & Timeline", "Main Sanctuary"),
    ScreenItem(Screen.Birthday.route, "14. Birthday Countdown", "Main Sanctuary"),
    ScreenItem(Screen.Anniversary.route, "15. Anniversary Milestones", "Main Sanctuary"),

    // Profile & Settings
    ScreenItem(Screen.Profile.route, "16. Partner Profile", "Profile & Settings"),
    ScreenItem(Screen.EditProfile.route, "17. Edit Profile", "Profile & Settings"),
    ScreenItem(Screen.Settings.route, "18. Settings", "Profile & Settings"),
    ScreenItem(Screen.Unpair.route, "19. Unpair Dialog", "Profile & Settings")
)

/**
 * Developer Quick Screen Switcher to immediately preview & jump to any screen.
 */
@Composable
fun QuickScreenSwitcher(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        // Floating Trigger Button
        Box(
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp)
                .size(42.dp)
                .shadow(12.dp, CircleShape, spotColor = DesignTokens.Colors.PrimaryPink)
                .clip(CircleShape)
                .background(DesignTokens.Colors.BackgroundMidnight.copy(alpha = 0.85f))
                .border(1.dp, DesignTokens.Colors.PrimaryPink.copy(alpha = 0.4f), CircleShape)
                .clickable { isExpanded = !isExpanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = "Screen Switcher",
                tint = DesignTokens.Colors.PrimaryPink,
                modifier = Modifier.size(20.dp)
            )
        }

        // Expanded Screen Navigator Panel
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { isExpanded = false },
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight(0.80f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(DesignTokens.Radius.Large),
                    contentPadding = 20.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PAIRLINK SCREENS (19)",
                                color = DesignTokens.Colors.PrimaryPink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = DesignTokens.Colors.TextSecondary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { isExpanded = false }
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val groups = AllScreenItems.groupBy { it.group }
                            groups.forEach { (groupName, items) ->
                                item {
                                    Text(
                                        text = groupName.uppercase(),
                                        color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.6f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                    )
                                }
                                items(items) { item ->
                                    val isSelected = currentRoute == item.route
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(DesignTokens.Radius.Small))
                                            .background(
                                                if (isSelected) DesignTokens.Colors.PrimaryPink.copy(alpha = 0.25f)
                                                else Color.White.copy(alpha = 0.05f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) DesignTokens.Colors.PrimaryPink.copy(alpha = 0.6f)
                                                else Color.Transparent,
                                                shape = RoundedCornerShape(DesignTokens.Radius.Small)
                                            )
                                            .clickable {
                                                onNavigateToRoute(item.route)
                                                isExpanded = false
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.label,
                                                color = if (isSelected) DesignTokens.Colors.PrimaryPink else DesignTokens.Colors.TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (isSelected) {
                                                Text(
                                                    text = "●",
                                                    color = DesignTokens.Colors.PrimaryPink,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
