package com.example.goplanify.domain.model

data class Authentication(
    var userId: User,
    var loginErrors: Int,
)
