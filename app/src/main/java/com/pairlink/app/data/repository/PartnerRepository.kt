package com.pairlink.app.data.repository

import com.pairlink.app.domain.model.PartnerProfile
import kotlinx.coroutines.flow.StateFlow

interface PartnerRepository {
    val partnerProfile: StateFlow<PartnerProfile>
    val heartPulseCount: StateFlow<Int>
    suspend fun sendHeartPulse(): Result<Int>
    suspend fun updatePartnerDetails(name: String, nickname: String, dob: String, avatarUrl: String?): Result<Unit>
    suspend fun updatePartnerDob(dob: String): Result<Unit>
    suspend fun connectPartner(partnerId: String): Result<PartnerProfile>
    suspend fun acceptPairRequest(): Result<Unit>
    suspend fun rejectPairRequest(): Result<Unit>
}
