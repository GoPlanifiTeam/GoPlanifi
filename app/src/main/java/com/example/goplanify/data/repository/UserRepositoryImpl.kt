package com.example.goplanify.data.repository

import android.util.Log
import com.example.goplanify.data.local.dao.TripDao
import com.example.goplanify.data.local.dao.UserDao
import com.example.goplanify.data.local.mapper.toDomain
import com.example.goplanify.data.local.mapper.toEntity
import com.example.goplanify.domain.model.User
import com.example.goplanify.domain.repository.UserRepository
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dao: UserDao,
    private val tripDao: TripDao
) : UserRepository {

    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    )

    override suspend fun addUser(user: User): Result<User> {
        return try {
            when {
                user.userId.isBlank() -> Result.failure(IllegalArgumentException("User ID cannot be empty"))
                user.email.isBlank() -> Result.failure(IllegalArgumentException("Email cannot be empty"))
                !isValidEmail(user.email) -> Result.failure(IllegalArgumentException("Invalid email format"))
                user.password.length < 8 -> Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
                user.firstName.isBlank() -> Result.failure(IllegalArgumentException("First name cannot be empty"))
                user.lastName.isBlank() -> Result.failure(IllegalArgumentException("Last name cannot be empty"))
                getUserByEmail(user.email) != null -> Result.failure(IllegalArgumentException("Email already registered"))
                getUserById(user.userId) != null -> Result.failure(IllegalArgumentException("User ID already exists"))
                else -> {
                    dao.insertUser(user.toEntity())
                    Log.d("DB-User", "User inserted: ${user.userId}")
                    Result.success(user)
                }
            }
        } catch (e: Exception) {
            Log.e("DB-User", "Error inserting user ${user.userId}", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val userEntity = dao.getUserById(userId)
            val tripEntities = tripDao.getTripsByUser(userId)
            val trips = tripEntities.map { it.toDomain(null, emptyList()) }
            val user = userEntity?.toDomain(trips)
            Log.d("DB-User", "Fetched user: $userId → $user")
            user
        } catch (e: Exception) {
            Log.e("DB-User", "Error fetching user by ID: $userId", e)
            null
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            val user = dao.getUserByEmail(email)?.toDomain()
            Log.d("DB-User", "Fetched user by email: $email → $user")
            user
        } catch (e: Exception) {
            Log.e("DB-User", "Error fetching user by email: $email", e)
            null
        }
    }

    override suspend fun updateUser(userId: String, updatedUser: User): Result<User> {
        return try {
            if (getUserById(userId) != null) {
                dao.updateUser(updatedUser.toEntity())
                Log.d("DB-User", "Updated user: $userId")
                Result.success(updatedUser)
            } else {
                Log.w("DB-User", "User not found: $userId")
                Result.failure(IllegalArgumentException("User not found"))
            }
        } catch (e: Exception) {
            Log.e("DB-User", "Error updating user: $userId", e)
            Result.failure(e)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return emailPattern.matcher(email).matches()
    }
}
