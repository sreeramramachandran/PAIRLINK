package com.pairlink.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.pairlink.app.core.notifications.NotificationChannelsHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main Application class for PairLink.
 * Initializes Hilt Dependency Injection, Timber logging, Firebase, and Notification Channels.
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
    }
}
