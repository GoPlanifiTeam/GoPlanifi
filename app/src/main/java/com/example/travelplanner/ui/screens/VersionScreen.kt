package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.travelplanner.R
import com.example.travelplanner.ui.viewmodel.VersionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionScreen(
    navController: NavController,
    versionViewModel: VersionViewModel = viewModel()
) {
    val versionInfo by versionViewModel.versionState.collectAsState()

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


