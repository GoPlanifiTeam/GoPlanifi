package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.PreferencesDao
import com.example.goplanify.data.local.mapper.toDomain
import com.example.goplanify.data.local.mapper.toEntity
import com.example.goplanify.domain.model.Preferences
import com.example.goplanify.domain.repository.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dao: PreferencesDao
) : PreferencesRepository {

    override suspend fun getPreferences(userId: String): Preferences? {
        return try {
            val result = dao.getByUserId(userId)
            Log.d("DB-Preferences", "Fetched preferences for userId=$userId → $result")
            result?.toDomain()
        } catch (e: Exception) {
            Log.e("DB-Preferences", "Error fetching preferences for userId=$userId", e)
            null
        }
    }

    override suspend fun savePreferences(preferences: Preferences) {
        try {
            dao.insertOrUpdate(preferences.toEntity())
            Log.d("DB-Preferences", "Saved preferences for userId=${preferences.userId}")
        } catch (e: Exception) {
            Log.e("DB-Preferences", "Error saving preferences for userId=${preferences.userId}", e)
        }
    }
}
