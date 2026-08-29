package com.pairlink.app.data.repository

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.pairlink.app.MainActivity
import com.pairlink.app.R
import com.pairlink.app.core.notifications.NotificationChannelsHelper
import com.pairlink.app.data.local.dao.NotificationDao
import com.pairlink.app.data.local.entity.NotificationEntity
import com.pairlink.app.data.local.preferences.NotificationPreferences
import com.pairlink.app.domain.model.DefaultWishlist
import com.pairlink.app.domain.model.NotificationCategoryFilter
import com.pairlink.app.domain.model.NotificationItem
import com.pairlink.app.domain.model.NotificationType
import com.pairlink.app.domain.model.WishlistItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomNotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val moodRepository: MoodRepository,
    private val statusRepository: StatusRepository,
    private val partnerRepository: PartnerRepository,
    private val notificationPreferences: NotificationPreferences,
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotificationRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    override val notifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
        .map { list -> list.map { it.toDomain() } }
        .flowOn(ioDispatcher)

    override val unreadCount: Flow<Int> = notificationDao.getUnreadCount()
        .flowOn(ioDispatcher)

    private val _wishlist = MutableStateFlow(DefaultWishlist)
    override val wishlist: StateFlow<List<WishlistItem>> = _wishlist.asStateFlow()

    private var lastPartnerMood: String? = null
    private var lastPartnerStatus: String? = null

    init {
        // Live mood sync listener
        scope.launch {
            moodRepository.observePartnerMood().collect { newMood ->
                if (lastPartnerMood != null && lastPartnerMood != newMood) {
                    try {
                        val prefs = notificationPreferences.preferencesFlow.first()
                        if (prefs.moodEnabled) {
                            val partnerName = partnerRepository.partnerProfile.value.name.ifBlank { "Partner" }
                            addNotification(
                                title = "❤️ $partnerName updated mood",
                                message = "Mood is now '$newMood'",
                                type = NotificationType.MOOD_UPDATED,
                                iconName = "mood",
                                senderUid = partnerRepository.partnerProfile.value.id
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error adding mood notification")
                    }
                }
                lastPartnerMood = newMood
            }
        }

        // Live status sync listener
        scope.launch {
            statusRepository.observePartnerStatus().collect { (newStatus, message) ->
                if (lastPartnerStatus != null && lastPartnerStatus != newStatus) {
                    try {
                        val prefs = notificationPreferences.preferencesFlow.first()
                        val isReachedHome = newStatus.contains("Home", ignoreCase = true)

                        if ((isReachedHome && prefs.reachedHomeEnabled) || (!isReachedHome && prefs.statusEnabled)) {
                            val partnerName = partnerRepository.partnerProfile.value.name.ifBlank { "Partner" }
                            val title = if (isReachedHome) "🏠 $partnerName reached home safely." else "$partnerName is $newStatus"
                            val notifMessage = message.ifBlank { "Activity status updated in your sanctuary." }
                            val type = if (isReachedHome) NotificationType.REACHED_HOME else NotificationType.STATUS_UPDATED
                            val icon = if (isReachedHome) "home" else "work"

                            addNotification(
                                title = title,
                                message = notifMessage,
                                type = type,
                                iconName = icon,
                                senderUid = partnerRepository.partnerProfile.value.id
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error adding status notification")
                    }
                }
                lastPartnerStatus = newStatus
            }
        }
    }

    override suspend fun addNotification(
        title: String,
        message: String,
        type: NotificationType,
        iconName: String,
        senderUid: String
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            // 1. Insert into local Room database (for In-App Notification Center)
            val entity = NotificationEntity(
                id = "NOTIF-${System.currentTimeMillis()}-${(100..999).random()}",
                title = title,
                message = message,
                type = type.name,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                iconName = iconName,
                senderUid = senderUid
            )
            notificationDao.insertNotification(entity)

            // 2. Post Android System Status Bar Notification
            showSystemNotification(title, message, type)

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving notification: %s", e.message)
            Result.success(Unit)
        }
    }

    private fun showSystemNotification(title: String, message: String, type: NotificationType) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
            val channelId = when (type) {
                NotificationType.PRESENCE -> NotificationChannelsHelper.CHANNEL_PRESENCE
                NotificationType.MOOD_UPDATED -> NotificationChannelsHelper.CHANNEL_MOOD
                NotificationType.STATUS_UPDATED, NotificationType.REACHED_HOME -> NotificationChannelsHelper.CHANNEL_STATUS
                NotificationType.BIRTHDAY_REMINDER, NotificationType.BIRTHDAY_TODAY -> NotificationChannelsHelper.CHANNEL_BIRTHDAY
                NotificationType.ANNIVERSARY_REMINDER, NotificationType.ANNIVERSARY_TODAY -> NotificationChannelsHelper.CHANNEL_ANNIVERSARY
                NotificationType.PAIR_REQUEST, NotificationType.PAIR_ACCEPTED, NotificationType.UNPAIR -> NotificationChannelsHelper.CHANNEL_RELATIONSHIP
                NotificationType.SYSTEM -> NotificationChannelsHelper.CHANNEL_SYSTEM
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                type.ordinal,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify((1000..9999).random(), notification)
        } catch (e: Exception) {
            Timber.w(e, "Could not post Android status bar notification")
        }
    }

    override suspend fun markAsRead(id: String): Result<Unit> = withContext(ioDispatcher) {
        notificationDao.markAsRead(id)
        Result.success(Unit)
    }

    override suspend fun markAllAsRead(): Result<Unit> = withContext(ioDispatcher) {
        notificationDao.markAllAsRead()
        Result.success(Unit)
    }

    override suspend fun deleteNotification(id: String): Result<Unit> = withContext(ioDispatcher) {
        notificationDao.deleteNotification(id)
        Result.success(Unit)
    }

    override suspend fun clearAllNotifications(): Result<Unit> = withContext(ioDispatcher) {
        notificationDao.clearAll()
        Result.success(Unit)
    }

    override fun filterNotifications(
        query: String,
        filter: NotificationCategoryFilter
    ): Flow<List<NotificationItem>> {
        val baseFlow = if (query.isBlank()) {
            notificationDao.getAllNotifications()
        } else {
            notificationDao.searchNotifications(query.trim())
        }

        return baseFlow.map { list ->
            list.map { it.toDomain() }.filter { item ->
                when (filter) {
                    NotificationCategoryFilter.ALL -> true
                    NotificationCategoryFilter.PRESENCE -> item.type == NotificationType.PRESENCE
                    NotificationCategoryFilter.MOOD -> item.type == NotificationType.MOOD_UPDATED
                    NotificationCategoryFilter.STATUS -> item.type == NotificationType.STATUS_UPDATED || item.type == NotificationType.REACHED_HOME
                    NotificationCategoryFilter.BIRTHDAY -> item.type == NotificationType.BIRTHDAY_REMINDER || item.type == NotificationType.BIRTHDAY_TODAY
                    NotificationCategoryFilter.ANNIVERSARY -> item.type == NotificationType.ANNIVERSARY_REMINDER || item.type == NotificationType.ANNIVERSARY_TODAY
                    NotificationCategoryFilter.SYSTEM -> item.type == NotificationType.SYSTEM || item.type == NotificationType.PAIR_REQUEST || item.type == NotificationType.PAIR_ACCEPTED || item.type == NotificationType.UNPAIR
                }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun addWishlistItem(title: String): Result<Unit> {
        val newItem = WishlistItem(
            id = System.currentTimeMillis().toString(),
            title = title,
            isSaved = true
        )
        _wishlist.value = _wishlist.value + newItem
        return Result.success(Unit)
    }

    override suspend fun deleteWishlistItem(id: String): Result<Unit> {
        _wishlist.value = _wishlist.value.filterNot { it.id == id }
        return Result.success(Unit)
    }
}
