package com.pairlink.app.data.repository

import com.pairlink.app.domain.model.PairRequest
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.PublicPartnerPreview
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Partner Pairing System.
 */
interface PairRepository {
    suspend fun searchPartner(partnerId: String): Result<PublicPartnerPreview>
    suspend fun sendRequest(receiverPartnerId: String): Result<PairRequest>
    suspend fun acceptRequest(requestId: String): Result<Unit>
    suspend fun rejectRequest(requestId: String): Result<Unit>
    suspend fun cancelRequest(requestId: String): Result<Unit>
    suspend fun unpair(): Result<Unit>
    suspend fun saveRelationshipDate(startDate: String): Result<Unit>
    suspend fun saveNextMeetingDate(meetingDate: String): Result<Unit>
    fun observeIncomingRequests(): Flow<List<PairRequest>>
    fun observeOutgoingRequests(): Flow<List<PairRequest>>
    fun observePartnerProfile(): Flow<PartnerProfile?>
}
