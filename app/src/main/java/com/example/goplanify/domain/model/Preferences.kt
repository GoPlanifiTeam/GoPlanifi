package com.example.goplanify.domain.model

//
data class  Preferences(
    var userId: User,
    var notificationsEnabled: Boolean,
    var preferredLanguage: String, //var por que se toca el valor
    var theme: String //var por que se toca el valor
)
