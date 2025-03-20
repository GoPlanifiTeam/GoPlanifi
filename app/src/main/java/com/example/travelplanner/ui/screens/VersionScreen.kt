package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelplanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionScreen(
    navController: NavController,
) {
    // Hardcoded version information
    val versionInfo = VersionInfo(
        appName = "Travel Planner",
        version = "1.0.0",
        releaseDate = "2025-03-20",
        changelog = listOf(
            "Initial release",
            "Added trip management",
            "User authentication support"
        )
    )

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.versionScreen), navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display version information
            Text(versionInfo.appName, style = MaterialTheme.typography.headlineMedium)
            Text("Version: ${versionInfo.version}")
            Text("Release Date: ${versionInfo.releaseDate}")

            Divider(thickness = 1.dp)
            Text("Changelog:")
            versionInfo.changelog.forEach { change ->
                Text("- $change")
            }
        }
    }
}

// Data class to represent version information
data class VersionInfo(
    val appName: String,
    val version: String,
    val releaseDate: String,
    val changelog: List<String>
)
