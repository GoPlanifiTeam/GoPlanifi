package com.example.goplanify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.goplanify.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(User(
        userId = "testUser123",
        email = "test@test.com",
        password = "defaultPass",
        firstName = "Test",
        lastName = "User",
        trips = null,
        imageURL = "https://example.com/user-avatar.png"
    ))
    val currentUser: StateFlow<User?> = _currentUser

    // Function to get the current user
    fun getCurrentUser(): User? {
        return _currentUser.value
    }

    private val _testUser = MutableStateFlow<User?>(null)
    val testUser: StateFlow<User?> = _testUser

    var isTestUserInitialized = false
        private set

    fun initializeTestUser(user: User) {
        _testUser.value = user
        isTestUserInitialized = true
    }

    fun validateLogin(username: String, password: String): Boolean {
        val testUser = _testUser.value ?: return false

        // Check if credentials match the test user
        if (testUser.email == username && testUser.password == password) {
            _currentUser.value = testUser
            return true
        }
        return false
    }
}