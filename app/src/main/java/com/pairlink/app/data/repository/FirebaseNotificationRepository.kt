package com.pairlink.app.data.repository

import com.pairlink.app.domain.model.NotificationCategoryFilter
import com.pairlink.app.domain.model.NotificationItem
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.WishlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Delegating FirebaseNotificationRepository wrapping RoomNotificationRepository.
 */
@Singleton
class FirebaseNotificationRepository @Inject constructor(
    private val roomRepository: RoomNotificationRepository
) : NotificationRepository {

    override val notifications: Flow<List<NotificationItem>>
        get() = roomRepository.notifications

    override val unreadCount: Flow<Int>
        get() = roomRepository.unreadCount

    override val wishlist: StateFlow<List<WishlistItem>>
        get() = roomRepository.wishlist

    override suspend fun addNotification(
        title: String,
        message: String,
        type: NotificationType,
        iconName: String,
        senderUid: String
    ): Result<Unit> = roomRepository.addNotification(title, message, type, iconName, senderUid)

    override suspend fun markAsRead(id: String): Result<Unit> = roomRepository.markAsRead(id)

    override suspend fun markAllAsRead(): Result<Unit> = roomRepository.markAllAsRead()

    override suspend fun deleteNotification(id: String): Result<Unit> = roomRepository.deleteNotification(id)

    override suspend fun clearAllNotifications(): Result<Unit> = roomRepository.clearAllNotifications()

    override fun filterNotifications(
        query: String,
        filter: NotificationCategoryFilter
    ): Flow<List<NotificationItem>> = roomRepository.filterNotifications(query, filter)

    override suspend fun addWishlistItem(title: String): Result<Unit> = roomRepository.addWishlistItem(title)

    override suspend fun deleteWishlistItem(id: String): Result<Unit> = roomRepository.deleteWishlistItem(id)
}
