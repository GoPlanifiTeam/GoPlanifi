package com.example.travelplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back Icon"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "About Screen", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
            Text(text = "Aquí describes la aplicación, tu empresa o lo que desees mostrar.")
            // Agrega más elementos básicos
            Divider(thickness = 1.dp)
            Text(text = "Versión: 1.0.0")
            Text(text = "Contacto: contact@example.com")
        }
    }
}