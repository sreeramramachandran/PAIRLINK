package com.pairlink.app.domain.model

/**
 * Represents the paired partner's profile and live status.
 */
data class PartnerProfile(
    val id: String = "",
    val name: String = "",
    val nickname: String = "",
    val avatarUrl: String = "",
    val partnerId: String = "",
    val dob: String = "",
    val isOnline: Boolean = false,
    val currentMood: String = "Happy",
    val currentStatus: String = "Available",
    val statusMessage: String = "",
    val lastActiveTime: String = "Just now"
) {
    companion object {
        fun fromUserProfile(user: UserProfile, customNickname: String = ""): PartnerProfile {
            return PartnerProfile(
                id = user.uid,
                name = user.username,
                nickname = customNickname.ifBlank { user.partnerNickname.ifBlank { user.username } },
                avatarUrl = user.avatarUrl,
                partnerId = user.partnerId,
                dob = user.dateOfBirth,
                isOnline = true,
                currentMood = user.mood.ifBlank { "Happy" },
                currentStatus = user.status.ifBlank { "Available" },
                statusMessage = user.statusMessage,
                lastActiveTime = "Just now"
            )
        }
    }
}
