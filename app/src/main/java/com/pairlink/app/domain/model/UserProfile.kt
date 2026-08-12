package com.pairlink.app.domain.model

import com.google.firebase.firestore.DocumentId

/**
 * Represents the user's profile and sanctuary state stored in Firestore `users` collection.
 */
data class UserProfile(
    @DocumentId
    val uid: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val partnerId: String = "",
    val partnerUid: String? = null,
    val pairedAt: Long? = null,
    val connectionStatus: String = ConnectionStatus.NOT_PAIRED.name,
    val dateOfBirth: String = "",
    val profileImageUrl: String = "",
    val mood: String = "Happy",
    val status: String = "Available",
    val customMoods: List<String> = emptyList(),
    val customStatuses: List<String> = emptyList(),
    val relationshipDate: String? = null,
    val partnerNickname: String = "",
    val statusMessage: String = "Connected to our private sanctuary. ✨",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val fcmToken: String? = null
) {
    // UI convenience properties
    val id: String get() = uid
    val phone: String get() = phoneNumber
    val dob: String get() = dateOfBirth
    val avatarUrl: String get() = profileImageUrl
    val currentMood: String get() = mood
    val currentStatus: String get() = status
    val relationshipStartDate: String get() = relationshipDate ?: ""
    val isPaired: Boolean get() = !partnerUid.isNullOrBlank() || connectionStatus == ConnectionStatus.PAIRED.name

    val currentConnectionStatus: ConnectionStatus get() = try {
        ConnectionStatus.valueOf(connectionStatus.uppercase())
    } catch (_: Exception) {
        if (isPaired) ConnectionStatus.PAIRED else ConnectionStatus.NOT_PAIRED
    }
}
