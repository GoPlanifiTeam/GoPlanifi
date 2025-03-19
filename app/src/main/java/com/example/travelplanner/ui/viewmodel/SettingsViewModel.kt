package com.example.travelplanner.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.model.Preferences
import com.example.travelplanner.domain.model.User
import com.example.travelplanner.domain.repository.PreferencesRepository
import com.example.travelplanner.ui.screens.setLocale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

// ✅ Define Settings UI State
data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: String = "en"
)

class SettingsViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> get() = _settingsState

    fun loadPreferences(user: User) {
        preferencesRepository.getPreferences(user)?.let { savedPreferences ->
            _settingsState.value = SettingsState(
                notificationsEnabled = savedPreferences.notificationsEnabled,
                selectedLanguage = savedPreferences.preferredLanguage
            )
        }
    }

    fun toggleNotifications(user: User, enabled: Boolean) {
        _settingsState.update { it.copy(notificationsEnabled = enabled) }
        preferencesRepository.toggleNotifications(user, enabled) // ✅ Save preference
    }

    fun changeLanguage(user: User, language: String, context: Context) {
        _settingsState.update { it.copy(selectedLanguage = language) }
        preferencesRepository.savePreferences(user, Preferences(user, _settingsState.value.notificationsEnabled, language, "default"))

        // ✅ Apply the new locale immediately
        setLocale(context, language)

        // ✅ Force a UI recomposition by resetting the state
        _settingsState.value = _settingsState.value.copy(selectedLanguage = language)
    }


    fun getSavedLanguage(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
    }
}
