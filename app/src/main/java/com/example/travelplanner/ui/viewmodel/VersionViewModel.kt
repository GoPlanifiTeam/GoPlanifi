package com.example.travelplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Version UI State
data class VersionState(
    val appName: String = "MyApp",
    val version: String = "1.0.0",
    val releaseDate: String = "March 2025",
    val changelog: List<String> = listOf(
        "Added login screen",
        "Improved performance",
        "Bug fixes and stability improvements"
    )
)

class VersionViewModel : ViewModel() {
    private val _versionState = MutableStateFlow(VersionState())
    val versionState: StateFlow<VersionState> get() = _versionState
}