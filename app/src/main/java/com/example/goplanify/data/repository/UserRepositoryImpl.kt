package com.example.goplanify.data.repository

import android.database.sqlite.SQLiteConstraintException
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

    private val phonePattern = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,3}[-\\s.]?[0-9]{4,10}$"
    )

    override suspend fun addUser(user: User): Result<User> {
        return try {
            // First check if user already exists
            val existingUser = getUserById(user.userId)

            if (existingUser != null) {
                // User already exists, return success with existing user
                Log.d("DB-User", "User already exists: ${user.userId}, skipping insertion")
                return Result.success(existingUser)
            }

            // Handle phone number validation separately
            val phoneNumber = user.phoneNumber
            if (phoneNumber != null && !isValidPhoneNumber(phoneNumber)) {
                return Result.failure(IllegalArgumentException("Invalid phone number format"))
            }

            // Rest of validation
            when {
                user.userId.isBlank() -> Result.failure(IllegalArgumentException("User ID cannot be empty"))
                user.email.isBlank() -> Result.failure(IllegalArgumentException("Email cannot be empty"))
                !isValidEmail(user.email) -> Result.failure(IllegalArgumentException("Invalid email format"))
                // Modified password validation to handle Firebase auth users
                user.password.length < 8 && !user.password.startsWith("********") ->
                    Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
                user.firstName.isBlank() -> Result.failure(IllegalArgumentException("First name cannot be empty"))
                user.lastName.isBlank() -> Result.failure(IllegalArgumentException("Last name cannot be empty"))
                user.username.isBlank() -> Result.failure(IllegalArgumentException("Username cannot be empty"))
                isUsernameTaken(user.username) -> Result.failure(IllegalArgumentException("Username is already taken"))
                getUserByEmail(user.email) != null && getUserByEmail(user.email)?.userId != user.userId ->
                    Result.failure(IllegalArgumentException("Email already registered"))
                else -> {
                    try {
                        dao.insertUser(user.toEntity())
                        Log.d("DB-User", "User inserted: ${user.userId}")
                        Result.success(user)
                    } catch (e: SQLiteConstraintException) {
                        // Handle the race condition - user was inserted by another thread
                        Log.d("DB-User", "Race condition detected during insert, fetching existing user: ${user.userId}")
                        val insertedUser = getUserById(user.userId)
                        if (insertedUser != null) {
                            Result.success(insertedUser)
                        } else {
                            // This shouldn't happen, but just in case
                            Result.failure(e)
                        }
                    }
                }
            }
        } catch (e: SQLiteConstraintException) {
            // Another thread inserted the user
            Log.d("DB-User", "Race condition detected, fetching existing user: ${user.userId}")
            val insertedUser = getUserById(user.userId)
            if (insertedUser != null) {
                Result.success(insertedUser)
            } else {
                Log.e("DB-User", "Error inserting user ${user.userId} due to constraint, but user not found", e)
                Result.failure(e)
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

    override suspend fun getUserByUsername(username: String): User? {
        return try {
            val user = dao.getUserByUsername(username)?.toDomain()
            Log.d("DB-User", "Fetched user by username: $username → $user")
            user
        } catch (e: Exception) {
            Log.e("DB-User", "Error fetching user by username: $username", e)
            null
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val isTaken = dao.isUsernameTaken(username)
            Log.d("DB-User", "Checking if username is taken: $username → $isTaken")
            isTaken
        } catch (e: Exception) {
            Log.e("DB-User", "Error checking if username is taken: $username", e)
            false
        }
    }

    override suspend fun updateUser(userId: String, updatedUser: User): Result<User> {
        return try {
            if (getUserById(userId) != null) {
                // Check if trying to update to a taken username
                if (updatedUser.username != getUserById(userId)?.username && isUsernameTaken(updatedUser.username)) {
                    return Result.failure(IllegalArgumentException("Username is already taken"))
                }

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

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phonePattern.matcher(phoneNumber).matches()
    }
}