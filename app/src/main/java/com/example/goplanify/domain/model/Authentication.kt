package com.example.goplanify.domain.model

data class Authentication(
    var userId: String,
    var loginErrors: Int,
)

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val userId: String) : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    object EmailNotVerified : AuthState()
}