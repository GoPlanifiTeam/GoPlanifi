package com.example.goplanify.data.repository
import android.util.Log
import com.example.goplanify.data.local.dao.AuthenticationDao
import com.example.goplanify.data.local.entity.AuthenticationEntity
import com.example.goplanify.domain.model.Authentication
import com.example.goplanify.domain.repository.AuthenticationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepositoryImpl @Inject constructor(
    private val dao: AuthenticationDao
) : AuthenticationRepository {

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