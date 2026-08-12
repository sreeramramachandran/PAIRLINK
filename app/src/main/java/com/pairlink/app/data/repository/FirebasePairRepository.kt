package com.pairlink.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.pairlink.app.domain.model.ConnectionStatus
import com.pairlink.app.domain.model.PairRequest
import com.pairlink.app.domain.model.PairRequestStatus
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.PublicPartnerPreview
import com.pairlink.app.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production PairRepository implementation handling Firestore pairing, search, and real-time requests.
 */
@Singleton
class FirebasePairRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PairRepository {

    private val currentUid: String
        get() = auth.currentUser?.uid ?: ""

    override suspend fun searchPartner(partnerId: String): Result<PublicPartnerPreview> = withContext(ioDispatcher) {
        val cleanPartnerId = partnerId.trim().uppercase()
        if (cleanPartnerId.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Partner ID cannot be blank."))
        }

        try {
            val query = firestore.collection("users")
                .whereEqualTo("partnerId", cleanPartnerId)
                .limit(1)
                .get()
                .await()

            if (query.isEmpty) {
                Timber.w("No user found with Partner ID: %s", cleanPartnerId)
                return@withContext Result.failure(IllegalArgumentException("No user found with Partner ID $cleanPartnerId. Please double-check."))
            }

            val doc = query.documents.first()
            val targetUid = doc.id

            if (targetUid == currentUid && currentUid.isNotBlank()) {
                Timber.w("User attempted to pair with own ID: %s", cleanPartnerId)
                return@withContext Result.failure(IllegalArgumentException("You cannot connect with your own Partner ID."))
            }

            val partnerUid = doc.getString("partnerUid")
            val connectionStatusStr = doc.getString("connectionStatus") ?: ConnectionStatus.NOT_PAIRED.name
            val isAlreadyPaired = !partnerUid.isNullOrBlank() || connectionStatusStr == ConnectionStatus.PAIRED.name

            if (isAlreadyPaired) {
                Timber.w("Target user %s is already connected to someone else", targetUid)
                return@withContext Result.failure(IllegalArgumentException("This user is already connected to a partner."))
            }

            val preview = PublicPartnerPreview(
                uid = targetUid,
                partnerId = doc.getString("partnerId") ?: cleanPartnerId,
                username = doc.getString("username") ?: "Partner",
                profileImageUrl = doc.getString("profileImageUrl") ?: "",
                connectionStatus = ConnectionStatus.NOT_PAIRED,
                isAlreadyPaired = false
            )

            Timber.i("Partner preview found: %s (%s)", preview.username, preview.partnerId)
            Result.success(preview)
        } catch (e: Exception) {
            Timber.e(e, "Error searching partner: %s", e.message)
            Result.failure(Exception(e.message ?: "Failed to search for partner.", e))
        }
    }

