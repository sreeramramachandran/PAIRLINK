package com.pairlink.app.data.repository

import android.net.Uri
import com.pairlink.app.domain.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserProfile>
    fun isUserLoggedIn(): Boolean
    suspend fun login(phone: String, pin: String): Result<UserProfile>
    suspend fun register(
        username: String,
        phone: String,
        dob: String,
        pin: String,
        imageUri: Uri? = null
    ): Result<UserProfile>
    suspend fun updateProfile(
        username: String,
        nickname: String,
        dob: String,
        avatarUrl: String?
    ): Result<Unit>
    suspend fun updateRelationshipDate(startDate: String): Result<Unit>
    suspend fun unpair(): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun fetchCurrentUserProfile(): Result<UserProfile?>
}
