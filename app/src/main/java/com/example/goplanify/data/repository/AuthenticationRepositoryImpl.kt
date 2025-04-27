package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.AuthenticationDao
import com.example.goplanify.data.local.entity.AuthenticationEntity
import com.example.goplanify.domain.model.Authentication
import com.example.goplanify.domain.model.AuthState
import com.example.goplanify.domain.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepositoryImpl @Inject constructor(
    private val dao: AuthenticationDao,
    private val firebaseAuth: FirebaseAuth
) : AuthenticationRepository {

    private val TAG = "AuthenticationRepo"

    // Firebase auth state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState

    init {
        // Set up Firebase auth state listener
        firebaseAuth.addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                _authState.value = AuthState.Authenticated(auth.currentUser!!.uid)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }

        // Initial check
        checkAuthStatus()
    }

    override fun getFirebaseUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun checkAuthStatus() {
        if (firebaseAuth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated(firebaseAuth.currentUser?.uid ?: "")
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.delete().await()
                Log.d(TAG, "User account deleted from Firebase.")
                _authState.value = AuthState.Unauthenticated
                Result.success(Unit)
            } else {
                Log.e(TAG, "No current user to delete.")
                Result.failure(Exception("No user found to delete"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete user account", e)
            Result.failure(e)
        }
    }

    override fun getUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            if (email.isEmpty() || password.isEmpty()) {
                return Result.failure(Exception("Email or password can't be empty"))
            }

            _authState.value = AuthState.Loading

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                // Check email verification status
                if (!user.isEmailVerified) {
                    // Sign out and return email not verified state
                    firebaseAuth.signOut()
                    _authState.value = AuthState.EmailNotVerified
                    return Result.failure(Exception("Email not verified"))
                }

                // Reset login errors
                resetLoginError(user.uid)
                _authState.value = AuthState.Authenticated(user.uid)
                Result.success(user.uid)
            } else {
                _authState.value = AuthState.Error("Authentication failed")
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            Result.failure(e)
        }
    }

    override suspend fun signup(email: String, password: String): Result<Pair<String, Boolean>> {
        return try {
            if (email.isEmpty() || password.isEmpty()) {
                return Result.failure(Exception("Email or password can't be empty"))
            }

            _authState.value = AuthState.Loading

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                // Explicitly send verification email and capture the result
                val emailVerificationResult = sendEmailVerification()

                // Explicitly convert to a Boolean
                val emailSent = emailVerificationResult.isSuccess

                // IMPORTANT: Set state to EmailNotVerified since we know email was just created
                _authState.value = AuthState.EmailNotVerified

                // Initialize login errors tracking
                resetLoginError(userId)

                Result.success(Pair(userId, emailSent))
            } else {
                _authState.value = AuthState.Error("User creation failed")
                Result.failure(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "User creation failed")
            Result.failure(e)
        }
    }

    override fun signout() {
        firebaseAuth.signOut()
        // Auth state listener will update _authState
    }

    // Update the sendEmailVerification method to be more explicit
    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                Log.d(TAG, "Email verification sent successfully.")
                Result.success(Unit)
            } else {
                Log.e(TAG, "No current user to send verification email.")
                Result.failure(Exception("No user found to send verification email"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send verification email", e)
            Result.failure(e)
        }
    }

    // Add password reset functionality
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            if (email.isEmpty()) {
                return Result.failure(Exception("Email can't be empty"))
            }

            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent successfully to $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send password reset email to $email", e)
            Result.failure(e)
        }
    }

    override suspend fun checkEmailVerification(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    // Error tracking methods (maintained for compatibility)
    override suspend fun getAuthByUserId(userId: String): Authentication? {
        return try {
            val result = dao.getByUserId(userId)
            Log.d("DB-Auth", "Fetched auth for userId=$userId → $result")
            result?.let {
                Authentication(
                    userId = it.userId,
                    loginErrors = it.loginErrors
                )
            }
        } catch (e: Exception) {
            Log.e("DB-Auth", "Error fetching auth for userId=$userId", e)
            null
        }
    }

    override suspend fun incrementLoginError(userId: String) {
        try {
            val current = dao.getByUserId(userId)
            val updated = current?.copy(loginErrors = current.loginErrors + 1)
                ?: AuthenticationEntity(userId = userId, loginErrors = 1)
            dao.insertOrUpdate(updated)
            Log.d("DB-Auth", "Incremented login errors for userId=$userId to ${updated.loginErrors}")
        } catch (e: Exception) {
            Log.e("DB-Auth", "Error incrementing login errors for userId=$userId", e)
        }
    }

    override suspend fun resetLoginError(userId: String) {
        try {
            dao.insertOrUpdate(AuthenticationEntity(userId = userId, loginErrors = 0))
            Log.d("DB-Auth", "Reset login errors for userId=$userId to 0")
        } catch (e: Exception) {
            Log.e("DB-Auth", "Error resetting login errors for userId=$userId", e)
        }
    }
}