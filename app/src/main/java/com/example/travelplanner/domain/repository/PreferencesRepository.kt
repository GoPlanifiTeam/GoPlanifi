package com.example.travelplanner.domain.repository

import com.example.travelplanner.domain.model.Preferences

interface PreferencesRepository {
    fun savePreferences(preferences: Preferences): Boolean
    fun getPreferences(userId: String): Preferences?
    fun updatePreferences(preferences: Preferences): Boolean
    fun deletePreferences(userId: String): Boolean
}