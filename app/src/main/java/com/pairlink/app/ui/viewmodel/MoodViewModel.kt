package com.pairlink.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.core.util.StickerStorageHelper
import com.pairlink.app.data.repository.FirebaseStickerUploader
import com.pairlink.app.data.repository.MoodRepository
import com.pairlink.app.data.repository.NotificationRepository
import com.pairlink.app.domain.model.MoodItem
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.StickerItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MoodUiState(
    val currentMood: String = "Happy",
    val availableMoods: List<MoodItem> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val notificationRepository: NotificationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val uiState: StateFlow<MoodUiState> = combine(
        moodRepository.currentMood,
        moodRepository.availableMoods,
        _errorMessage,
        _isLoading
    ) { mood, moods, error, loading ->
        MoodUiState(
            currentMood = mood,
            availableMoods = moods,
            errorMessage = error,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MoodUiState()
    )

    fun saveMood(mood: String, onComplete: () -> Unit = {}) {
        val cleanMood = mood.trim()
        if (cleanMood.isBlank()) {
            _errorMessage.value = "Please select or enter a mood."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = moodRepository.setMood(cleanMood)
            _isLoading.value = false

            result.onSuccess {
                notificationRepository.addNotification(
                    title = "Mood updated to '$cleanMood'",
                    message = "Your mood was updated in your sanctuary. ✨",
                    type = NotificationType.MOOD_UPDATED,
                    iconName = "mood"
                )
                onComplete()
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to update mood."
            }
        }
    }

    fun saveStickerMood(sticker: StickerItem, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val cloudUrl = FirebaseStickerUploader.uploadStickerIfNeeded(context, sticker.urlOrRes)
            StickerStorageHelper.updateStickerCloudUrl(context, sticker.id, cloudUrl)

            val stickerRes = moodRepository.setStickerMood(cloudUrl, sticker.id)
            val moodRes = moodRepository.setMood("Feeling " + sticker.name)

            _isLoading.value = false

            if (stickerRes.isSuccess && moodRes.isSuccess) {
                notificationRepository.addNotification(
                    title = "Mood sticker updated",
                    message = "Your sticker mood was updated in your sanctuary. ✨",
                    type = NotificationType.MOOD_UPDATED,
                    iconName = "mood"
                )
                onComplete()
            } else {
                _errorMessage.value = "Failed to update sticker mood."
            }
        }
    }

    fun deleteCustomMood(moodId: String) {
        viewModelScope.launch {
            moodRepository.deleteCustomMood(moodId)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
