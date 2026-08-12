package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.data.repository.AuthRepository
import com.pairlink.app.data.repository.PairRepository
import com.pairlink.app.domain.model.PairRequest
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.PublicPartnerPreview
import com.pairlink.app.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PairUiState {
    data object Idle : PairUiState
    data object Searching : PairUiState
    data class FoundPartner(val partner: PublicPartnerPreview) : PairUiState
    data object Sending : PairUiState
    data class WaitingForResponse(val request: PairRequest) : PairUiState
    data class IncomingRequest(val request: PairRequest) : PairUiState
    data object Connected : PairUiState
    data class Error(val message: String) : PairUiState
    data object Loading : PairUiState
}

@HiltViewModel
class PairViewModel @Inject constructor(
    private val pairRepository: PairRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UserProfile> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val partnerProfile: StateFlow<PartnerProfile?> = pairRepository.observePartnerProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val incomingRequests: StateFlow<List<PairRequest>> = pairRepository.observeIncomingRequests()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<PairUiState>(PairUiState.Idle)
    val uiState: StateFlow<PairUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            incomingRequests.collect { requests ->
                if (requests.isNotEmpty() && _uiState.value !is PairUiState.Connected && _uiState.value !is PairUiState.Loading && !currentUser.value.isPaired) {
                    _uiState.value = PairUiState.IncomingRequest(requests.first())
                }
            }
        }
    }

    fun searchPartner(partnerId: String) {
        val cleanId = partnerId.trim().uppercase()
        if (cleanId.isBlank()) {
            _uiState.value = PairUiState.Error("Please enter a valid Partner ID.")
            return
        }

        viewModelScope.launch {
            _uiState.value = PairUiState.Searching
            val result = pairRepository.searchPartner(cleanId)
            result.onSuccess { preview ->
                _uiState.value = PairUiState.FoundPartner(preview)
            }.onFailure { error ->
                _uiState.value = PairUiState.Error(error.message ?: "Partner not found. Check the ID.")
            }
        }
    }

    fun sendPairRequest(receiverPartnerId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = PairUiState.Sending
            val result = pairRepository.sendRequest(receiverPartnerId)
            result.onSuccess { request ->
                _uiState.value = PairUiState.WaitingForResponse(request)
                onSuccess()
            }.onFailure { error ->
                _uiState.value = PairUiState.Error(error.message ?: "Failed to send pair request.")
            }
        }
    }

    fun acceptPairRequest(requestId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = PairUiState.Loading
            val result = pairRepository.acceptRequest(requestId)
            result.onSuccess {
                _uiState.value = PairUiState.Connected
                authRepository.fetchCurrentUserProfile()
                onSuccess()
            }.onFailure { error ->
                _uiState.value = PairUiState.Error(error.message ?: "Failed to accept pair request.")
            }
        }
    }

    fun rejectPairRequest(requestId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = PairUiState.Loading
            val result = pairRepository.rejectRequest(requestId)
            result.onSuccess {
                _uiState.value = PairUiState.Idle
                onSuccess()
            }.onFailure { error ->
                _uiState.value = PairUiState.Error(error.message ?: "Failed to reject pair request.")
            }
        }
    }

    fun cancelPairRequest(requestId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = PairUiState.Loading
            val result = pairRepository.cancelRequest(requestId)
            result.onSuccess {
                _uiState.value = PairUiState.Idle
                onSuccess()
            }.onFailure { error ->
                _uiState.value = PairUiState.Error(error.message ?: "Failed to cancel pair request.")
            }
        }
    }

    fun unpair(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = PairUiState.Loading
            val result = pairRepository.unpair()
            result.onSuccess {
                _uiState.value = PairUiState.Idle
                authRepository.fetchCurrentUserProfile()
                onSuccess()
            }.onFailure { error ->
                _uiState.value = PairUiState.Error(error.message ?: "Failed to unpair.")
            }
        }
    }

    fun saveRelationshipDate(startDate: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = PairUiState.Loading
            val result = pairRepository.saveRelationshipDate(startDate)
            result.onSuccess {
                _uiState.value = PairUiState.Connected
                authRepository.fetchCurrentUserProfile()
                onSuccess()
            }.onFailure { error ->
                _uiState.value = PairUiState.Error(error.message ?: "Failed to save relationship date.")
            }
        }
    }

    fun clearError() {
        if (_uiState.value is PairUiState.Error) {
            _uiState.value = PairUiState.Idle
        }
    }

    fun resetState() {
        _uiState.value = PairUiState.Idle
    }
}
