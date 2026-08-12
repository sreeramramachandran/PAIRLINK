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
 * Daily background worker checking and scheduling partner birthday reminders.
 */
class BirthdayReminderWorker(
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
            if (!prefs.birthdayEnabled) return Result.success()

            // Calculate birthday countdown
            val dobStr = inputData.getString("partner_dob") ?: "1997-10-24"
            val partnerName = inputData.getString("partner_name") ?: "Chinnu"

            val today = LocalDate.now()
            val dob = LocalDate.parse(dobStr)
            var nextBirthday = dob.withYear(today.year)
            if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1)
            }

            val daysLeft = ChronoUnit.DAYS.between(today, nextBirthday)

            val (title, message, type) = when (daysLeft) {
                0L -> Triple("🎉 Today is $partnerName's Birthday!", "Make today unforgettable for your partner! ❤️", NotificationType.BIRTHDAY_TODAY)
                1L -> Triple("🎂 Tomorrow is $partnerName's Birthday ❤️", "Only 1 day left until your partner's special day!", NotificationType.BIRTHDAY_REMINDER)
                3L -> Triple("🎂 3 Days Until $partnerName's Birthday", "Time to prepare something special! ✨", NotificationType.BIRTHDAY_REMINDER)
                7L -> Triple("🎂 1 Week Until $partnerName's Birthday", "7 days left until the celebration. ❤️", NotificationType.BIRTHDAY_REMINDER)
                else -> return Result.success()
            }

            // Save to Room Database
            val entity = NotificationEntity(
                id = "BDAY-${System.currentTimeMillis()}",
                title = title,
                message = message,
                type = type.name,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                iconName = "cake",
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
                1002,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, NotificationChannelsHelper.CHANNEL_BIRTHDAY)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(2001, notification)

            Result.success()
        } catch (_: Exception) {
            Result.success()
        }
    }
}
