package com.pairlink.app.core.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

/**
 * Utility for dispatching high-priority FCM Push Notification payloads directly
 * to a partner device to wake up the screen, vibrate, and display notification
 * banners even when the partner app is closed, in background, or phone screen is off.
 */
object FcmPushHelper {

    suspend fun sendPushNotificationToPartner(
        partnerUid: String,
        title: String,
        message: String,
        type: String,
        senderUid: String,
        iconName: String = "favorite"
    ) = withContext(Dispatchers.IO) {
        if (partnerUid.isBlank()) return@withContext
        try {
            // 1. Retrieve partner's FCM Token from Firestore users collection
            val partnerDoc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(partnerUid)
                .get()
                .await()

            val fcmToken = partnerDoc.getString("fcmToken") ?: ""
            if (fcmToken.isBlank()) {
                Timber.w("Partner FCM token is blank for uid: %s", partnerUid)
                return@withContext
            }

            // 2. Build high-priority payload for FCM HTTP transmission
            val url = URL("https://fcm.googleapis.com/fcm/send")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            // Legacy / FCM server fallback key header
            conn.setRequestProperty("Authorization", "key=AAAA_PAIRLINK_LEGACY_SERVER_KEY")
            conn.doOutput = true
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val payload = JSONObject().apply {
                put("to", fcmToken)
                put("priority", "high")
                put("content_available", true)
                put("direct_boot_ok", true)

                // Standard notification block (OS handles banner & vibration when app is closed)
                val notifObj = JSONObject().apply {
                    put("title", title)
                    put("body", message)
                    put("sound", "default")
                    put("priority", "high")
                    put("channel_id", "presence_channel")
                }
                put("notification", notifObj)

                // Data block (PairLinkFirebaseMessagingService handles onMessageReceived)
                val dataObj = JSONObject().apply {
                    put("title", title)
                    put("message", message)
                    put("type", type)
                    put("senderUid", senderUid)
                    put("iconName", iconName)
                }
                put("data", dataObj)
            }

            conn.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = conn.responseCode
            Timber.i("FCM Push sent to token %s with status code %d", fcmToken.take(10), responseCode)
        } catch (e: Exception) {
            Timber.w(e, "Could not send FCM push notification to partner: %s", e.message)
        }
    }
}
