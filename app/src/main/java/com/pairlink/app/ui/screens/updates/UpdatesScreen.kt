package com.pairlink.app.ui.screens.updates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens
import com.pairlink.app.domain.model.NotificationCategoryFilter
import com.pairlink.app.domain.model.NotificationItem
import com.pairlink.app.ui.components.AnimatedMeshBackground
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.components.GlassBottomNavigation
import com.pairlink.app.ui.components.GlassCard
import com.pairlink.app.ui.components.GlassChip
import com.pairlink.app.ui.components.GlassTextField
import com.pairlink.app.ui.components.GlassTopBar
import com.pairlink.app.ui.components.NotificationItemCard

/**
 * Full Notification Center screen with search, category filtering, Room persistence, and unread management.
 */
@Composable
fun UpdatesScreen(
    notifications: List<NotificationItem>,
    unreadCount: Int = 0,
    searchQuery: String = "",
    selectedFilter: NotificationCategoryFilter = NotificationCategoryFilter.ALL,
    partnerName: String = "Partner",
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterSelected: (NotificationCategoryFilter) -> Unit = {},
    onNotificationClick: (NotificationItem) -> Unit = {},
    onMarkAllAsRead: () -> Unit = {},
    onDeleteNotification: (String) -> Unit = {},
    currentTab: BottomNavTab = BottomNavTab.UPDATES,
    onTabSelected: (BottomNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedMeshBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassTopBar(
                    title = "Notification Center",
                    actions = {
                        if (unreadCount > 0) {
                            IconButton(onClick = onMarkAllAsRead) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Mark All As Read",
                                    tint = DesignTokens.Colors.PrimaryPink
                                )
                            }
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Subtitle & Unread Counter Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Timeline & Alerts",
                                color = DesignTokens.Colors.TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Stay connected with $partnerName's moments.",
                                color = DesignTokens.Colors.TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(DesignTokens.Colors.PrimaryPink)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount New",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Search Bar
                    GlassTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = "Search notifications...",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = DesignTokens.Colors.TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NotificationCategoryFilter.entries.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            val label = when (filter) {
                                NotificationCategoryFilter.ALL -> "All"
                                NotificationCategoryFilter.PRESENCE -> "Presence"
                                NotificationCategoryFilter.MOOD -> "Mood"
                                NotificationCategoryFilter.STATUS -> "Status"
                                NotificationCategoryFilter.BIRTHDAY -> "Birthday"
                                NotificationCategoryFilter.ANNIVERSARY -> "Anniversary"
                                NotificationCategoryFilter.SYSTEM -> "System"
                            }

                            GlassChip(
                                text = label,
                                selected = isSelected,
                                onClick = { onFilterSelected(filter) }
                            )
                        }
                    }

                    // Notifications List
                    if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsNone,
                                        contentDescription = "Empty",
                                        tint = DesignTokens.Colors.TextSecondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No notifications match '$searchQuery'" else "Your sanctuary is serene and calm.",
                                    color = DesignTokens.Colors.TextSecondary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(notifications, key = { it.id }) { item ->
                                NotificationItemCard(
                                    item = item,
                                    onClick = { onNotificationClick(item) },
                                    onDelete = { onDeleteNotification(item.id) }
                                )
                            }

                            item {
                                Box(modifier = Modifier.padding(bottom = 80.dp))
                            }
                        }
                    }
                }
            }

            GlassBottomNavigation(
                currentTab = currentTab,
                unreadNotificationsCount = unreadCount,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
