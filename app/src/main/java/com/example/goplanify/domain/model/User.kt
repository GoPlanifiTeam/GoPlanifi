package com.example.goplanify.domain.model

import java.util.Date

data class User(
    var userId: String,
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    var username: String,
    var birthDate: Date?,
    var address: String?,
    var country: String?,
    var phoneNumber: String?,
    var acceptEmails: Boolean,
    val trips: List<Trip>?,
    val imageURL: String?
)