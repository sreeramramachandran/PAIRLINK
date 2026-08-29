package com.pairlink.app.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.pairlink.app.domain.model.DefaultStatusOptions
import com.pairlink.app.domain.model.StatusItem
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
 * Production implementation of StatusRepository using Cloud Firestore.
 * Supports custom status creation, deletion, fallback to "Available", and real-time partner sync.
 */
@Singleton
class FirebaseStatusRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StatusRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val currentUid: String
        get() = auth.currentUser?.uid ?: ""

    private val _currentStatus = MutableStateFlow("Available")
    override val currentStatus: StateFlow<String> = _currentStatus.asStateFlow()

    private val _lastUpdatedText = MutableStateFlow("Just now")
    override val lastUpdatedText: StateFlow<String> = _lastUpdatedText.asStateFlow()

    private val _availableStatuses = MutableStateFlow(DefaultStatusOptions)
    override val availableStatuses: StateFlow<List<StatusItem>> = _availableStatuses.asStateFlow()

    init {
        scope.launch {
            auth.addAuthStateListener { firebaseAuth ->
                val uid = firebaseAuth.currentUser?.uid
                if (uid != null) {
                    attachUserStatusListener(uid)
                }
            }
            if (auth.currentUser != null) {
                attachUserStatusListener(currentUid)
            }
        }
    }

    private fun attachUserStatusListener(uid: String) {
        firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening to user status doc: %s", uid)
                    return@addSnapshotListener
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        val status = snapshot.getString("status")
                        if (!status.isNullOrBlank()) {
                            _currentStatus.value = status
                            _lastUpdatedText.value = "Just now"
                        }

                        @Suppress("UNCHECKED_CAST")
                        val customList = snapshot.get("customStatuses") as? List<String> ?: emptyList()
                        val customItems = customList.map { statusText ->
                            createStatusItem(statusText, isCustom = true)
                        }

                        val combined = customItems + DefaultStatusOptions.filter { def ->
                            customItems.none { it.label.equals(def.label, ignoreCase = true) }
                        }
                        _availableStatuses.value = combined
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing user status document")
                }
            }
    }

    override suspend fun setStatus(status: String, message: String?): Result<Unit> = withContext(ioDispatcher) {
        val cleanStatus = status.trim()
        if (cleanStatus.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Status cannot be empty."))
        }
        if (cleanStatus.length > 60) {
            return@withContext Result.failure(IllegalArgumentException("Status must be 60 characters or less."))
        }

        _currentStatus.value = cleanStatus
        _lastUpdatedText.value = "Just now"

        val isDefault = DefaultStatusOptions.any { it.label.equals(cleanStatus, ignoreCase = true) }
        if (!isDefault) {
            val exists = _availableStatuses.value.any { it.label.equals(cleanStatus, ignoreCase = true) }
            if (!exists) {
                val newItem = createStatusItem(cleanStatus, isCustom = true)
                _availableStatuses.value = listOf(newItem) + _availableStatuses.value
            }
        }

        if (auth.currentUser != null && currentUid.isNotBlank()) {
            try {
                val updates = mutableMapOf<String, Any>(
                    "status" to cleanStatus,
                    "updatedAt" to System.currentTimeMillis()
                )
                if (!message.isNullOrBlank()) {
                    updates["statusMessage"] = message.trim()
                }
                if (!isDefault) {
                    updates["customStatuses"] = FieldValue.arrayUnion(cleanStatus)
                }
                firestore.collection("users").document(currentUid).set(updates, SetOptions.merge()).await()
                Timber.d("Status updated in Firestore: %s", cleanStatus)

                // Dispatch notification & FCM push to partner
                val userDoc = firestore.collection("users").document(currentUid).get().await()
                val partnerUid = userDoc.getString("partnerUid") ?: ""
                val username = userDoc.getString("username") ?: "Partner"
                if (partnerUid.isNotBlank()) {
                    val now = System.currentTimeMillis()
                    val notifId = "status_${now}_${(100..999).random()}"
                    val isReachedHome = cleanStatus.contains("Home", ignoreCase = true)
                    val title = if (isReachedHome) "🏠 $username reached home safely." else "$username is $cleanStatus"
                    val notifMessage = message?.ifBlank { "Activity status updated in your sanctuary." } ?: "Activity status updated in your sanctuary."
                    val typeStr = if (isReachedHome) "REACHED_HOME" else "STATUS_UPDATED"
                    val iconName = if (isReachedHome) "home" else "work"

                    firestore.collection("notifications").document(notifId).set(
                        mapOf(
                            "id" to notifId,
                            "title" to title,
                            "message" to notifMessage,
                            "type" to typeStr,
                            "senderUid" to currentUid,
                            "receiverUid" to partnerUid,
                            "iconName" to iconName,
                            "timestamp" to now,
                            "read" to false
                        ),
                        SetOptions.merge()
                    )

                    com.pairlink.app.core.util.FcmPushHelper.sendPushNotificationToPartner(
                        partnerUid = partnerUid,
                        title = title,
                        message = notifMessage,
                        type = typeStr,
                        senderUid = currentUid,
                        iconName = iconName
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Could not update status immediately in Firestore: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    override suspend fun setStatusSticker(stickerUrl: String, stickerId: String): Result<Unit> = withContext(ioDispatcher) {
        if (auth.currentUser != null && currentUid.isNotBlank()) {
            try {
                val cloudUrl = FirebaseStickerUploader.uploadStickerIfNeeded(context, stickerUrl)
                val updates = mapOf(
                    "currentStatusStickerUrl" to cloudUrl,
                    "currentStatusStickerId" to stickerId,
                    "updatedAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(currentUid).set(updates, SetOptions.merge()).await()
                Timber.d("Status Sticker updated in Firestore with cloud URL: %s", cloudUrl)

                val userDoc = firestore.collection("users").document(currentUid).get().await()
                val partnerUid = userDoc.getString("partnerUid") ?: ""
                val username = userDoc.getString("username") ?: "Partner"
                if (partnerUid.isNotBlank()) {
                    val now = System.currentTimeMillis()
                    val notifId = "status_${now}_${(100..999).random()}"
                    val title = "🌟 $username updated status sticker"
                    val notifMessage = "Shared a new sticker status in your sanctuary!"

                    firestore.collection("notifications").document(notifId).set(
                        mapOf(
                            "id" to notifId,
                            "title" to title,
                            "message" to notifMessage,
                            "type" to "STATUS_UPDATED",
                            "senderUid" to currentUid,
                            "receiverUid" to partnerUid,
                            "iconName" to "star",
                            "timestamp" to now,
                            "read" to false
                        ),
                        SetOptions.merge()
                    )

                    com.pairlink.app.core.util.FcmPushHelper.sendPushNotificationToPartner(
                        partnerUid = partnerUid,
                        title = title,
                        message = notifMessage,
                        type = "STATUS_UPDATED",
                        senderUid = currentUid,
                        iconName = "star"
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Error updating status sticker in Firestore: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    override suspend fun addCustomStatus(status: String, emoji: String?): Result<Unit> = withContext(ioDispatcher) {
        val cleanStatus = status.trim()
        if (cleanStatus.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Status cannot be empty."))
        }

        val newItem = createStatusItem(cleanStatus, isCustom = true)
        _availableStatuses.value = listOf(newItem) + _availableStatuses.value.filterNot { it.label.equals(cleanStatus, ignoreCase = true) }
        setStatus(cleanStatus, null)
    }

    override suspend fun deleteCustomStatus(statusId: String): Result<Unit> = withContext(ioDispatcher) {
        val target = _availableStatuses.value.find { it.id == statusId || it.label.equals(statusId, ignoreCase = true) }
            ?: return@withContext Result.success(Unit)

        _availableStatuses.value = _availableStatuses.value.filterNot { it.id == target.id }

        if (_currentStatus.value.equals(target.label, ignoreCase = true) || _currentStatus.value.contains(target.label, ignoreCase = true)) {
            _currentStatus.value = "Available"
        }

        if (auth.currentUser != null && currentUid.isNotBlank()) {
            try {
                val updates = mutableMapOf<String, Any>(
                    "customStatuses" to FieldValue.arrayRemove(target.label),
                    "updatedAt" to System.currentTimeMillis()
                )
                if (_currentStatus.value == "Available") {
                    updates["status"] = "Available"
                }
                firestore.collection("users").document(currentUid).set(updates, SetOptions.merge()).await()
                Timber.d("Custom status deleted from Firestore: %s", target.label)
            } catch (e: Exception) {
                Timber.w(e, "Error removing custom status from Firestore: %s", e.message)
            }
        }
        Result.success(Unit)
    }

    private fun createStatusItem(statusText: String, isCustom: Boolean): StatusItem {
        val emoji = if (statusText.length >= 2 && !statusText.first().isLetterOrDigit()) {
            statusText.take(2)
        } else {
            "✨"
        }
        val cleanLabel = statusText.removePrefix(emoji).trim().ifBlank { statusText }
        return StatusItem(
            id = "custom-${statusText.hashCode()}",
            label = cleanLabel,
            icon = Icons.Default.Star,
            emoji = emoji,
            isCustom = isCustom
        )
    }

    override fun observePartnerStatus(): Flow<Pair<String, String>> = callbackFlow {
        var userListener: ListenerRegistration? = null
        var partnerListener: ListenerRegistration? = null
        var authListener: FirebaseAuth.AuthStateListener? = null

        fun attachListenerForUid(uid: String) {
            userListener?.remove()
            partnerListener?.remove()

            if (uid.isBlank()) {
                trySend(Pair("Available", ""))
                return
            }

            try {
                userListener = firestore.collection("users").document(uid)
                    .addSnapshotListener { userSnapshot, userErr ->
                        if (userErr != null) {
                            Timber.e(userErr, "Error listening to current user for partner status")
                            return@addSnapshotListener
                        }
                        try {
                            val partnerUid = userSnapshot?.getString("partnerUid")

                            if (partnerUid.isNullOrBlank()) {
                                partnerListener?.remove()
                                partnerListener = null
                                trySend(Pair("Available", ""))
                            } else {
                                partnerListener?.remove()
                                partnerListener = firestore.collection("users").document(partnerUid)
                                    .addSnapshotListener { partnerSnapshot, partErr ->
                                        if (partErr != null) {
                                            Timber.e(partErr, "Error listening to partner status doc")
                                            return@addSnapshotListener
                                        }
                                        val status = partnerSnapshot?.getString("status") ?: "Available"
                                        val statusMessage = partnerSnapshot?.getString("statusMessage") ?: ""
                                        trySend(Pair(status, statusMessage))
                                    }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error extracting partner UID for status")
                            trySend(Pair("Available", ""))
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception setting up partner status listener")
                trySend(Pair("Available", ""))
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
