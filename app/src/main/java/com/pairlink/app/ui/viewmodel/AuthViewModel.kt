package com.pairlink.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.data.repository.AuthRepository
import com.pairlink.app.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: UserProfile) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UserProfile> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun checkSession() {
        viewModelScope.launch {
            if (authRepository.isUserLoggedIn()) {
                authRepository.fetchCurrentUserProfile()
            }
        }
    }

    suspend fun restoreSession(): Boolean {
        if (!authRepository.isUserLoggedIn()) {
            return false
        }
        val result = authRepository.fetchCurrentUserProfile()
        val user = result.getOrNull()
        return user != null && user.uid.isNotBlank()
    }

    fun login(
        phone: String,
        pin: String,
        onSuccess: () -> Unit
    ) {
        val cleanPhone = phone.filter { it.isDigit() }
        if (cleanPhone.length < 8) {
            _uiState.value = AuthUiState.Error("Please enter a valid phone number (at least 8 digits).")
            return
        }
        if (pin.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(phone, pin)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
                onSuccess()
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Authentication failed. Please check credentials.")
            }
        }
    }

    fun register(
        username: String,
        phone: String,
        dob: String,
        pin: String,
        confirmPin: String = pin,
        imageUri: Uri? = null,
        onSuccess: () -> Unit
    ) {
        if (username.trim().isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter a username.")
            return
        }
        val cleanPhone = phone.filter { it.isDigit() }
        if (cleanPhone.length < 8) {
            _uiState.value = AuthUiState.Error("Please enter a valid phone number.")
            return
        }
        if (dob.trim().isBlank()) {
            _uiState.value = AuthUiState.Error("Please select your date of birth.")
            return
        }
        if (pin.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }
        if (pin != confirmPin) {
            _uiState.value = AuthUiState.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(username, phone, dob, pin, imageUri)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
                onSuccess()
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Registration failed. Please try again.")
            }
        }
    }

    fun updateRelationshipDate(startDate: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.updateRelationshipDate(startDate)
            onSuccess()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Idle
            onSuccess()
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
