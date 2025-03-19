package com.example.travelplanner.ui.viewmodel
import androidx.lifecycle.ViewModel
import com.example.travelplanner.domain.model.User
import com.example.travelplanner.domain.repository.AuthenticationRepository

class AuthenticationViewModel : ViewModel() {
    private val repository = AuthenticationRepository()
    fun login(user: User) = repository.login(user)
    fun logout(user: User) = repository.logout(user)
    fun resetPassword(user: User) = repository.resetPassword(user)
}
