package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelplanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionScreen(navController: NavController) {
    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.versionScreen), navController) }, // ✅ Now using `CommonTopBar`
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(R.string.version_screen), style = MaterialTheme.typography.headlineMedium) // "Version Screen"
            Text(text = stringResource(R.string.version_app_name)) // "Application: MyApp"
            Text(text = stringResource(R.string.version_current)) // "Current version: 1.0.0"
            Text(text = stringResource(R.string.version_release_date)) // "Release Date: February 2025"
            Divider(thickness = 1.dp)
            Text(text = stringResource(R.string.version_changelog)) // "Changelog:"
            Text(text = stringResource(R.string.version_changelog_login)) // "- Added login screen"
            Text(text = stringResource(R.string.version_changelog_performance)) // "- Performance improvements"
        }
    }
}
