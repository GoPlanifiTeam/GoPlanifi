package com.example.goplanify.data.local.mapper

import com.example.goplanify.data.local.entity.PreferencesEntity
import com.example.goplanify.domain.model.Preferences

// Entidad → Dominio
fun PreferencesEntity.toDomain(): Preferences =
    Preferences(
        userId = userId,
        notificationsEnabled = notificationsEnabled,
        preferredLanguage = preferredLanguage,
        theme = theme
    )

// Dominio → Entidad
fun Preferences.toEntity(): PreferencesEntity =
    PreferencesEntity(
        userId = userId,
        notificationsEnabled = notificationsEnabled,
        preferredLanguage = preferredLanguage,
        theme = theme
    )
