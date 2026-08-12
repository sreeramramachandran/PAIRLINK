package com.pairlink.app.data.repository

import com.pairlink.app.domain.model.NotificationCategoryFilter
import com.pairlink.app.domain.model.NotificationItem
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for Notification Center, Room persistence, and wishlist.
 */
interface NotificationRepository {
    val notifications: Flow<List<NotificationItem>>
    val unreadCount: Flow<Int>
    val wishlist: StateFlow<List<WishlistItem>>

    suspend fun addNotification(
        title: String,
        message: String,
        type: NotificationType,
        iconName: String = "favorite",
        senderUid: String = ""
    ): Result<Unit>

    suspend fun markAsRead(id: String): Result<Unit>
    suspend fun markAllAsRead(): Result<Unit>
    suspend fun deleteNotification(id: String): Result<Unit>
    suspend fun clearAllNotifications(): Result<Unit>
    fun filterNotifications(query: String, filter: NotificationCategoryFilter): Flow<List<NotificationItem>>
    suspend fun addWishlistItem(title: String): Result<Unit>
    suspend fun deleteWishlistItem(id: String): Result<Unit>
}
