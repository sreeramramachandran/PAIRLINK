package com.pairlink.app.domain.model

import com.google.firebase.firestore.DocumentId

/**
 * Represents the live presence document stored in Firestore `presence/{pairId}`.
 * All numeric fields use 64-bit Long to guarantee safe Firestore deserialization.
 */
data class PresenceDocument(
    @DocumentId
    val pairId: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val isHolding: Boolean = false,
    val startedAt: Long = 0L,
    val lastPulseTimestamp: Long = 0L,
    val pulseSenderUid: String = "",
    val pulseCount: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Visual Presence Ring status colors around the heart button.
 */
enum class PresenceRingState {
    OFFLINE,    // Gray
    ONLINE,     // Green
    SLEEPING,   // Blue
    BUSY,       // Yellow
    DRIVING,    // Orange
    HOLDING     // Red / Vibrant Pink Pulsing
}
