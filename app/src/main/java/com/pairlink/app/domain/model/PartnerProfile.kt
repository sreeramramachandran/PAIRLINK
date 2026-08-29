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
    val nextMeetingDate: String = "",
    val latitude: Double = 11.0728,
    val longitude: Double = 76.0740,
    val locationName: String = "Malappuram, India",
    val weatherTemp: String = "28°",
    val weatherCondition: String = "Partly Cloudy",
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
                nextMeetingDate = user.nextMeetingDate ?: "",
                latitude = user.latitude,
                longitude = user.longitude,
                locationName = user.locationName.ifBlank { "Malappuram, India" },
                weatherTemp = user.weatherTemp.ifBlank { "28°" },
                weatherCondition = user.weatherCondition.ifBlank { "Partly Cloudy" },
                lastActiveTime = "Just now"
            )
        }
    }
}
