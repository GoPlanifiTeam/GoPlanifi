package com.example.goplanify.ui.screens

import androidx.lifecycle.ViewModel
import com.example.goplanify.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

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

    fun logout() {
        _currentUser.value = null
    }
}