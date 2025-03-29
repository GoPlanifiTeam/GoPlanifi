package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Preferences

interface PreferencesRepository {
    suspend fun savePreferences(preferences: Preferences)
    suspend fun getPreferences(userId: String): Preferences?
}
