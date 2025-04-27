package com.example.goplanify.domain.repository

import com.example.goplanify.domain.model.Authentication
import com.example.goplanify.domain.model.AuthState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable

interface AuthenticationRepository {
    // Firebase Auth operations
    val authState: StateFlow<AuthState>
    fun checkAuthStatus()
    fun getUserId(): String?
    fun isUserAuthenticated(): Boolean
    fun getFirebaseUser(): FirebaseUser?
    suspend fun login(email: String, password: String): Result<String>
    suspend fun signup(email: String, password: String): Result<Pair<String, Serializable>>
    fun signout()
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun checkEmailVerification(): Boolean
    suspend fun getAuthByUserId(userId: String): Authentication?
    suspend fun incrementLoginError(userId: String)
    suspend fun resetLoginError(userId: String)
}