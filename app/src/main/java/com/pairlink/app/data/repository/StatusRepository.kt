package com.pairlink.app.data.repository

import com.pairlink.app.domain.model.StatusItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for Activity Status Synchronization and custom status CRUD.
 */
interface StatusRepository {
    val currentStatus: StateFlow<String>
    val lastUpdatedText: StateFlow<String>
    val availableStatuses: StateFlow<List<StatusItem>>
    suspend fun setStatus(status: String, message: String? = null): Result<Unit>
    suspend fun addCustomStatus(status: String, emoji: String? = null): Result<Unit>
    suspend fun deleteCustomStatus(statusId: String): Result<Unit>
    fun observePartnerStatus(): Flow<Pair<String, String>>
}
