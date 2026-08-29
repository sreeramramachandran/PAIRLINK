package com.pairlink.app.data.repository

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.pairlink.app.domain.model.DefaultMoodOptions
import com.pairlink.app.domain.model.MoodItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of MoodRepository using Cloud Firestore.
 * Supports dynamic custom mood creation, real-time partner sync, and deletion with fallback to "Happy".
 */
@Singleton
class FirebaseMoodRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MoodRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val currentUid: String
        get() = auth.currentUser?.uid ?: ""

    private val _currentMood = MutableStateFlow("Happy")
    override val currentMood: StateFlow<String> = _currentMood.asStateFlow()

    private val _availableMoods = MutableStateFlow(DefaultMoodOptions)
    override val availableMoods: StateFlow<List<MoodItem>> = _availableMoods.asStateFlow()

    init {
        scope.launch {
            auth.addAuthStateListener { firebaseAuth ->
                val uid = firebaseAuth.currentUser?.uid
                if (uid != null) {
                    attachUserMoodListener(uid)
                }
            }
            if (auth.currentUser != null) {
                attachUserMoodListener(currentUid)
            }
        }
    }

    private fun attachUserMoodListener(uid: String) {
        firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening to user mood doc: %s", uid)
                    return@addSnapshotListener
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        val mood = snapshot.getString("mood")
                        if (!mood.isNullOrBlank()) {
                            _currentMood.value = mood
                        }

                        @Suppress("UNCHECKED_CAST")
                        val customList = snapshot.get("customMoods") as? List<String> ?: emptyList()
                        val customItems = customList.map { moodText ->
                            createMoodItem(moodText, isCustom = true)
                        }

                        // Combine custom items with default options avoiding duplicates
                        val combined = customItems + DefaultMoodOptions.filter { def ->
                            customItems.none { it.label.equals(def.label, ignoreCase = true) }
                        }
                        _availableMoods.value = combined
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing user mood document")
                }
            }
    }

    override suspend fun setMood(mood: String): Result<Unit> = withContext(ioDispatcher) {
        val cleanMood = mood.trim()
        if (cleanMood.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Mood cannot be empty."))
        }
        if (cleanMood.length > 80) {
            return@withContext Result.failure(IllegalArgumentException("Mood must be 80 characters or less."))
        }

        _currentMood.value = cleanMood
        val isDefault = DefaultMoodOptions.any { it.label.equals(cleanMood, ignoreCase = true) }

        if (!isDefault) {
            val existsInAvailable = _availableMoods.value.any { it.label.equals(cleanMood, ignoreCase = true) }
            if (!existsInAvailable) {
                val newItem = createMoodItem(cleanMood, isCustom = true)
                _availableMoods.value = listOf(newItem) + _availableMoods.value
            }
        }

        if (auth.currentUser != null && currentUid.isNotBlank()) {
            try {
                val updates = mutableMapOf<String, Any>(
                    "mood" to cleanMood,
                    "updatedAt" to System.currentTimeMillis()
                )
                if (!isDefault) {
                    updates["customMoods"] = FieldValue.arrayUnion(cleanMood)
                }
                firestore.collection("users").document(currentUid).set(updates, SetOptions.merge()).await()
                Timber.d("Mood updated in Firestore: %s", cleanMood)

                // Dispatch notification & FCM push to partner
                val userDoc = firestore.collection("users").document(currentUid).get().await()
                val partnerUid = userDoc.getString("partnerUid") ?: ""
                val username = userDoc.getString("username") ?: "Partner"
                if (partnerUid.isNotBlank()) {
                    val now = System.currentTimeMillis()
                    val notifId = "mood_${now}_${(100..999).random()}"
                    val title = "❤️ $username updated mood"
                    val message = "Mood is now '$cleanMood'"

                    firestore.collection("notifications").document(notifId).set(
                        mapOf(
                            "id" to notifId,
                            "title" to title,
                            "message" to message,
                            "type" to "MOOD_UPDATED",
                            "senderUid" to currentUid,
                            "receiverUid" to partnerUid,
                            "iconName" to "mood",
                            "timestamp" to now,
                            "read" to false
                        ),
                        SetOptions.merge()
                    )

                    com.pairlink.app.core.util.FcmPushHelper.sendPushNotificationToPartner(
                        partnerUid = partnerUid,
                        title = title,
                        message = message,
                        type = "MOOD_UPDATED",
                        senderUid = currentUid,
                        iconName = "mood"
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Could not update mood immediately in Firestore: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    override suspend fun setStickerMood(stickerUrl: String, stickerId: String): Result<Unit> = withContext(ioDispatcher) {
        if (auth.currentUser != null && currentUid.isNotBlank()) {
            try {
                val cloudUrl = FirebaseStickerUploader.uploadStickerIfNeeded(context, stickerUrl)
                val updates = mapOf(
                    "currentMoodStickerUrl" to cloudUrl,
                    "currentMoodStickerId" to stickerId,
                    "updatedAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(currentUid).set(updates, SetOptions.merge()).await()
                Timber.d("Sticker Mood updated in Firestore with cloud URL: %s", cloudUrl)

                val userDoc = firestore.collection("users").document(currentUid).get().await()
                val partnerUid = userDoc.getString("partnerUid") ?: ""
                val username = userDoc.getString("username") ?: "Partner"
                if (partnerUid.isNotBlank()) {
                    val now = System.currentTimeMillis()
                    val notifId = "mood_${now}_${(100..999).random()}"
                    val title = "❤️ $username updated mood sticker"
                    val message = "Shared a new sticker mood in your sanctuary! ✨"

                    firestore.collection("notifications").document(notifId).set(
                        mapOf(
                            "id" to notifId,
                            "title" to title,
                            "message" to message,
                            "type" to "MOOD_UPDATED",
                            "senderUid" to currentUid,
                            "receiverUid" to partnerUid,
                            "iconName" to "mood",
                            "timestamp" to now,
                            "read" to false
                        ),
                        SetOptions.merge()
                    )

                    com.pairlink.app.core.util.FcmPushHelper.sendPushNotificationToPartner(
                        partnerUid = partnerUid,
                        title = title,
                        message = message,
                        type = "MOOD_UPDATED",
                        senderUid = currentUid,
                        iconName = "mood"
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Error updating sticker mood in Firestore: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    override suspend fun deleteCustomMood(moodId: String): Result<Unit> = withContext(ioDispatcher) {
        val target = _availableMoods.value.find { it.id == moodId || it.label.equals(moodId, ignoreCase = true) }
            ?: return@withContext Result.success(Unit)

        // Remove from available list
        _availableMoods.value = _availableMoods.value.filterNot { it.id == target.id }

        // If the deleted mood was currently active, revert to default "Happy"
        if (_currentMood.value.equals(target.label, ignoreCase = true) || _currentMood.value.contains(target.label, ignoreCase = true)) {
            _currentMood.value = "Happy"
        }

        if (auth.currentUser != null && currentUid.isNotBlank()) {
            try {
                val updates = mutableMapOf<String, Any>(
                    "customMoods" to FieldValue.arrayRemove(target.label),
                    "updatedAt" to System.currentTimeMillis()
                )
                if (_currentMood.value == "Happy") {
                    updates["mood"] = "Happy"
                }
                firestore.collection("users").document(currentUid).set(updates, SetOptions.merge()).await()
                Timber.d("Custom mood deleted from Firestore: %s", target.label)
            } catch (e: Exception) {
                Timber.w(e, "Error removing custom mood from Firestore: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    private fun createMoodItem(moodText: String, isCustom: Boolean): MoodItem {
        val emoji = if (moodText.length >= 2 && !moodText.first().isLetterOrDigit()) {
            moodText.take(2)
        } else {
            "✨"
        }
        val cleanLabel = moodText.removePrefix(emoji).trim().ifBlank { moodText }
        return MoodItem(
            id = "custom-${moodText.hashCode()}",
            label = cleanLabel,
            emoji = emoji,
            icon = Icons.Default.Mood,
            floatDelayMs = 250,
            isCustom = isCustom
        )
    }

    override fun observePartnerMood(): Flow<String> = callbackFlow {
        var userListener: ListenerRegistration? = null
        var partnerListener: ListenerRegistration? = null
        var authListener: FirebaseAuth.AuthStateListener? = null

        fun attachListenerForUid(uid: String) {
            userListener?.remove()
            partnerListener?.remove()

            if (uid.isBlank()) {
                trySend("Happy")
                return
            }

            try {
                userListener = firestore.collection("users").document(uid)
                    .addSnapshotListener { userSnapshot, userErr ->
                        if (userErr != null) {
                            Timber.e(userErr, "Error listening to user doc for partner mood")
                            return@addSnapshotListener
                        }
                        try {
                            val partnerUid = userSnapshot?.getString("partnerUid")
                            if (partnerUid.isNullOrBlank()) {
                                partnerListener?.remove()
                                partnerListener = null
                                trySend("Happy")
                            } else {
                                partnerListener?.remove()
                                partnerListener = firestore.collection("users").document(partnerUid)
                                    .addSnapshotListener { partnerSnapshot, partErr ->
                                        if (partErr != null) {
                                            Timber.e(partErr, "Error listening to partner mood doc")
                                            return@addSnapshotListener
                                        }
                                        val mood = partnerSnapshot?.getString("mood") ?: "Happy"
                                        trySend(mood)
                                    }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error extracting partner UID for mood")
                            trySend("Happy")
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception setting up partner mood listener")
                trySend("Happy")
            }
        }

        authListener = FirebaseAuth.AuthStateListener { fbAuth ->
            val uid = fbAuth.currentUser?.uid ?: ""
            attachListenerForUid(uid)
        }
        auth.addAuthStateListener(authListener)
        attachListenerForUid(auth.currentUser?.uid ?: "")

        awaitClose {
            authListener?.let { auth.removeAuthStateListener(it) }
            userListener?.remove()
            partnerListener?.remove()
        }
    }.flowOn(ioDispatcher)
}
