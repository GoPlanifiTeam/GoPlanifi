package com.example.travelplanner.domain.model

data class Preferences(
    var userId: String,
    var notificationsEnabled: Boolean,
    var preferredLanguage: String,
    var theme: String
) {
    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_CATALAN = "ca"
        const val LANGUAGE_SPANISH = "es"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"

        val SUPPORTED_LANGUAGES = listOf(LANGUAGE_ENGLISH, LANGUAGE_CATALAN, LANGUAGE_SPANISH)
        val SUPPORTED_THEMES = listOf(THEME_LIGHT, THEME_DARK, THEME_SYSTEM)
    }

    fun updatePreferences(newTheme: String, newLanguage: String, newNotificationsEnabled: Boolean) {
        this.theme = newTheme
        this.preferredLanguage = newLanguage
        this.notificationsEnabled = newNotificationsEnabled
    }
}