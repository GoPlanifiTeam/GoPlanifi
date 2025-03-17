package com.example.travelplanner.data.repository

import com.example.travelplanner.domain.model.Preferences
import com.example.travelplanner.domain.repository.PreferencesRepository

class PreferencesRepositoryImpl : PreferencesRepository {
    private val userPreferences = mutableListOf<Preferences>()

    override fun savePreferences(preferences: Preferences): Boolean {
        if (!validatePreferences(preferences)) {
            return false
        }

        // Check if preferences already exist for this user
        val existingIndex = userPreferences.indexOfFirst { it.userId == preferences.userId }
        if (existingIndex != -1) {
            // Update existing preferences
            userPreferences[existingIndex] = preferences
        } else {
            // Add new preferences
            userPreferences.add(preferences)
        }

        return true
    }

    override fun getPreferences(userId: String): Preferences? {
        if (userId.isBlank()) {
            return null
        }

        return userPreferences.find { it.userId == userId }
    }

    override fun updatePreferences(preferences: Preferences): Boolean {
        if (!validatePreferences(preferences)) {
            return false
        }

        val index = userPreferences.indexOfFirst { it.userId == preferences.userId }
        if (index != -1) {
            userPreferences[index] = preferences
            return true
        }
        return false
    }

    override fun deletePreferences(userId: String): Boolean {
        if (userId.isBlank()) {
            return false
        }

        val initialSize = userPreferences.size
        userPreferences.removeAll { it.userId == userId }
        return userPreferences.size < initialSize
    }

    private fun validatePreferences(preferences: Preferences): Boolean {
        // Check required fields
        if (preferences.userId.isBlank()) {
            return false
        }

        // Validate theme
        if (preferences.theme !in Preferences.SUPPORTED_THEMES) {
            return false
        }

        // Validate language
        if (preferences.preferredLanguage !in Preferences.SUPPORTED_LANGUAGES) {
            return false
        }

        return true
    }
}