package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class HomeUiState(
    val partner: PartnerProfile = PartnerProfile(),
    val user: UserProfile = UserProfile(),
    val heartbeatCount: Int = 0,
    val togetherTimeText: String = "—",
    val birthdayDaysLeftText: String = "—"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val partnerRepository: PartnerRepository,
    private val authRepository: AuthRepository,
    private val pairRepository: PairRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        partnerRepository.partnerProfile,
        authRepository.currentUser,
        partnerRepository.heartPulseCount
    ) { partner, user, heartCount ->
        HomeUiState(
            partner = partner,
            user = user,
            heartbeatCount = heartCount,
            togetherTimeText = calculateTogetherTime(user.relationshipStartDate),
            birthdayDaysLeftText = calculateBirthdayDaysLeft(partner.dob)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun sendHeartPulse() {
        viewModelScope.launch {
            partnerRepository.sendHeartPulse()
            notificationRepository.addNotification(
                title = "You sent a heartbeat!",
                message = "A warm burst of affection was sent to your sanctuary. ❤️",
                type = NotificationType.PRESENCE
            )
        }
    }

    fun saveRelationshipDate(startDate: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            pairRepository.saveRelationshipDate(startDate)
            authRepository.updateRelationshipDate(startDate)
            notificationRepository.addNotification(
                title = "Relationship date updated",
                message = "Our milestone journey starts from $startDate. ✨",
                type = NotificationType.ANNIVERSARY_REMINDER
            )
            onComplete()
        }
    }

    fun updatePartnerBirthday(dob: String) {
        viewModelScope.launch {
            partnerRepository.updatePartnerDob(dob)
        }
    }

    private fun calculateTogetherTime(startDateStr: String): String {
        if (startDateStr.isBlank()) return "Set Date"
        return try {
            val start = LocalDate.parse(startDateStr)
            val now = LocalDate.now()
            val period = Period.between(start, now)
            val years = period.years
            val months = period.months
            val days = period.days
            when {
                years > 0 && months > 0 -> "${years}Y ${months}M"
                years > 0 -> "${years}Y"
                months > 0 -> "${months}M ${days}D"
                days > 0 -> "${days}D"
                else -> "Today"
            }
        } catch (_: Exception) {
            "Set Date"
        }
    }

    private fun calculateBirthdayDaysLeft(dobStr: String): String {
        if (dobStr.isBlank()) return "Set Date"
        return try {
            val today = LocalDate.now()
            val dob = LocalDate.parse(dobStr)
            var nextBirthday = dob.withYear(today.year)
            if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1)
            }
            val days = ChronoUnit.DAYS.between(today, nextBirthday)
            if (days == 0L) "Today! 🎉" else "$days Days"
        } catch (_: Exception) {
            "Set Date"
        }
    }
}
