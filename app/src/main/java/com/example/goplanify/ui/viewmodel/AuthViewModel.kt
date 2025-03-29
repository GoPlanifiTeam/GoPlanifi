package com.example.goplanify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.User
import com.example.goplanify.domain.repository.AuthenticationRepository
import com.example.goplanify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _loginErrorCount = MutableStateFlow(0)
    val loginErrorCount: StateFlow<Int> = _loginErrorCount

    fun getCurrentUser(): User? = _currentUser.value

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    suspend fun getUserById(userId: String): User? {
        return userRepository.getUserById(userId)
    }

    fun validateLogin(email: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if (user?.password == password) {
                _currentUser.value = user
                authenticationRepository.resetLoginError(user.userId)
                _loginErrorCount.value = 0
            } else if (user != null) {
                authenticationRepository.incrementLoginError(user.userId)
                val current = authenticationRepository.getAuthByUserId(user.userId)
                _loginErrorCount.value = current?.loginErrors ?: 1
            }
        }
    }
}
