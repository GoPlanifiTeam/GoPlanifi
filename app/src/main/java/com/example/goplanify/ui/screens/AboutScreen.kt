package com.example.goplanify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goplanify.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.aboutScreen), navController) }, // ✅ Now using `CommonTopBar`
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.about_screen), // "About Screen"
                style = MaterialTheme.typography.headlineMedium
            )
            Text(text = stringResource(R.string.about_description)) // "Lorem ipsum dolor sit amet..."
            Text(text = stringResource(R.string.about_details)) // "Describe your app, company, etc."

            Divider(thickness = 1.dp)

            Text(text = "${stringResource(R.string.versionScreen)}: 1.0.0") // "Version: 1.0.0"
            Text(text = stringResource(R.string.about_contact)) // "Contact: contact@example.com"
        }
    }
}