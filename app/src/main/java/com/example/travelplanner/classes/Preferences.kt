package com.example.travelplanner.classes
//
data class  Preferences(
    var userId: String,
    var notificationsEnabled: Boolean,
    var preferredLanguage: String, //var por que se toca el valor
    var theme: String //var por que se toca el valor
)
{ //Funciones de la clase
    fun updatePreferences(newTheme: String, newLanguage: String, newNotificationsEnabled: Boolean) {
        this.theme = newTheme
        this.preferredLanguage = newLanguage
        this.notificationsEnabled = newNotificationsEnabled
    }
}