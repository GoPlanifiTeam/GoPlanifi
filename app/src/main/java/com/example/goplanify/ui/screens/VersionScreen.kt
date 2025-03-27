package com.example.goplanify.ui.screens

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
import com.example.goplanify.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionScreen(
    navController: NavController,
) {
    // Use string resources for version information
    val versionInfo = VersionInfo(
        appName = stringResource(R.string.app_name),
        version = stringResource(R.string.version_current),
        releaseDate = stringResource(R.string.version_release_date),
        changelog = listOf(
            stringResource(R.string.version_changelog_login),
            stringResource(R.string.version_changelog_performance)
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
            Text(versionInfo.version)
            Text(versionInfo.releaseDate)

            Divider(thickness = 1.dp)
            Text(stringResource(R.string.version_changelog))
            versionInfo.changelog.forEach { change ->
                Text(change)
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
