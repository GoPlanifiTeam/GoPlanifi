package com.example.goplanify.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.goplanify.domain.model.Preferences
import com.example.goplanify.domain.model.User
import com.example.goplanify.domain.repository.PreferencesRepository
import com.example.goplanify.ui.screens.setLocale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: String = "es"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> get() = _settingsState

    // Cargar las preferencias del usuario
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
        preferencesRepository.toggleNotifications(user, enabled)
    }

    // Cambiar el idioma y guardar la preferencia
    fun changeLanguage(user: User, language: String, context: Context) {
        _settingsState.update { it.copy(selectedLanguage = language) }
        preferencesRepository.savePreferences(user, Preferences(user, _settingsState.value.notificationsEnabled, language, "default"))
        setLocale(context, language)
        _settingsState.value = _settingsState.value.copy(selectedLanguage = language)
    }


    // Método para obtener el idioma desde SharedPreferences si se desea
    fun getSavedLanguage(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("language", Locale.getDefault().language) ?: "pt"
    }

}
