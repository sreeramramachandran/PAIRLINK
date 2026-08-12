package com.pairlink.app.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pairlink.app.core.workers.DailyNotificationScheduler

/**
 * Re-schedules daily WorkManager notifications upon system boot completion.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            DailyNotificationScheduler.scheduleDailyReminders(context)
        }
    }
}
