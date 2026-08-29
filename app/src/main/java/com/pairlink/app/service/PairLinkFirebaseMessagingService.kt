package com.pairlink.app.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pairlink.app.MainActivity
import com.pairlink.app.R
import com.pairlink.app.core.notifications.NotificationChannelsHelper
import com.pairlink.app.data.local.PairLinkDatabase
import com.pairlink.app.data.local.entity.NotificationEntity
import com.pairlink.app.data.local.preferences.NotificationPreferences
import com.pairlink.app.domain.model.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Firebase Cloud Messaging Service handling real-time push notifications,
 * user preference filtering, and Room database persistence.
 */
class PairLinkFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private val database: PairLinkDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            PairLinkDatabase::class.java,
            "pairlink.db"
        ).fallbackToDestructiveMigration().build()
    }

    private val preferences: NotificationPreferences by lazy {
        NotificationPreferences(applicationContext)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("fcmToken", token, "updatedAt", System.currentTimeMillis())
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val rawType = data["type"] ?: "SYSTEM"
        val title = data["title"] ?: remoteMessage.notification?.title ?: "PairLink"
        val message = data["message"] ?: data["body"] ?: remoteMessage.notification?.body ?: "New sanctuary update."
        val senderUid = data["senderUid"] ?: ""
        val iconName = data["iconName"] ?: "favorite"

        val parsedType = try {
            NotificationType.valueOf(rawType)
        } catch (_: Exception) {
            NotificationType.SYSTEM
        }

        serviceScope.launch {
            val prefs = preferences.preferencesFlow.first()

            // Check if notification is enabled in user preferences
            val isEnabled = when (parsedType) {
                NotificationType.PRESENCE -> prefs.presenceEnabled
                NotificationType.MOOD_UPDATED -> prefs.moodEnabled
                NotificationType.STATUS_UPDATED -> prefs.statusEnabled
                NotificationType.REACHED_HOME -> prefs.reachedHomeEnabled
                NotificationType.BIRTHDAY_REMINDER, NotificationType.BIRTHDAY_TODAY -> prefs.birthdayEnabled
                NotificationType.ANNIVERSARY_REMINDER, NotificationType.ANNIVERSARY_TODAY -> prefs.anniversaryEnabled
                NotificationType.PAIR_REQUEST, NotificationType.PAIR_ACCEPTED, NotificationType.UNPAIR, NotificationType.SYSTEM -> prefs.systemEnabled
            }

            if (!isEnabled) return@launch

            // 1. Save to Room database
            val entity = NotificationEntity(
                id = "FCM-${System.currentTimeMillis()}-${(100..999).random()}",
                title = title,
                message = message,
                type = parsedType.name,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                iconName = iconName,
                senderUid = senderUid
            )
            database.notificationDao().insertNotification(entity)

            // 2. Post System Notification on corresponding channel
            val channelId = when (parsedType) {
                NotificationType.PRESENCE -> NotificationChannelsHelper.CHANNEL_PRESENCE
                NotificationType.MOOD_UPDATED -> NotificationChannelsHelper.CHANNEL_MOOD
                NotificationType.STATUS_UPDATED, NotificationType.REACHED_HOME -> NotificationChannelsHelper.CHANNEL_STATUS
                NotificationType.BIRTHDAY_REMINDER, NotificationType.BIRTHDAY_TODAY -> NotificationChannelsHelper.CHANNEL_BIRTHDAY
                NotificationType.ANNIVERSARY_REMINDER, NotificationType.ANNIVERSARY_TODAY -> NotificationChannelsHelper.CHANNEL_ANNIVERSARY
                NotificationType.PAIR_REQUEST, NotificationType.PAIR_ACCEPTED, NotificationType.UNPAIR -> NotificationChannelsHelper.CHANNEL_RELATIONSHIP
                NotificationType.SYSTEM -> NotificationChannelsHelper.CHANNEL_SYSTEM
            }

            showSystemNotification(channelId, title, message, parsedType)
        }
    }

    private fun showSystemNotification(
        channelId: String,
        title: String,
        message: String,
        type: NotificationType
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            type.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify((1000..9999).random(), notification)
    }
}
