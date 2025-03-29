package com.example.goplanify.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.Preferences
import com.example.goplanify.domain.repository.PreferencesRepository
import com.example.goplanify.ui.screens.setLocale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: String = "es"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> get() = _settingsState

    fun loadPreferences(userId: String) {
        viewModelScope.launch {
            val prefs = preferencesRepository.getPreferences(userId)
            prefs?.let {
                _settingsState.value = SettingsState(
                    notificationsEnabled = it.notificationsEnabled,
                    selectedLanguage = it.preferredLanguage
                )
            }
        }
    }

    fun toggleNotifications(userId: String, enabled: Boolean) {
        _settingsState.update { it.copy(notificationsEnabled = enabled) }
        viewModelScope.launch {
            val current = preferencesRepository.getPreferences(userId)
            val newPrefs = Preferences(
                userId = userId,
                notificationsEnabled = enabled,
                preferredLanguage = current?.preferredLanguage ?: "es",
                theme = current?.theme ?: "default"
            )
            preferencesRepository.savePreferences(newPrefs)
        }
    }



    fun changeLanguage(userId: String, language: String, context: Context) {
        _settingsState.update { it.copy(selectedLanguage = language) }
        viewModelScope.launch {
            val current = preferencesRepository.getPreferences(userId)
            val newPrefs = Preferences(
                userId = userId,
                notificationsEnabled = current?.notificationsEnabled ?: true,
                preferredLanguage = language,
                theme = current?.theme ?: "default"
            )
            preferencesRepository.savePreferences(newPrefs)
            setLocale(context, language)
        }
    }

    suspend fun getSavedLanguageFromRoom(userId: String): String {
        val prefs = preferencesRepository.getPreferences(userId)
        return prefs?.preferredLanguage ?: "en"
    }
}