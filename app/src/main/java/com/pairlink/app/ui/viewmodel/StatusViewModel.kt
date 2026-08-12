package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.data.repository.NotificationRepository
import com.pairlink.app.data.repository.StatusRepository
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.StatusItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatusUiState(
    val currentStatus: String = "Available",
    val lastUpdated: String = "Just now",
    val availableStatuses: List<StatusItem> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val statusRepository: StatusRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val uiState: StateFlow<StatusUiState> = combine(
        statusRepository.currentStatus,
        statusRepository.lastUpdatedText,
        statusRepository.availableStatuses,
        _errorMessage,
        _isLoading
    ) { status, updated, statuses, error, loading ->
        StatusUiState(
            currentStatus = status,
            lastUpdated = updated,
            availableStatuses = statuses,
            errorMessage = error,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatusUiState()
    )

    fun updateStatus(status: String, message: String? = null, onComplete: () -> Unit = {}) {
        val cleanStatus = status.trim()
        if (cleanStatus.isBlank()) {
            _errorMessage.value = "Please select or enter a status."
            return
        }
        if (cleanStatus.length > 60) {
            _errorMessage.value = "Status must be 60 characters or less."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = statusRepository.setStatus(cleanStatus, message)
            _isLoading.value = false

            result.onSuccess {
                val isReachedHome = cleanStatus.contains("Home", ignoreCase = true)
                notificationRepository.addNotification(
                    title = if (isReachedHome) "🏠 Reached Home" else "Status changed to '$cleanStatus'",
                    message = message ?: "Updated current sanctuary activity. 📍",
                    type = if (isReachedHome) NotificationType.REACHED_HOME else NotificationType.STATUS_UPDATED,
                    iconName = if (isReachedHome) "home" else "work"
                )
                onComplete()
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to update status."
            }
        }
    }

    fun addCustomStatus(status: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val result = statusRepository.addCustomStatus(status)
            result.onSuccess {
                notificationRepository.addNotification(
                    title = "New status created",
                    message = "Added '$status' to your status options. ✨",
                    type = NotificationType.STATUS_UPDATED,
                    iconName = "work"
                )
                onComplete()
            }
        }
    }

    fun deleteCustomStatus(statusId: String) {
        viewModelScope.launch {
            statusRepository.deleteCustomStatus(statusId)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
