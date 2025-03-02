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
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.profileScreen), navController) }, // ✅ Now using `CommonTopBar`
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.profile_screen), style = MaterialTheme.typography.headlineMedium) // "Profile Screen"
            Text(stringResource(R.string.profile_name)) // "Name: John Doe"
            Text(stringResource(R.string.profile_email)) // "Email: john.doe@example.com"
            Text(stringResource(R.string.profile_location)) // "Location: Madrid, Spain"
            Divider(thickness = 1.dp)
            Text(stringResource(R.string.profile_info)) // "Additional profile information, settings, photo, etc."
        }
    }
}