    override suspend fun sendRequest(receiverPartnerId: String): Result<PairRequest> = withContext(ioDispatcher) {
        val searchResult = searchPartner(receiverPartnerId)
        val receiver = searchResult.getOrElse { return@withContext Result.failure(it) }

        if (receiver.isAlreadyPaired) {
            return@withContext Result.failure(IllegalStateException("This user is already connected."))
        }

        try {
            val uid = currentUid
            if (uid.isBlank()) {
                return@withContext Result.failure(IllegalStateException("You must be logged in to connect."))
            }

            val currentUserDoc = firestore.collection("users").document(uid).get().await()
            val currentPartnerUid = currentUserDoc.getString("partnerUid")
            if (!currentPartnerUid.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException("You are already paired with a partner. Please unpair first."))
            }

            val senderPartnerId = currentUserDoc.getString("partnerId") ?: ""
            val senderUsername = currentUserDoc.getString("username") ?: "Partner"
            val senderProfileImage = currentUserDoc.getString("profileImageUrl") ?: ""

            // Check if active pending request already exists
            val existing = firestore.collection("pair_requests")
                .whereEqualTo("senderUid", uid)
                .whereEqualTo("receiverUid", receiver.uid)
                .whereEqualTo("status", PairRequestStatus.PENDING.name)
                .limit(1)
                .get()
                .await()

            if (!existing.isEmpty) {
                val existingDoc = existing.documents.first()
                val existingReq = existingDoc.toObject(PairRequest::class.java)
                if (existingReq != null) {
                    return@withContext Result.success(existingReq)
                }
            }

            val requestId = UUID.randomUUID().toString()
            val newRequest = PairRequest(
                requestId = requestId,
                senderUid = uid,
                receiverUid = receiver.uid,
                senderPartnerId = senderPartnerId,
                receiverPartnerId = receiver.partnerId,
                senderUsername = senderUsername,
                senderProfileImage = senderProfileImage,
                status = PairRequestStatus.PENDING.name,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val batch = firestore.batch()
            batch.set(firestore.collection("pair_requests").document(requestId), newRequest)
            batch.update(firestore.collection("users").document(uid), "connectionStatus", ConnectionStatus.REQUEST_SENT.name)
            batch.update(firestore.collection("users").document(receiver.uid), "connectionStatus", ConnectionStatus.REQUEST_RECEIVED.name)
            batch.commit().await()

            Timber.i("Pair request sent successfully: %s from %s to %s", requestId, senderUsername, receiver.username)
            Result.success(newRequest)
        } catch (e: Exception) {
            Timber.e(e, "Error sending pair request: %s", e.message)
            Result.failure(Exception(e.message ?: "Failed to send pair request.", e))
        }
    }

