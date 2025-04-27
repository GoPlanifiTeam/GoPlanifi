package com.example.goplanify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.R
import com.example.goplanify.domain.model.AuthState
import com.example.goplanify.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.observeAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Check both authentication state and current user
    LaunchedEffect(authState) {
        // If explicitly unauthenticated, navigate to login
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("loginScreen") {
                popUpTo("profileScreen") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.profileScreen), navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentUser == null) {
                // Show loading state instead of immediately redirecting
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Display user info
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${currentUser?.firstName}",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = currentUser?.email ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign out")
                }
            }

            if (showSignOutDialog) {
                AlertDialog(
                    onDismissRequest = { showSignOutDialog = false },
                    title = { Text("Sign out") },
                    text = { Text("Are you sure you want to sign out?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                authViewModel.signout()
                                showSignOutDialog = false
                                // Navigation will happen in LaunchedEffect when authState changes
                            }
                        ) {
                            Text("Sign out")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showSignOutDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}