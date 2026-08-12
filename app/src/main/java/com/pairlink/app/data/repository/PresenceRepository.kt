package com.pairlink.app.data.repository

import com.pairlink.app.domain.model.PresenceDocument
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for PairLink's real-time Presence System.
 */
interface PresenceRepository {
    suspend fun startPresence(): Result<Unit>
    suspend fun stopPresence(): Result<Unit>
    fun observePresence(): Flow<PresenceDocument?>
    suspend fun syncFcmToken(): Result<String>
    suspend fun sendPresenceNotification(): Result<Unit>
}
