package com.example.goplanify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.goplanify.R
import com.example.goplanify.domain.model.AuthState
import com.example.goplanify.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.observeAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val loginErrorCount by authViewModel.loginErrorCount.collectAsState()
    val context = LocalContext.current
    val passwordResetEmailSent by authViewModel.passwordResetEmailSent.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State for password reset dialog
    var showPasswordResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                // User is authenticated, navigate to home
                navController.navigate("Home") {
                    popUpTo("loginScreen") { inclusive = true }
                }
            }
            is AuthState.EmailNotVerified -> {
                isLoading = false
                errorMessage = "Please verify your email before logging in."
            }
            is AuthState.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is AuthState.Error -> {
                isLoading = false
                errorMessage = state.message
            }
            else -> {
                isLoading = false
            }
        }
    }

    // Handle password reset email sent status
    LaunchedEffect(passwordResetEmailSent) {
        if (passwordResetEmailSent) {
            Toast.makeText(
                context,
                "Password reset email sent! Please check your inbox.",
                Toast.LENGTH_LONG
            ).show()

            // Reset the state for future use
            authViewModel.resetPasswordResetState()
        }
    }

    // Display resend verification email button if needed
    if (authState is AuthState.EmailNotVerified) {
        TextButton(
            onClick = { authViewModel.resendVerificationEmail() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Resend Verification Email")
        }
    }

    // Also navigate when currentUser becomes available
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("Home") {
                popUpTo("loginScreen") { inclusive = true }
            }
        }
    }

    // Password Reset Dialog
    if (showPasswordResetDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordResetDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email address to receive a password reset link.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmail.isNotEmpty()) {
                            authViewModel.sendPasswordResetEmail(resetEmail)
                            showPasswordResetDialog = false
                        }
                    },
                    enabled = resetEmail.isNotEmpty()
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.profileScreen),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.default_user)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.default_pass)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = stringResource(R.string.mainApp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.navigate("signupScreen") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(text = "Create Account")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                resetEmail = email // Pre-populate with the current email if available
                showPasswordResetDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(text = "Forgot Password?")
        }

        // Show error message if any
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}