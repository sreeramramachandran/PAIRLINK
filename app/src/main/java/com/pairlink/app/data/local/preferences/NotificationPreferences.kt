package com.pairlink.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_preferences")

data class NotificationPreferencesState(
    val presenceEnabled: Boolean = true,
    val moodEnabled: Boolean = true,
    val statusEnabled: Boolean = true,
    val birthdayEnabled: Boolean = true,
    val anniversaryEnabled: Boolean = true,
    val reachedHomeEnabled: Boolean = true,
    val systemEnabled: Boolean = true
)

@Singleton
class NotificationPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val PRESENCE_ENABLED = booleanPreferencesKey("presence_enabled")
        val MOOD_ENABLED = booleanPreferencesKey("mood_enabled")
        val STATUS_ENABLED = booleanPreferencesKey("status_enabled")
        val BIRTHDAY_ENABLED = booleanPreferencesKey("birthday_enabled")
        val ANNIVERSARY_ENABLED = booleanPreferencesKey("anniversary_enabled")
        val REACHED_HOME_ENABLED = booleanPreferencesKey("reached_home_enabled")
        val SYSTEM_ENABLED = booleanPreferencesKey("system_enabled")
    }

    val preferencesFlow: Flow<NotificationPreferencesState> = context.dataStore.data.map { preferences ->
        NotificationPreferencesState(
            presenceEnabled = preferences[PreferencesKeys.PRESENCE_ENABLED] ?: true,
            moodEnabled = preferences[PreferencesKeys.MOOD_ENABLED] ?: true,
            statusEnabled = preferences[PreferencesKeys.STATUS_ENABLED] ?: true,
            birthdayEnabled = preferences[PreferencesKeys.BIRTHDAY_ENABLED] ?: true,
            anniversaryEnabled = preferences[PreferencesKeys.ANNIVERSARY_ENABLED] ?: true,
            reachedHomeEnabled = preferences[PreferencesKeys.REACHED_HOME_ENABLED] ?: true,
            systemEnabled = preferences[PreferencesKeys.SYSTEM_ENABLED] ?: true
        )
    }

    suspend fun setPresenceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.PRESENCE_ENABLED] = enabled }
    }

    suspend fun setMoodEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.MOOD_ENABLED] = enabled }
    }

    suspend fun setStatusEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.STATUS_ENABLED] = enabled }
    }

    suspend fun setBirthdayEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.BIRTHDAY_ENABLED] = enabled }
    }

    suspend fun setAnniversaryEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ANNIVERSARY_ENABLED] = enabled }
    }

    suspend fun setReachedHomeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.REACHED_HOME_ENABLED] = enabled }
    }

    suspend fun setSystemEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SYSTEM_ENABLED] = enabled }
    }
}
