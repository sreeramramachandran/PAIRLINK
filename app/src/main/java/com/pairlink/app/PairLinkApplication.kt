package com.pairlink.app

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.pairlink.app.core.notifications.NotificationChannelsHelper
import com.pairlink.app.core.workers.NotificationSyncWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Main Application class for PairLink.
 * Initializes Hilt, Timber logging, Firebase, Notification Channels,
 * and schedules background notification sync workers.
 */
@HiltAndroidApp
class PairLinkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize Firebase if not already initialized
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val app = FirebaseApp.initializeApp(this)
                Timber.i("Firebase initialized successfully: %s", app?.name)
            } else {
                Timber.i("Firebase already initialized: %s", FirebaseApp.getInstance().name)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error initializing FirebaseApp")
        }

        // Initialize Android notification channels
        NotificationChannelsHelper.createAllNotificationChannels(this)

        // Schedule background NotificationSyncWorker for offline / app-closed notifications
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<NotificationSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "PairLinkNotificationSyncWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            Timber.i("NotificationSyncWorker enqueued successfully")
        } catch (e: Exception) {
            Timber.w(e, "Error enqueuing NotificationSyncWorker: %s", e.message)
        }
    }
}
