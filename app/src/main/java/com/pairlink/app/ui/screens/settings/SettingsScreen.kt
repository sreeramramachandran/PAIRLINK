package com.pairlink.app.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.data.local.preferences.NotificationPreferencesState
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.UserProfile
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassButton
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Settings Screen matching the Stitch prototype design with own Partner ID card and granular Notification Preferences.
 */
@Composable
fun SettingsScreen(
    partner: PartnerProfile,
    user: UserProfile,
    preferences: NotificationPreferencesState = NotificationPreferencesState(),
    onTogglePresence: () -> Unit = {},
    onToggleMood: () -> Unit = {},
    onToggleStatus: () -> Unit = {},
    onToggleReachedHome: () -> Unit = {},
    onToggleBirthday: () -> Unit = {},
    onToggleAnniversary: () -> Unit = {},
    onToggleSystem: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToUnpair: () -> Unit = {},
    onLogout: () -> Unit = {},
    currentTab: BottomNavTab = BottomNavTab.SETTINGS,
    onTabSelected: (BottomNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    fun copyPartnerId() {
        if (user.partnerId.isBlank()) return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("PairLink Partner ID", user.partnerId)
        clipboard.setPrimaryClip(clip)
        isCopied = true
        coroutineScope.launch {
            delay(2000)
            isCopied = false
        }
    }

    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(title = "Settings")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Header
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Settings",
                            color = DesignTokens.Colors.TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Manage your shared sanctuary preferences.",
                            color = DesignTokens.Colors.TextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    // Card 1: Your Unique Partner ID Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 18.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(DesignTokens.Colors.PrimaryPink.copy(alpha = 0.20f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = "Partner ID",
                                        tint = DesignTokens.Colors.PrimaryPink,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "YOUR PARTNER ID",
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = user.partnerId.ifBlank { "PAIR-..." },
                                        color = DesignTokens.Colors.TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCopied) DesignTokens.Colors.OnlineGreen.copy(alpha = 0.25f)
                                        else Color.White.copy(alpha = 0.12f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isCopied) DesignTokens.Colors.OnlineGreen else Color.White.copy(alpha = 0.25f),
                                        CircleShape
                                    )
                                    .clickable(onClick = ::copyPartnerId),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy ID",
                                    tint = if (isCopied) DesignTokens.Colors.OnlineGreen else DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Card 2: Joint Profile
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 20.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "Joint Profile",
                                    color = DesignTokens.Colors.TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Overlapping Avatars
                                Box(modifier = Modifier.size(width = 80.dp, height = 48.dp)) {
                                    AsyncImage(
                                        model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(partner.avatarUrl),
                                        contentDescription = "Partner",
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, DesignTokens.Colors.BackgroundMidnight, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    AsyncImage(
                                        model = com.pairlink.app.core.util.ImageUtils.getAvatarModel(user.avatarUrl),
                                        contentDescription = "User",
                                        modifier = Modifier
                                            .offset(x = 28.dp)
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, DesignTokens.Colors.BackgroundMidnight, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Column {
                                    Text(
                                        text = if (partner.name.isNotBlank()) "${user.username} & ${partner.name}" else user.username,
                                        color = DesignTokens.Colors.TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (user.isPaired) "Connected Sanctuary" else "Not paired yet",
                                        color = DesignTokens.Colors.TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            GlassButton(
                                text = "Manage Profiles",
                                onClick = onNavigateToProfile,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Card 3: Notification & Reminder Preferences
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 20.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "Notification Preferences",
                                    color = DesignTokens.Colors.TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            SettingsToggleRow(
                                title = "Presence Heartbeat Alerts",
                                subtitle = "Signals when partner is thinking of you",
                                checked = preferences.presenceEnabled,
                                onCheckedChange = { onTogglePresence() }
                            )

                            SettingsToggleRow(
                                title = "Mood Updates",
                                subtitle = "Alerts when partner updates their mood",
                                checked = preferences.moodEnabled,
                                onCheckedChange = { onToggleMood() }
                            )

                            SettingsToggleRow(
                                title = "Activity Status",
                                subtitle = "Working, sleeping, driving updates",
                                checked = preferences.statusEnabled,
                                onCheckedChange = { onToggleStatus() }
                            )

                            SettingsToggleRow(
                                title = "Reached Home Safety",
                                subtitle = "Immediate safe arrival notifications",
                                checked = preferences.reachedHomeEnabled,
                                onCheckedChange = { onToggleReachedHome() }
                            )

                            SettingsToggleRow(
                                title = "Birthday Countdown Reminders",
                                subtitle = "7, 3, 1 day & today notifications",
                                checked = preferences.birthdayEnabled,
                                onCheckedChange = { onToggleBirthday() }
                            )

                            SettingsToggleRow(
                                title = "Anniversary Milestones",
                                subtitle = "Month, week & today celebration alerts",
                                checked = preferences.anniversaryEnabled,
                                onCheckedChange = { onToggleAnniversary() }
                            )

                            SettingsToggleRow(
                                title = "System & Sanctuary Alerts",
                                subtitle = "Pairing and connection updates",
                                checked = preferences.systemEnabled,
                                onCheckedChange = { onToggleSystem() }
                            )
                        }
                    }

                    // Card 4: Privacy & Security
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 20.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Privacy",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "Privacy & Security",
                                    color = DesignTokens.Colors.TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            SettingsNavigationRow(
                                title = "Privacy Policy",
                                onClick = { /* View Policy */ }
                            )

                            SettingsNavigationRow(
                                title = "Change PIN",
                                onClick = { /* Change PIN */ }
                            )
                        }
                    }

                    // Card 5: App Settings & Danger Zone
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(DesignTokens.Radius.Large),
                        contentPadding = 20.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "App Settings",
                                    tint = DesignTokens.Colors.PrimaryPink,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "App Settings",
                                    color = DesignTokens.Colors.TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            SettingsNavigationRow(
                                title = "Appearance",
                                trailingText = "Dark (Default)",
                                onClick = { /* Theme dialog */ }
                            )

                            SettingsNavigationRow(
                                title = "Offline Persistence & Room Sync",
                                trailingText = "Active",
                                onClick = { /* Sync status */ }
                            )

                            SettingsDangerActionRow(
                                title = "Unlink Partner",
                                icon = Icons.Default.HeartBroken,
                                onClick = onNavigateToUnpair
                            )

                            SettingsDangerActionRow(
                                title = "Logout",
                                icon = Icons.AutoMirrored.Filled.Logout,
                                onClick = onLogout
                            )
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

@Composable
private fun SettingsNavigationRow(
    title: String,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = DesignTokens.Colors.TextPrimary,
            fontSize = 14.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            trailingText?.let {
                Text(
                    text = it,
                    color = DesignTokens.Colors.TextSecondary,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Chevron",
                tint = DesignTokens.Colors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                color = DesignTokens.Colors.TextPrimary,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = DesignTokens.Colors.TextSecondary,
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DesignTokens.Colors.PrimaryPink,
                checkedTrackColor = DesignTokens.Colors.SoftPink.copy(alpha = 0.4f),
                uncheckedThumbColor = DesignTokens.Colors.TextSecondary,
                uncheckedTrackColor = Color.White.copy(alpha = 0.10f)
            )
        )
    }
}

@Composable
private fun SettingsDangerActionRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = DesignTokens.Colors.DangerRose,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = DesignTokens.Colors.DangerRose,
            modifier = Modifier.size(20.dp)
        )
    }
}
