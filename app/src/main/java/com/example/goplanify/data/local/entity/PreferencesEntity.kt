package com.example.goplanify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class PreferencesEntity(
    @PrimaryKey val userId: String,
    val notificationsEnabled: Boolean,
    val preferredLanguage: String,
    val theme: String
)
