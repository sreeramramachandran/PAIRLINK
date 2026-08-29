package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.core.haptics.PresenceHapticManager
import com.pairlink.app.data.repository.AuthRepository
import com.pairlink.app.data.repository.NotificationRepository
import com.pairlink.app.data.repository.PartnerRepository
import com.pairlink.app.data.repository.PresenceRepository
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.PresenceRingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface PresenceUiState {
    data object Idle : PresenceUiState
    data object Holding : PresenceUiState
    data class PartnerHolding(val startedAt: Long) : PresenceUiState
    data object Receiving : PresenceUiState
    data object Released : PresenceUiState
    data class Error(val message: String) : PresenceUiState
    data object Loading : PresenceUiState
}

@HiltViewModel
class PresenceViewModel @Inject constructor(
    private val presenceRepository: PresenceRepository,
    private val authRepository: AuthRepository,
    private val partnerRepository: PartnerRepository,
    private val notificationRepository: NotificationRepository,
    private val hapticManager: PresenceHapticManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<PresenceUiState>(PresenceUiState.Idle)
    val uiState: StateFlow<PresenceUiState> = _uiState.asStateFlow()

    private val _isUserHolding = MutableStateFlow(false)
    val isUserHolding: StateFlow<Boolean> = _isUserHolding.asStateFlow()

    // Presence Ring Priority: Holding -> Driving -> Sleeping -> Busy -> Online -> Offline
    val ringState: StateFlow<PresenceRingState> = combine(
        _uiState,
        partnerRepository.partnerProfile
    ) { state, partner ->
        when {
            state is PresenceUiState.PartnerHolding -> PresenceRingState.HOLDING
            partner.currentStatus.contains("Driv", ignoreCase = true) -> PresenceRingState.DRIVING
            partner.currentStatus.contains("Sleep", ignoreCase = true) -> PresenceRingState.SLEEPING
            partner.currentStatus.contains("Busy", ignoreCase = true) || partner.currentStatus.contains("Work", ignoreCase = true) -> PresenceRingState.BUSY
            partner.isOnline -> PresenceRingState.ONLINE
            else -> PresenceRingState.OFFLINE
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PresenceRingState.ONLINE
    )

    private var lastHandledPulseTime = System.currentTimeMillis()
    private var lastHandledPulseCount = 0L

    init {
        // Sync FCM token
        viewModelScope.launch {
            try {
                presenceRepository.syncFcmToken()
            } catch (e: Exception) {
                Timber.w(e, "Error syncing FCM token in PresenceViewModel")
            }
        }

        // Listen for real-time presence & heart pulses from partner
        viewModelScope.launch {
            try {
                presenceRepository.observePresence().collect { doc ->
                    try {
                        val currentUid = authRepository.currentUser.value.uid
                        if (doc != null) {
                            // 1. Partner Heart Tap / Pulse received -> Vibrate partner's phone & record notification
                            val now = System.currentTimeMillis()
                            val isRecentPulse = (now - doc.lastPulseTimestamp) < 15000L // Sent in last 15 seconds
                            val isNewPulse = doc.pulseSenderUid.isNotBlank() &&
                                    doc.pulseSenderUid != currentUid &&
                                    isRecentPulse &&
                                    ((doc.pulseCount > 0L && doc.pulseCount > lastHandledPulseCount) ||
                                            (doc.lastPulseTimestamp > 0L && doc.lastPulseTimestamp > lastHandledPulseTime))

                            if (isNewPulse) {
                                if (doc.pulseCount > 0L) lastHandledPulseCount = doc.pulseCount
                                if (doc.lastPulseTimestamp > 0L) lastHandledPulseTime = doc.lastPulseTimestamp

                                hapticManager.triggerPulseVibration()

                                val partnerName = partnerRepository.partnerProfile.value.name.ifBlank { "Partner" }
                                notificationRepository.addNotification(
                                    title = "❤️ $partnerName sent you love!",
                                    message = "They sent a sweet heartbeat pulse to your sanctuary. ✨",
                                    type = NotificationType.PRESENCE,
                                    senderUid = doc.pulseSenderUid
                                )
                            }

                            // 2. Partner continuous hold presence
                            if (doc.isHolding && doc.senderUid.isNotBlank() && doc.senderUid != currentUid) {
                                _uiState.value = PresenceUiState.PartnerHolding(doc.startedAt)
                                hapticManager.startHeartbeatVibration()
                            } else {
                                if (_uiState.value is PresenceUiState.PartnerHolding) {
                                    hapticManager.stopHeartbeatVibration()
                                    _uiState.value = PresenceUiState.Idle
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error processing presence document update")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in observePresence collection flow")
            }
        }
    }

    fun onHeartPressed() {
        if (_isUserHolding.value) return
        _isUserHolding.value = true
        _uiState.value = PresenceUiState.Holding
        hapticManager.triggerHeartPressFeedback()

        viewModelScope.launch {
            try {
                presenceRepository.startPresence()
            } catch (e: Exception) {
                Timber.w(e, "Error calling startPresence")
            }
        }
    }

    fun onHeartReleased() {
        if (!_isUserHolding.value) return
        _isUserHolding.value = false
        _uiState.value = PresenceUiState.Idle

        viewModelScope.launch {
            try {
                presenceRepository.stopPresence()
            } catch (e: Exception) {
                Timber.w(e, "Error calling stopPresence")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            hapticManager.stopHeartbeatVibration()
        } catch (_: Exception) { }
    }
}
