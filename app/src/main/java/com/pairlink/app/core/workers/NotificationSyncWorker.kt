package com.pairlink.app.core.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pairlink.app.MainActivity
import com.pairlink.app.R
import com.pairlink.app.core.notifications.NotificationChannelsHelper
import com.pairlink.app.data.local.PairLinkDatabase
import com.pairlink.app.data.local.entity.NotificationEntity
import com.pairlink.app.domain.model.NotificationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Background WorkManager worker that queries unread partner notifications from Firestore
 * and posts system status bar notifications with screen wakeup & 2.4s vibration pattern
 * even if the app process is closed or cleared from recent apps.
 */
class NotificationSyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext Result.success()

        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("receiverUid", uid)
                .whereEqualTo("read", false)
                .get()
                .await()

            if (snapshot != null && !snapshot.isEmpty) {
                val db = androidx.room.Room.databaseBuilder(
                    context,
                    PairLinkDatabase::class.java,
                    "pairlink.db"
                ).fallbackToDestructiveMigration().build()
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

                for (doc in snapshot.documents) {
                    val notifId = doc.getString("id") ?: doc.id
                    val title = doc.getString("title") ?: "PairLink Update"
                    val message = doc.getString("message") ?: "New update in your sanctuary."
                    val typeStr = doc.getString("type") ?: "SYSTEM"
                    val senderUid = doc.getString("senderUid") ?: ""
                    val iconName = doc.getString("iconName") ?: "favorite"
                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                    val parsedType = try {
                        NotificationType.valueOf(typeStr)
                    } catch (_: Exception) {
                        NotificationType.SYSTEM
                    }

                    // 1. Save to local Room DB
                    val entity = NotificationEntity(
                        id = notifId,
                        title = title,
                        message = message,
                        type = parsedType.name,
                        timestamp = timestamp,
                        isRead = true,
                        iconName = iconName,
                        senderUid = senderUid
                    )
                    db.notificationDao().insertNotification(entity)

                    // 2. Wake screen display from sleep/black screen
                    try {
                        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                        val wakeLock = powerManager?.newWakeLock(
                            PowerManager.FULL_WAKE_LOCK or
                                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                                    PowerManager.ON_AFTER_RELEASE,
                            "PairLink:WorkerWakeLock"
                        )
                        wakeLock?.acquire(3000L)
                    } catch (e: Exception) {
                        Timber.w(e, "Could not acquire WakeLock in worker")
                    }

                    // 3. Post system notification with 2.4s heartbeat vibration
                    val channelId = when (parsedType) {
                        NotificationType.PRESENCE -> NotificationChannelsHelper.CHANNEL_PRESENCE
                        NotificationType.MOOD_UPDATED -> NotificationChannelsHelper.CHANNEL_MOOD
                        NotificationType.STATUS_UPDATED, NotificationType.REACHED_HOME -> NotificationChannelsHelper.CHANNEL_STATUS
                        NotificationType.BIRTHDAY_REMINDER, NotificationType.BIRTHDAY_TODAY -> NotificationChannelsHelper.CHANNEL_BIRTHDAY
                        NotificationType.ANNIVERSARY_REMINDER, NotificationType.ANNIVERSARY_TODAY -> NotificationChannelsHelper.CHANNEL_ANNIVERSARY
                        NotificationType.PAIR_REQUEST, NotificationType.PAIR_ACCEPTED, NotificationType.UNPAIR -> NotificationChannelsHelper.CHANNEL_RELATIONSHIP
                        NotificationType.SYSTEM -> NotificationChannelsHelper.CHANNEL_SYSTEM
                    }

                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        parsedType.ordinal,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val heartbeatPattern = longArrayOf(0, 350, 150, 450, 150, 600, 200, 500)

                    val notification = NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVibrate(heartbeatPattern)
                        .setFullScreenIntent(pendingIntent, true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .build()

                    notificationManager?.notify((1000..9999).random(), notification)

                    // Mark read in Firestore so it is not re-posted
                    FirebaseFirestore.getInstance()
                        .collection("notifications")
                        .document(doc.id)
                        .set(mapOf("read" to true), SetOptions.merge())
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error executing NotificationSyncWorker: %s", e.message)
            Result.retry()
        }
    }
}
