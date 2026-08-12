package com.pairlink.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.pairlink.app.core.util.ImageUtils
import com.pairlink.app.data.repository.AuthRepository
import com.pairlink.app.data.repository.PartnerRepository
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

data class ProfileUiState(
    val partner: PartnerProfile = PartnerProfile(),
    val user: UserProfile = UserProfile()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val partnerRepository: PartnerRepository,
    private val authRepository: AuthRepository,
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    val uiState: StateFlow<ProfileUiState> = combine(
        partnerRepository.partnerProfile,
        authRepository.currentUser
    ) { partner, user ->
        ProfileUiState(partner = partner, user = user)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun saveProfile(
        username: String,
        nickname: String,
        avatarUri: Uri?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            var uploadedUrl: String? = null
            try {
                if (avatarUri != null) {
                    val uid = authRepository.currentUser.value.uid
                    val compressedBytes = ImageUtils.compressUriToByteArray(context, avatarUri)

                    // 1. Attempt upload to Firebase Storage with timeout
                    if (uid.isNotBlank() && compressedBytes != null) {
                        val storageResult = withTimeoutOrNull(10000L) {
                            try {
                                val imageRef = storage.reference.child("profile_images/$uid.jpg")
                                imageRef.putBytes(compressedBytes).await()
                                val downloadUrl = imageRef.downloadUrl.await().toString()
                                Timber.i("Uploaded avatar to Firebase Storage: %s", downloadUrl)
                                downloadUrl
                            } catch (e: Exception) {
                                Timber.w(e, "Firebase Storage upload failed: %s. Using Base64 fallback.", e.message)
                                null
                            }
                        }
                        uploadedUrl = storageResult
                    }

                    // 2. If Firebase Storage failed, unavailable, or timed out, fallback to Base64 data URI
                    if (uploadedUrl.isNullOrBlank()) {
                        uploadedUrl = ImageUtils.compressUriToBase64(context, avatarUri)
                        Timber.i("Fallback avatar saved as Base64 Data URI (%d chars)", uploadedUrl?.length ?: 0)
                    }
                }

                // Update only current user's profile and partner nickname (preserves original DOB)
                authRepository.updateProfile(
                    username = username,
                    nickname = nickname,
                    dob = "", // Preserves existing DOB
                    avatarUrl = uploadedUrl
                )
            } catch (e: Exception) {
                Timber.e(e, "Error saving profile: %s", e.message)
            } finally {
                _isSaving.value = false
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }
}
