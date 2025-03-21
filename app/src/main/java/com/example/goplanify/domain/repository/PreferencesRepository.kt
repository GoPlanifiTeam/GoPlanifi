package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Preferences
import com.example.goplanify.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor() {
    private val userPreferences = mutableMapOf<String, Preferences>()

    fun savePreferences(user: User, preferences: Preferences) {
        userPreferences[user.userId] = preferences
    }

    fun getPreferences(user: User): Preferences? {
        return userPreferences[user.userId]
    }

    fun updateTheme(user: User, newTheme: String) {
        userPreferences[user.userId]?.theme = newTheme
    }

    fun toggleNotifications(user: User, isEnabled: Boolean) {
        userPreferences[user.userId]?.notificationsEnabled = isEnabled
    }
}