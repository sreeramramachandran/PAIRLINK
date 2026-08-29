package com.pairlink.app.core.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Creates and maintains all distinct Android Notification Channels for PairLink
 * with explicit Notification.VISIBILITY_PUBLIC for 100% lock-screen display.
 */
object NotificationChannelsHelper {

    const val CHANNEL_PRESENCE = "presence_channel"
    const val CHANNEL_RELATIONSHIP = "relationship_channel"
    const val CHANNEL_MOOD = "mood_channel"
    const val CHANNEL_STATUS = "status_channel"
    const val CHANNEL_BIRTHDAY = "birthday_channel"
    const val CHANNEL_ANNIVERSARY = "anniversary_channel"
    const val CHANNEL_SYSTEM = "system_channel"

    fun createAllNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val channels = listOf(
            NotificationChannel(
                CHANNEL_PRESENCE,
                "Presence Heartbeats",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Live touch and heartbeat presence signals from your partner."
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                vibrationPattern = longArrayOf(0, 130, 90, 220, 550)
            },
            NotificationChannel(
                CHANNEL_RELATIONSHIP,
                "Relationship & Pairing",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pair requests, connection status, and sanctuary pairing alerts."
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
            NotificationChannel(
                CHANNEL_MOOD,
                "Mood Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Emotional updates when your partner shares a new mood."
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
            NotificationChannel(
                CHANNEL_STATUS,
                "Activity Status",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Activity and 'Reached Home' safety updates from your partner."
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
            NotificationChannel(
                CHANNEL_BIRTHDAY,
                "Birthday Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Meaningful reminders counting down to your partner's special day."
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
            NotificationChannel(
                CHANNEL_ANNIVERSARY,
                "Anniversary Milestones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Anniversary countdowns and relationship milestone celebrations."
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            },
            NotificationChannel(
                CHANNEL_SYSTEM,
                "System & Sanctuary",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "General sanctuary and account notifications."
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        )

        channels.forEach { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }
}
