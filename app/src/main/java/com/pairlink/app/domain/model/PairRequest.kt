package com.pairlink.app.domain.model

import com.google.firebase.firestore.DocumentId

/**
 * Represents a pair request document stored in Firestore `pair_requests/{requestId}`.
 */
data class PairRequest(
    @DocumentId
    val requestId: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val senderPartnerId: String = "",
    val receiverPartnerId: String = "",
    val senderUsername: String = "",
    val senderProfileImage: String = "",
    val status: String = PairRequestStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val requestStatus: PairRequestStatus get() = try {
        PairRequestStatus.valueOf(status.uppercase())
    } catch (_: Exception) {
        PairRequestStatus.PENDING
    }
}

/**
 * Sanitized public partner preview returned when searching by Partner ID.
 * Protects sensitive personal data (phone, dob, password).
 */
data class PublicPartnerPreview(
    val uid: String = "",
    val partnerId: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val connectionStatus: ConnectionStatus = ConnectionStatus.NOT_PAIRED,
    val isAlreadyPaired: Boolean = false
)
