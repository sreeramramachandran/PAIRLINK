package com.pairlink.app.core.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pairlink.app.MainActivity
import com.pairlink.app.R
import com.pairlink.app.core.notifications.NotificationChannelsHelper
import com.pairlink.app.data.local.PairLinkDatabase
import com.pairlink.app.data.local.entity.NotificationEntity
import com.pairlink.app.data.local.preferences.NotificationPreferences
import com.pairlink.app.domain.model.NotificationType
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Daily background worker checking and scheduling anniversary milestone reminders.
 */
class AnniversaryReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = androidx.room.Room.databaseBuilder(
                context,
                PairLinkDatabase::class.java,
                "pairlink.db"
            ).build()

            val preferences = NotificationPreferences(context)
            val prefs = preferences.preferencesFlow.first()
            if (!prefs.anniversaryEnabled) return Result.success()

            val startDateStr = inputData.getString("relationship_date")
            if (startDateStr.isNullOrBlank()) return Result.success()

            val today = LocalDate.now()
            val startDate = LocalDate.parse(startDateStr)
            var nextAnniversary = startDate.withYear(today.year)
            if (nextAnniversary.isBefore(today)) {
                nextAnniversary = nextAnniversary.plusYears(1)
            }

            val daysLeft = ChronoUnit.DAYS.between(today, nextAnniversary)

            val (title, message, type) = when (daysLeft) {
                0L -> Triple("💍 Happy Anniversary ❤️", "Celebrating another beautiful year of love together!", NotificationType.ANNIVERSARY_TODAY)
                1L -> Triple("❤️ Tomorrow is your Anniversary!", "Only 1 day until your special anniversary milestone.", NotificationType.ANNIVERSARY_REMINDER)
                3L -> Triple("❤️ Only 3 days until your Anniversary", "Cherishing the beautiful journey you share.", NotificationType.ANNIVERSARY_REMINDER)
                7L -> Triple("❤️ Only 7 days until your anniversary", "One week until your relationship celebration.", NotificationType.ANNIVERSARY_REMINDER)
                30L -> Triple("❤️ 1 Month Until Your Anniversary", "A milestone is approaching in your sanctuary.", NotificationType.ANNIVERSARY_REMINDER)
                else -> return Result.success()
            }

            // Save to Room Database
            val entity = NotificationEntity(
                id = "ANNIV-${System.currentTimeMillis()}",
                title = title,
                message = message,
                type = type.name,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                iconName = "favorite",
                senderUid = ""
            )
            database.notificationDao().insertNotification(entity)

            // Post System Notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1003,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, NotificationChannelsHelper.CHANNEL_ANNIVERSARY)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(2002, notification)

            Result.success()
        } catch (_: Exception) {
            Result.success()
        }
    }
}
