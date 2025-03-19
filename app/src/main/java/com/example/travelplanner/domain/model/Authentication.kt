package com.example.travelplanner.domain.model

data class Authentication(
    var userId: User,
    var loginErrors: Int,
)
