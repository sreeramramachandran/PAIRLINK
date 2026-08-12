package com.pairlink.app.core.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

/**
 * Schedules periodic daily background checks for birthday and anniversary reminders.
 */
object DailyNotificationScheduler {

    private const val WORK_NAME_BIRTHDAY = "pairlink_birthday_reminder_work"
    private const val WORK_NAME_ANNIVERSARY = "pairlink_anniversary_reminder_work"

    fun scheduleDailyReminders(
        context: Context,
        partnerDob: String = "",
        partnerName: String = "Partner",
        relationshipDate: String = ""
    ) {
        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // 1. Birthday Work Request
        if (partnerDob.isNotBlank()) {
            val birthdayWork = PeriodicWorkRequestBuilder<BirthdayReminderWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInputData(
                    workDataOf(
                        "partner_dob" to partnerDob,
                        "partner_name" to partnerName
                    )
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME_BIRTHDAY,
                ExistingPeriodicWorkPolicy.KEEP,
                birthdayWork
            )
        }

        // 2. Anniversary Work Request
        if (relationshipDate.isNotBlank()) {
            val anniversaryWork = PeriodicWorkRequestBuilder<AnniversaryReminderWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInputData(
                    workDataOf(
                        "relationship_date" to relationshipDate
                    )
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME_ANNIVERSARY,
                ExistingPeriodicWorkPolicy.KEEP,
                anniversaryWork
            )
        }
    }
}
