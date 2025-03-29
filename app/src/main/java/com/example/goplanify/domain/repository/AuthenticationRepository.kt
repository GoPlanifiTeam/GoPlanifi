package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Authentication

interface AuthenticationRepository {
    suspend fun getAuthByUserId(userId: String): Authentication?
    suspend fun incrementLoginError(userId: String)
    suspend fun resetLoginError(userId: String)
}
