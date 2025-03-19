package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// Profile UI State
data class ProfileState(
    val name: String = "John Doe",
    val email: String = "john.doe@example.com",
    val location: String = "Madrid, Spain"
)

class ProfileViewModel : ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> get() = _profileState

    fun updateProfile(name: String, email: String, location: String) {
        _profileState.update { ProfileState(name, email, location) }
    }
}
