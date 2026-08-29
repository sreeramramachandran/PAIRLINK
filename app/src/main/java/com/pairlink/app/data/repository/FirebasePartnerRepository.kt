package com.pairlink.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production Partner Repository observing live Firestore updates for the paired partner.
 * Seamlessly manages partner document snapshot listeners across login, logout, and pairing.
 */
@Singleton
class FirebasePartnerRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val pairRepository: PairRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PartnerRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var partnerDocListener: ListenerRegistration? = null
    private var currentObservedPartnerUid: String? = null

    private val _partnerProfile = MutableStateFlow(PartnerProfile())
    override val partnerProfile: StateFlow<PartnerProfile> = _partnerProfile.asStateFlow()

    private val _heartPulseCount = MutableStateFlow(0)
    override val heartPulseCount: StateFlow<Int> = _heartPulseCount.asStateFlow()

    init {
        scope.launch {
            authRepository.currentUser.collect { user ->
                val partnerUid = user.partnerUid
                val partnerNickname = user.partnerNickname

                if (partnerUid.isNullOrBlank()) {
                    detachPartnerListener()
                    _partnerProfile.value = PartnerProfile()
                } else {
                    if (currentObservedPartnerUid != partnerUid) {
                        attachPartnerListener(partnerUid, partnerNickname)
                    } else {
                        // Partner nickname may have changed; update current profile with new nickname
                        val current = _partnerProfile.value
                        if (current.id.isNotBlank()) {
                            _partnerProfile.value = current.copy(
                                nickname = partnerNickname.ifBlank { current.name }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun attachPartnerListener(partnerUid: String, partnerNickname: String) {
        detachPartnerListener()
        currentObservedPartnerUid = partnerUid
        Timber.d("Attaching partner listener for partnerUid: %s", partnerUid)

        partnerDocListener = firestore.collection("users").document(partnerUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening to partner user doc: %s", partnerUid)
                    return@addSnapshotListener
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        val partnerUser = snapshot.toObject(UserProfile::class.java)
                        if (partnerUser != null) {
                            val profile = PartnerProfile.fromUserProfile(partnerUser, partnerNickname)
                            _partnerProfile.value = profile
                            Timber.d("Live partner profile updated: name=%s, avatarUrl=%s, mood=%s, status=%s",
                                profile.name, profile.avatarUrl.take(30), profile.currentMood, profile.currentStatus)
                        }
                    } else {
                        _partnerProfile.value = PartnerProfile()
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing partner profile snapshot for partnerUid: %s", partnerUid)
                }
            }
    }

    private fun detachPartnerListener() {
        partnerDocListener?.remove()
        partnerDocListener = null
        currentObservedPartnerUid = null
    }

    override suspend fun sendHeartPulse(): Result<Int> = withContext(ioDispatcher) {
        val currentUid = auth.currentUser?.uid ?: authRepository.currentUser.value.uid
        val partner = _partnerProfile.value
        _heartPulseCount.value += 1

        if (currentUid.isNotBlank() && partner.id.isNotBlank()) {
            val pairId = if (currentUid < partner.id) "${currentUid}_${partner.id}" else "${partner.id}_${currentUid}"
            try {
                val now = System.currentTimeMillis()
                firestore.collection("presence").document(pairId).set(
                    mapOf(
                        "pairId" to pairId,
                        "senderUid" to currentUid,
                        "receiverUid" to partner.id,
                        "lastPulseTimestamp" to now,
                        "pulseSenderUid" to currentUid,
                        "pulseCount" to FieldValue.increment(1),
                        "updatedAt" to now
                    ),
                    SetOptions.merge()
                ).await()

                // Trigger background / lockscreen push notification for partner
                val notifId = "pulse_${now}_${(100..999).random()}"
                val senderName = authRepository.currentUser.value.username.ifBlank { "Partner" }
                val title = "❤️ $senderName sent you a Love Beat!"
                val notifMessage = "A warm burst of affection was sent to your sanctuary. 💓"

                firestore.collection("notifications").document(notifId).set(
                    mapOf(
                        "id" to notifId,
                        "title" to title,
                        "message" to notifMessage,
                        "type" to "PRESENCE",
                        "senderUid" to currentUid,
                        "receiverUid" to partner.id,
                        "iconName" to "favorite",
                        "timestamp" to now,
                        "read" to false
                    ),
                    SetOptions.merge()
                )

                // High priority FCM push transmission to wake display screen & vibrate partner device
                com.pairlink.app.core.util.FcmPushHelper.sendPushNotificationToPartner(
                    partnerUid = partner.id,
                    title = title,
                    message = notifMessage,
                    type = "PRESENCE",
                    senderUid = currentUid,
                    iconName = "favorite"
                )
            } catch (e: Exception) {
                Timber.w(e, "Could not sync heart pulse to Firestore")
            }
        }
        Result.success(_heartPulseCount.value)
    }

    override suspend fun updatePartnerDetails(
        name: String,
        nickname: String,
        dob: String,
        avatarUrl: String?
    ): Result<Unit> = withContext(ioDispatcher) {
        val current = _partnerProfile.value
        val updated = current.copy(
            name = name,
            nickname = nickname,
            dob = dob,
            avatarUrl = avatarUrl ?: current.avatarUrl
        )
        _partnerProfile.value = updated

        if (current.id.isNotBlank()) {
            try {
                firestore.collection("users").document(current.id).set(
                    mapOf(
                        "username" to name,
                        "dateOfBirth" to dob,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                Timber.w(e, "Error updating partner details in Firestore")
            }
        }
        Result.success(Unit)
    }

    override suspend fun updatePartnerDob(dob: String): Result<Unit> = withContext(ioDispatcher) {
        val current = _partnerProfile.value
        _partnerProfile.value = current.copy(dob = dob)

        if (current.id.isNotBlank()) {
            try {
                firestore.collection("users").document(current.id).set(
                    mapOf(
                        "dateOfBirth" to dob,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                Timber.w(e, "Error updating partner DOB in Firestore")
            }
        }
        Result.success(Unit)
    }

    override suspend fun connectPartner(partnerId: String): Result<PartnerProfile> {
        val search = pairRepository.searchPartner(partnerId)
        val preview = search.getOrElse { return Result.failure(it) }
        val partner = PartnerProfile(
            id = preview.uid,
            name = preview.username,
            nickname = preview.username,
            avatarUrl = preview.profileImageUrl,
            partnerId = preview.partnerId
        )
        pairRepository.sendRequest(partnerId)
        _partnerProfile.value = partner
        return Result.success(partner)
    }

    override suspend fun acceptPairRequest(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun rejectPairRequest(): Result<Unit> {
        return Result.success(Unit)
    }
}