    override suspend fun acceptRequest(requestId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val requestRef = firestore.collection("pair_requests").document(requestId)
            val requestDoc = requestRef.get().await()
            val request = requestDoc.toObject(PairRequest::class.java)
                ?: return@withContext Result.failure(IllegalArgumentException("Pair request not found."))

            val senderUid = request.senderUid
            val receiverUid = request.receiverUid

            // Execute atomic transaction to pair both users simultaneously
            firestore.runTransaction { transaction ->
                val senderRef = firestore.collection("users").document(senderUid)
                val receiverRef = firestore.collection("users").document(receiverUid)

                val senderDoc = transaction.get(senderRef)
                val receiverDoc = transaction.get(receiverRef)

                val senderCurrentPartner = senderDoc.getString("partnerUid")
                val receiverCurrentPartner = receiverDoc.getString("partnerUid")

                if (!senderCurrentPartner.isNullOrBlank() && senderCurrentPartner != receiverUid) {
                    throw IllegalStateException("Sender is already paired with another account.")
                }
                if (!receiverCurrentPartner.isNullOrBlank() && receiverCurrentPartner != senderUid) {
                    throw IllegalStateException("Receiver is already paired with another account.")
                }

                val now = System.currentTimeMillis()
                val updatesSender = mapOf(
                    "partnerUid" to receiverUid,
                    "pairedAt" to now,
                    "connectionStatus" to ConnectionStatus.PAIRED.name,
                    "updatedAt" to now
                )
                val updatesReceiver = mapOf(
                    "partnerUid" to senderUid,
                    "pairedAt" to now,
                    "connectionStatus" to ConnectionStatus.PAIRED.name,
                    "updatedAt" to now
                )

                transaction.update(senderRef, updatesSender)
                transaction.update(receiverRef, updatesReceiver)
                transaction.update(requestRef, mapOf(
                    "status" to PairRequestStatus.ACCEPTED.name,
                    "updatedAt" to now
                ))
            }.await()

            Timber.i("Pair request accepted successfully for sender=%s and receiver=%s", senderUid, receiverUid)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error accepting pair request: %s", e.message)
            Result.failure(Exception(e.message ?: "Failed to accept pair request.", e))
        }
    }

    override suspend fun rejectRequest(requestId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val requestRef = firestore.collection("pair_requests").document(requestId)
            val requestDoc = requestRef.get().await()
            val request = requestDoc.toObject(PairRequest::class.java)

            if (request != null) {
                val batch = firestore.batch()
                batch.update(requestRef, "status", PairRequestStatus.REJECTED.name, "updatedAt", System.currentTimeMillis())
                batch.update(firestore.collection("users").document(request.receiverUid), "connectionStatus", ConnectionStatus.NOT_PAIRED.name)
                batch.update(firestore.collection("users").document(request.senderUid), "connectionStatus", ConnectionStatus.NOT_PAIRED.name)
                batch.commit().await()
            }
            Timber.i("Pair request rejected: %s", requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error rejecting pair request: %s", e.message)
            Result.failure(Exception(e.message ?: "Failed to reject pair request.", e))
        }
    }

    override suspend fun cancelRequest(requestId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val requestRef = firestore.collection("pair_requests").document(requestId)
            val requestDoc = requestRef.get().await()
            val request = requestDoc.toObject(PairRequest::class.java)

            if (request != null) {
                val batch = firestore.batch()
                batch.update(requestRef, "status", PairRequestStatus.CANCELLED.name, "updatedAt", System.currentTimeMillis())
                batch.update(firestore.collection("users").document(request.senderUid), "connectionStatus", ConnectionStatus.NOT_PAIRED.name)
                batch.update(firestore.collection("users").document(request.receiverUid), "connectionStatus", ConnectionStatus.NOT_PAIRED.name)
                batch.commit().await()
            }
            Timber.i("Pair request cancelled: %s", requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error cancelling pair request: %s", e.message)
            Result.failure(Exception(e.message ?: "Failed to cancel pair request.", e))
        }
    }

    override suspend fun unpair(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val uid = currentUid
            if (uid.isBlank()) return@withContext Result.failure(IllegalStateException("Not logged in."))

            val userDoc = firestore.collection("users").document(uid).get().await()
            val partnerUid = userDoc.getString("partnerUid")

            firestore.runTransaction { transaction ->
                val userRef = firestore.collection("users").document(uid)
                val now = System.currentTimeMillis()

                transaction.update(userRef, mapOf(
                    "partnerUid" to null,
                    "pairedAt" to null,
                    "connectionStatus" to ConnectionStatus.NOT_PAIRED.name,
                    "relationshipDate" to null,
                    "partnerNickname" to "",
                    "updatedAt" to now
                ))

                if (!partnerUid.isNullOrBlank()) {
                    val partnerRef = firestore.collection("users").document(partnerUid)
                    transaction.update(partnerRef, mapOf(
                        "partnerUid" to null,
                        "pairedAt" to null,
                        "connectionStatus" to ConnectionStatus.NOT_PAIRED.name,
                        "relationshipDate" to null,
                        "partnerNickname" to "",
                        "updatedAt" to now
                    ))
                }
            }.await()

            Timber.i("Unpairing completed for user: %s", uid)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error unpairing: %s", e.message)
            Result.failure(Exception(e.message ?: "Failed to unpair.", e))
        }
    }

    override suspend fun saveRelationshipDate(startDate: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val uid = currentUid
            if (uid.isBlank()) return@withContext Result.failure(IllegalStateException("Not logged in."))

            val userDoc = firestore.collection("users").document(uid).get().await()
            val partnerUid = userDoc.getString("partnerUid")

            val now = System.currentTimeMillis()
            val batch = firestore.batch()
            batch.update(firestore.collection("users").document(uid), "relationshipDate", startDate, "updatedAt", now)
            if (!partnerUid.isNullOrBlank()) {
                batch.update(firestore.collection("users").document(partnerUid), "relationshipDate", startDate, "updatedAt", now)
            }
            batch.commit().await()

            Timber.i("Relationship date saved: %s", startDate)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving relationship date: %s", e.message)
            Result.failure(Exception(e.message ?: "Failed to save relationship date.", e))
        }
    }

    override fun observeIncomingRequests(): Flow<List<PairRequest>> = callbackFlow {
        var listener: ListenerRegistration? = null
        var authListener: FirebaseAuth.AuthStateListener? = null

        fun attachListenerForUid(uid: String) {
            listener?.remove()
            if (uid.isBlank()) {
                trySend(emptyList())
                return
            }
            listener = firestore.collection("pair_requests")
                .whereEqualTo("receiverUid", uid)
                .whereEqualTo("status", PairRequestStatus.PENDING.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.e(error, "Error observing incoming requests")
                        return@addSnapshotListener
                    }
                    val requests = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(PairRequest::class.java)
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing incoming pair request document")
                            null
                        }
                    } ?: emptyList()
                    trySend(requests)
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
            listener?.remove()
        }
    }.flowOn(ioDispatcher)

    override fun observeOutgoingRequests(): Flow<List<PairRequest>> = callbackFlow {
        var listener: ListenerRegistration? = null
        var authListener: FirebaseAuth.AuthStateListener? = null

        fun attachListenerForUid(uid: String) {
            listener?.remove()
            if (uid.isBlank()) {
                trySend(emptyList())
                return
            }
            listener = firestore.collection("pair_requests")
                .whereEqualTo("senderUid", uid)
                .whereEqualTo("status", PairRequestStatus.PENDING.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.e(error, "Error observing outgoing requests")
                        return@addSnapshotListener
                    }
                    val requests = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(PairRequest::class.java)
                        } catch (e: Exception) {
                            Timber.e(e, "Error parsing outgoing pair request document")
                            null
                        }
                    } ?: emptyList()
                    trySend(requests)
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
            listener?.remove()
        }
    }.flowOn(ioDispatcher)

    override fun observePartnerProfile(): Flow<PartnerProfile?> = callbackFlow {
        var userListener: ListenerRegistration? = null
        var partnerListener: ListenerRegistration? = null
        var authListener: FirebaseAuth.AuthStateListener? = null

        fun attachUserListener(uid: String) {
            userListener?.remove()
            partnerListener?.remove()
            if (uid.isBlank()) {
                trySend(null)
                return
            }
            val userRef = firestore.collection("users").document(uid)
            userListener = userRef.addSnapshotListener { userSnapshot, userErr ->
                if (userErr != null) {
                    Timber.e(userErr, "Error listening to current user for partner info")
                    return@addSnapshotListener
                }
                try {
                    val partnerUid = userSnapshot?.getString("partnerUid")
                    val customNickname = userSnapshot?.getString("partnerNickname") ?: ""

                    if (partnerUid.isNullOrBlank()) {
                        partnerListener?.remove()
                        partnerListener = null
                        trySend(PartnerProfile())
                    } else {
                        partnerListener?.remove()
                        val partnerRef = firestore.collection("users").document(partnerUid)
                        partnerListener = partnerRef.addSnapshotListener { partnerSnapshot, partErr ->
                            if (partErr != null) {
                                Timber.e(partErr, "Error listening to partner user doc")
                                return@addSnapshotListener
                            }
                            try {
                                val partnerUser = partnerSnapshot?.toObject(UserProfile::class.java)
                                if (partnerUser != null) {
                                    trySend(PartnerProfile.fromUserProfile(partnerUser, customNickname))
                                } else {
                                    trySend(PartnerProfile())
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error deserializing partner UserProfile")
                                trySend(PartnerProfile())
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error processing current user partner info")
                    trySend(PartnerProfile())
                }
            }
        }

        authListener = FirebaseAuth.AuthStateListener { fbAuth ->
            val uid = fbAuth.currentUser?.uid ?: ""
            attachUserListener(uid)
        }
        auth.addAuthStateListener(authListener)
        attachUserListener(auth.currentUser?.uid ?: "")

        awaitClose {
            authListener?.let { auth.removeAuthStateListener(it) }
            userListener?.remove()
            partnerListener?.remove()
        }
    }.flowOn(ioDispatcher)
}
