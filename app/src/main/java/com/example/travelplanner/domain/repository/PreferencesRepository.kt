package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Preferences
import com.example.travelplanner.domain.model.User

class PreferencesRepository {
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