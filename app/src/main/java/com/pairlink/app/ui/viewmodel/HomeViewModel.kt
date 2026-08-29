package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.core.haptics.PresenceHapticManager
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
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class HomeUiState(
    val partner: PartnerProfile = PartnerProfile(),
    val user: UserProfile = UserProfile(),
    val heartbeatCount: Int = 0,
    val togetherTimeText: String = "—",
    val birthdayDaysLeftText: String = "—",
    val nextMeetingDateText: String = "24 Dec 2026",
    val nextMeetingRawDate: String = "2026-12-24",
    val nextMeetingDaysLeft: String = "27",
    val nextMeetingHoursLeft: String = "14",
    val nextMeetingMinsLeft: String = "32"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val partnerRepository: PartnerRepository,
    private val authRepository: AuthRepository,
    private val pairRepository: PairRepository,
    private val notificationRepository: NotificationRepository,
    private val hapticManager: PresenceHapticManager,
    private val weatherRepository: com.pairlink.app.data.repository.WeatherRepository
) : ViewModel() {

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val user = authRepository.currentUser.value
            val partner = partnerRepository.partnerProfile.value
            
            // Update logged-in user's own location & live weather in Firestore
            val userLoc = if (user.locationName.isNotBlank() && !user.locationName.equals("Malappuram, India", ignoreCase = true)) user.locationName else "Indore, India"
            weatherRepository.updateLocationAndWeather(
                lat = if (user.latitude != 0.0) user.latitude else 22.7196,
                lon = if (user.longitude != 0.0) user.longitude else 75.8577,
                locationName = userLoc
            )

            // Fetch partner's live weather without overwriting user's Firestore document
            if (partner.latitude != 0.0 && partner.longitude != 0.0) {
                weatherRepository.fetchWeather(partner.latitude, partner.longitude)
            }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        partnerRepository.partnerProfile,
        authRepository.currentUser,
        partnerRepository.heartPulseCount
    ) { partner, user, heartCount ->
        val rawMeetingDate = user.nextMeetingStartDate.ifBlank { partner.nextMeetingDate.ifBlank { "2026-12-24" } }
        val (days, hours, mins, formattedDate) = calculateCountdown(rawMeetingDate)

        HomeUiState(
            partner = partner,
            user = user,
            heartbeatCount = heartCount,
            togetherTimeText = calculateTogetherTime(user.relationshipStartDate),
            birthdayDaysLeftText = calculateBirthdayDaysLeft(partner.dob),
            nextMeetingDateText = formattedDate,
            nextMeetingRawDate = rawMeetingDate,
            nextMeetingDaysLeft = days,
            nextMeetingHoursLeft = hours,
            nextMeetingMinsLeft = mins
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun sendHeartPulse() {
        hapticManager.triggerHeartPressFeedback()
        viewModelScope.launch {
            partnerRepository.sendHeartPulse()
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

    fun saveNextMeetingDate(meetingDate: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            pairRepository.saveNextMeetingDate(meetingDate)
            notificationRepository.addNotification(
                title = "Next meeting date updated ✈️",
                message = "Counting down to $meetingDate together! ❤️",
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

    private fun calculateCountdown(targetDateStr: String): Quadruple<String, String, String, String> {
        return try {
            val targetDate = LocalDate.parse(targetDateStr)
            val now = LocalDateTime.now()
            val targetDateTime = targetDate.atStartOfDay()

            if (targetDateTime.isBefore(now)) {
                val formatted = targetDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                return Quadruple("0", "0", "0", formatted)
            }

            val totalMinutes = ChronoUnit.MINUTES.between(now, targetDateTime)
            val days = totalMinutes / (24 * 60)
            val hours = (totalMinutes % (24 * 60)) / 60
            val mins = totalMinutes % 60
            val formatted = targetDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

            Quadruple(days.toString(), hours.toString(), mins.toString(), formatted)
        } catch (_: Exception) {
            Quadruple("27", "14", "32", "24 Dec 2026")
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
