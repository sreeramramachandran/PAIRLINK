package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.data.repository.NotificationRepository
import com.pairlink.app.data.repository.PartnerRepository
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.domain.model.WishlistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

data class BirthdayUiState(
    val partner: PartnerProfile = PartnerProfile(),
    val wishlist: List<WishlistItem> = emptyList(),
    val daysLeft: Int = 0,
    val formattedBirthday: String = "Set Birthday"
)

@HiltViewModel
class BirthdayViewModel @Inject constructor(
    private val partnerRepository: PartnerRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val uiState: StateFlow<BirthdayUiState> = combine(
        partnerRepository.partnerProfile,
        notificationRepository.wishlist
    ) { partner, wishlist ->
        BirthdayUiState(
            partner = partner,
            wishlist = wishlist,
            daysLeft = calculateDaysLeft(partner.dob),
            formattedBirthday = formatBirthdayDate(partner.dob)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BirthdayUiState()
    )

    fun addWishlistItem(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            notificationRepository.addWishlistItem(title.trim())
        }
    }

    fun deleteWishlistItem(id: String) {
        viewModelScope.launch {
            notificationRepository.deleteWishlistItem(id)
        }
    }

    fun updateBirthday(newDob: String) {
        viewModelScope.launch {
            partnerRepository.updatePartnerDob(newDob)
        }
    }

    private fun calculateDaysLeft(dobStr: String): Int {
        if (dobStr.isBlank()) return 0
        return try {
            val today = LocalDate.now()
            val dob = LocalDate.parse(dobStr)
            var nextBirthday = dob.withYear(today.year)
            if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1)
            }
            ChronoUnit.DAYS.between(today, nextBirthday).toInt()
        } catch (_: Exception) {
            0
        }
    }

    private fun formatBirthdayDate(dobStr: String): String {
        if (dobStr.isBlank()) return "Set Birthday"
        return try {
            val dob = LocalDate.parse(dobStr)
            val month = dob.format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault()))
            val day = dob.dayOfMonth
            val suffix = when {
                day in 11..13 -> "th"
                day % 10 == 1 -> "st"
                day % 10 == 2 -> "nd"
                day % 10 == 3 -> "rd"
                else -> "th"
            }
            "$month $day$suffix"
        } catch (_: Exception) {
            "Set Birthday"
        }
    }
}
