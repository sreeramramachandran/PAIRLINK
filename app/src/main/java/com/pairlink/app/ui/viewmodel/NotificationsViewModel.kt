package com.pairlink.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pairlink.app.data.repository.NotificationRepository
import com.pairlink.app.data.repository.PartnerRepository
import com.pairlink.app.domain.model.NotificationCategoryFilter
import com.pairlink.app.domain.model.NotificationItem
import com.pairlink.app.domain.model.PartnerProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val unreadCount: Int = 0,
    val searchQuery: String = "",
    val selectedFilter: NotificationCategoryFilter = NotificationCategoryFilter.ALL,
    val partner: PartnerProfile = PartnerProfile()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    partnerRepository: PartnerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(NotificationCategoryFilter.ALL)
    val selectedFilter: StateFlow<NotificationCategoryFilter> = _selectedFilter.asStateFlow()

    val uiState: StateFlow<NotificationsUiState> = combine(
        _searchQuery,
        _selectedFilter
    ) { query, filter ->
        Pair(query, filter)
    }.flatMapLatest { (query, filter) ->
        combine(
            notificationRepository.filterNotifications(query, filter),
            notificationRepository.unreadCount,
            partnerRepository.partnerProfile
        ) { notifs, unread, partner ->
            NotificationsUiState(
                notifications = notifs,
                unreadCount = unread,
                searchQuery = query,
                selectedFilter = filter,
                partner = partner
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotificationsUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: NotificationCategoryFilter) {
        _selectedFilter.value = filter
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            notificationRepository.clearAllNotifications()
        }
    }
}
