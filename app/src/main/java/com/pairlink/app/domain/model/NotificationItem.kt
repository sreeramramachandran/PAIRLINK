package com.pairlink.app.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Supported notification types across PairLink.
 */
enum class NotificationType {
    PAIR_REQUEST,
    PAIR_ACCEPTED,
    PRESENCE,
    MOOD_UPDATED,
    STATUS_UPDATED,
    REACHED_HOME,
    BIRTHDAY_REMINDER,
    BIRTHDAY_TODAY,
    ANNIVERSARY_REMINDER,
    ANNIVERSARY_TODAY,
    UNPAIR,
    SYSTEM
}

/**
 * Filter categories for Notification Center.
 */
enum class NotificationCategoryFilter {
    ALL,
    PRESENCE,
    MOOD,
    STATUS,
    BIRTHDAY,
    ANNIVERSARY,
    SYSTEM
}

/**
 * Domain model representing a notification in the Notification Center.
 */
data class NotificationItem(
    val id: String = System.currentTimeMillis().toString(),
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val iconName: String = "favorite",
    val senderUid: String = ""
) {
    val formattedTime: String
        get() {
            val diff = System.currentTimeMillis() - timestamp
            val minutes = diff / (1000 * 60)
            val hours = minutes / 60
            val days = hours / 24

            return when {
                minutes < 2 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days == 1L -> "Yesterday"
                days < 7 -> "${days}d ago"
                else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
            }
        }
}
