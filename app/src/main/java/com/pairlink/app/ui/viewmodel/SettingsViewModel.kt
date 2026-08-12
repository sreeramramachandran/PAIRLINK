package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.data.local.preferences.NotificationPreferences
import com.pairlink.app.data.local.preferences.NotificationPreferencesState
import com.pairlink.app.data.repository.AuthRepository
import com.pairlink.app.data.repository.NotificationRepository
import com.pairlink.app.data.repository.PairRepository
import com.pairlink.app.data.repository.PartnerRepository
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val partner: PartnerProfile = PartnerProfile(),
    val user: UserProfile = UserProfile(),
    val preferences: NotificationPreferencesState = NotificationPreferencesState(),
    val presenceHapticsEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val partnerRepository: PartnerRepository,
    private val pairRepository: PairRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val notificationPreferences: NotificationPreferences
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        partnerRepository.partnerProfile,
        authRepository.currentUser,
        notificationPreferences.preferencesFlow
    ) { partner, user, prefs ->
        SettingsUiState(
            partner = partner,
            user = user,
            preferences = prefs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun togglePresenceNotification() {
        val current = uiState.value.preferences.presenceEnabled
        viewModelScope.launch {
            notificationPreferences.setPresenceEnabled(!current)
        }
    }

    fun toggleMoodNotification() {
        val current = uiState.value.preferences.moodEnabled
        viewModelScope.launch {
            notificationPreferences.setMoodEnabled(!current)
        }
    }

    fun toggleStatusNotification() {
        val current = uiState.value.preferences.statusEnabled
        viewModelScope.launch {
            notificationPreferences.setStatusEnabled(!current)
        }
    }

    fun toggleBirthdayNotification() {
        val current = uiState.value.preferences.birthdayEnabled
        viewModelScope.launch {
            notificationPreferences.setBirthdayEnabled(!current)
        }
    }

    fun toggleAnniversaryNotification() {
        val current = uiState.value.preferences.anniversaryEnabled
        viewModelScope.launch {
            notificationPreferences.setAnniversaryEnabled(!current)
        }
    }

    fun toggleReachedHomeNotification() {
        val current = uiState.value.preferences.reachedHomeEnabled
        viewModelScope.launch {
            notificationPreferences.setReachedHomeEnabled(!current)
        }
    }

    fun toggleSystemNotification() {
        val current = uiState.value.preferences.systemEnabled
        viewModelScope.launch {
            notificationPreferences.setSystemEnabled(!current)
        }
    }

    fun unpair(onComplete: () -> Unit) {
        viewModelScope.launch {
            pairRepository.unpair()
            authRepository.unpair()
            notificationRepository.addNotification(
                title = "Unpaired Partner",
                message = "You have disconnected from your partner sanctuary.",
                type = NotificationType.UNPAIR
            )
            onComplete()
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}
