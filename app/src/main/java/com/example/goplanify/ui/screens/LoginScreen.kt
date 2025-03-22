package com.example.goplanify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goplanify.domain.model.User
import com.example.goplanify.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goplanify.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // States for username, password, and the alert dialog
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showAlert by remember { mutableStateOf(false) }

    // Get default values from strings.xml
    val defaultUser = stringResource(id = R.string.default_user)
    val defaultPass = stringResource(id = R.string.default_pass)

    // Initialize the testUser in the ViewModel
    LaunchedEffect(Unit) {
        if (!authViewModel.isTestUserInitialized) {
            authViewModel.initializeTestUser(
                User(
                    userId = "testUser123",
                    email = "test@test.com", //Este es el default user
                    password = "defaultPass", // Esta es la contraseña
                    firstName = "Test",
                    lastName = "User",
                    trips = emptyList(),
                    imageURL = "https://example.com/user-avatar.png"
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.profileScreen), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.default_user)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.default_pass)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (authViewModel.validateLogin(username, password)) {
                    // Navigate to Home and remove Login from the back stack
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    showAlert = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.mainApp))
        }
    }

    // Show an alert dialog if the login fails
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text(stringResource(R.string.cancel)) },
            text = { Text(stringResource(R.string.about_contact)) },
            confirmButton = {
                Button(onClick = { showAlert = false }) {
                    Text(stringResource(R.string.add))
                }
            }
        )
    }
}
