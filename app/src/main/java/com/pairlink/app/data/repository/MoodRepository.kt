package com.pairlink.app.data.repository

import com.pairlink.app.domain.model.MoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for Mood Synchronization and custom mood CRUD.
 */
interface MoodRepository {
    val currentMood: StateFlow<String>
    val availableMoods: StateFlow<List<MoodItem>>
    suspend fun setMood(mood: String): Result<Unit>
    suspend fun setStickerMood(stickerUrl: String, stickerId: String): Result<Unit>
    suspend fun deleteCustomMood(moodId: String): Result<Unit>
    fun observePartnerMood(): Flow<String>
}
