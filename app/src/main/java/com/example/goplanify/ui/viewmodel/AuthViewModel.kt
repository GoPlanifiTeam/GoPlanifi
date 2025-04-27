package com.example.goplanify.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goplanify.domain.model.AuthState
import com.example.goplanify.domain.model.User
import com.example.goplanify.domain.repository.AuthenticationRepository
import com.example.goplanify.domain.repository.UserRepository
import com.google.firebase.appcheck.FirebaseAppCheck
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val authenticationRepository: AuthenticationRepository,
    val userRepository: UserRepository,
    @ApplicationContext private val application: Context
) : ViewModel() {

    // Forward the auth state from the repository
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _loginErrorCount = MutableStateFlow(0)
    val loginErrorCount: StateFlow<Int> = _loginErrorCount

    // Email verification state
    private val _verificationEmailSent = MutableStateFlow(false)
    val verificationEmailSent: StateFlow<Boolean> = _verificationEmailSent

    // Password reset state
    private val _passwordResetEmailSent = MutableStateFlow(false)
    val passwordResetEmailSent: StateFlow<Boolean> = _passwordResetEmailSent

    init {
        viewModelScope.launch {
            authenticationRepository.authState.collect { state ->
                _authState.value = state
                Log.d("AuthViewModel", "Auth state changed: $state")

                when (state) {
                    is AuthState.Authenticated -> {
                        loadUserProfile(state.userId)
                    }
                    is AuthState.Unauthenticated -> {
                        _currentUser.value = null
                        Log.d("AuthViewModel", "User is null - unauthenticated")
                    }
                    else -> { }
                }
            }
        }
    }

    private suspend fun loadUserProfile(userId: String) {
        Log.d("AuthViewModel", "Loading profile for user: $userId")
        var user = userRepository.getUserById(userId)

        if (user == null) {
            Log.w("AuthViewModel", "User is authenticated but no profile found in local DB")

            // Try to retrieve signup data from shared preferences first
            val savedUserData = retrieveUserSignupDataFromPrefs(userId)

            if (savedUserData != null) {
                Log.d("AuthViewModel", "Found saved signup data for user: $userId")
                // We have signup data for this user, use it to create the local profile
                user = savedUserData

                userRepository.addUser(user)
                    .onSuccess {
                        Log.d("AuthViewModel", "Successfully created local user from saved signup data")
                    }
                    .onFailure { error ->
                        Log.e("AuthViewModel", "Failed to save user from signup data: ${error.message}")
                    }
            } else {
                // No saved signup data, use Firebase info as fallback
                val firebaseUser = authenticationRepository.getFirebaseUser()
                if (firebaseUser != null) {
                    Log.d("AuthViewModel", "Creating local user record from Firebase user")

                    // Get the display name and parse it for first/last name
                    val displayName = firebaseUser.displayName ?: ""
                    val nameParts = displayName.split(" ")

                    // Handle empty display name by using defaults
                    val firstName = if (nameParts.isNotEmpty() && nameParts[0].isNotBlank()) {
                        nameParts[0]
                    } else {
                        "User" // Default first name if not available
                    }

                    val lastName = if (nameParts.size > 1 && nameParts.last().isNotBlank()) {
                        nameParts.last()
                    } else {
                        userId.take(5) // Use part of the userId as a default last name
                    }

                    // Generate a unique username from userId (take first 8 chars)
                    val username = userId.take(8)

                    // Check if the username is already taken
                    if (userRepository.isUsernameTaken(username)) {
                        // If taken, append some random chars to make it unique
                        val uniqueUsername = "$username${(1000..9999).random()}"
                        Log.d("AuthViewModel", "Username $username was taken, using $uniqueUsername instead")

                        // Create user with secure placeholder password and valid fields
                        user = User(
                            userId = userId,
                            email = firebaseUser.email ?: "",
                            password = "********", // Secure placeholder, not actual password
                            firstName = firstName,
                            lastName = lastName,
                            username = uniqueUsername,
                            birthDate = null,
                            address = null,
                            country = null,
                            phoneNumber = null,
                            acceptEmails = false,
                            trips = null,
                            imageURL = firebaseUser.photoUrl?.toString()
                        )
                    } else {
                        // Create user with secure placeholder password and valid fields
                        user = User(
                            userId = userId,
                            email = firebaseUser.email ?: "",
                            password = "********", // Secure placeholder, not actual password
                            firstName = firstName,
                            lastName = lastName,
                            username = username,
                            birthDate = null,
                            address = null,
                            country = null,
                            phoneNumber = null,
                            acceptEmails = false,
                            trips = null,
                            imageURL = firebaseUser.photoUrl?.toString()
                        )
                    }

                    Log.d("AuthViewModel", "Attempting to save user with firstName=$firstName, lastName=$lastName, username=${user?.username}")

                    userRepository.addUser(user)
                        .onSuccess {
                            Log.d("AuthViewModel", "Successfully created local user from Firebase data")
                        }
                        .onFailure { error ->
                            // In case of failure, try to fetch the user one more time
                            val retryUser = userRepository.getUserById(userId)
                            if (retryUser != null) {
                                Log.d("AuthViewModel", "User was created by another thread, using that instead")
                                user = retryUser
                            } else {
                                Log.e("AuthViewModel", "Failed to save Firebase user to local DB: ${error.message}")
                            }
                        }
                }
            }
        }

        // Set the current user, whether from DB or newly created
        _currentUser.value = user
    }

    // Function to retrieve saved signup data
    private fun retrieveUserSignupDataFromPrefs(userId: String): User? {
        val sharedPreferences = application.getSharedPreferences("user_signup_data", Context.MODE_PRIVATE)

        val savedUserId = sharedPreferences.getString("last_signup_userId", "")

        // Only use the saved data if it belongs to the current user
        if (savedUserId != userId) {
            return null
        }

        val email = sharedPreferences.getString("last_signup_email", "") ?: ""
        val firstName = sharedPreferences.getString("last_signup_firstName", "") ?: ""
        val lastName = sharedPreferences.getString("last_signup_lastName", "") ?: ""
        val username = sharedPreferences.getString("last_signup_username", "") ?: ""
        val birthDateLong = sharedPreferences.getLong("last_signup_birthDate", -1)
        val birthDate = if (birthDateLong != -1L) Date(birthDateLong) else null
        val address = sharedPreferences.getString("last_signup_address", null)
        val country = sharedPreferences.getString("last_signup_country", null)
        val phoneNumber = sharedPreferences.getString("last_signup_phoneNumber", null)
        val acceptEmails = sharedPreferences.getBoolean("last_signup_acceptEmails", false)

        // Make sure we have the essential fields
        if (userId.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            return null
        }

        return User(
            userId = userId,
            email = email,
            password = "********", // Secure placeholder
            firstName = firstName,
            lastName = lastName,
            username = username,
            birthDate = birthDate,
            address = address,
            country = country,
            phoneNumber = phoneNumber,
            acceptEmails = acceptEmails,
            trips = null,
            imageURL = null
        )
    }

    fun getCurrentUser(): User? = _currentUser.value

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    suspend fun getUserById(userId: String): User? {
        return userRepository.getUserById(userId)
    }

    suspend fun isEmailVerified(): Boolean {
        return authenticationRepository.checkEmailVerification()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authenticationRepository.login(email, password)
                .onFailure { exception ->
                    // Existing error tracking logic
                    val user = userRepository.getUserByEmail(email)
                    if (user != null) {
                        authenticationRepository.incrementLoginError(user.userId)
                        val auth = authenticationRepository.getAuthByUserId(user.userId)
                        _loginErrorCount.value = auth?.loginErrors ?: 1
                    }
                }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            authenticationRepository.sendEmailVerification()
                .onSuccess {
                    _verificationEmailSent.value = true
                }
                .onFailure {
                    _authState.value = AuthState.Error("Failed to send verification email")
                }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            authenticationRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _passwordResetEmailSent.value = true
                    _authState.value = AuthState.Unauthenticated
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error("Failed to send password reset email: ${error.message}")
                }
        }
    }

    fun resetVerificationState() {
        _verificationEmailSent.value = false
    }

    fun resetPasswordResetState() {
        _passwordResetEmailSent.value = false
    }


    fun signup(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String,
        birthDate: Date?,
        address: String?,
        country: String?,
        phoneNumber: String?,
        acceptEmails: Boolean
    ) {
        viewModelScope.launch {
            try {
                // First check if username is already taken
                if (userRepository.isUsernameTaken(username)) {
                    _authState.value = AuthState.Error("Username is already taken")
                    return@launch
                }

                authenticationRepository.signup(email, password)
                    .onSuccess { (userId, emailSent) ->
                        // Explicitly convert to Boolean if needed
                        val isEmailSent = emailSent == true

                        // Check if user already exists in local DB
                        val existingUser = userRepository.getUserById(userId)

                        if (existingUser == null) {
                            // Create a user in the local database with ALL the information from signup
                            val newUser = User(
                                userId = userId,
                                email = email,
                                password = "********", // Secure placeholder, not the actual password
                                firstName = firstName,
                                lastName = lastName,
                                username = username,
                                birthDate = birthDate,
                                address = address,
                                country = country,
                                phoneNumber = phoneNumber,
                                acceptEmails = acceptEmails,
                                trips = null,
                                imageURL = null
                            )

                            // Store this newly created user in a shared preference or similar
                            // to retrieve it later if needed
                            saveUserSignupDataToPrefs(newUser)

                            try {
                                val result = userRepository.addUser(newUser)
                                result.onSuccess {
                                    Log.d("AuthViewModel", "User added to local DB: $userId with data from signup form")
                                    _currentUser.value = it
                                }
                                result.onFailure { error ->
                                    // In case of failure, check if user was created by another thread
                                    val retryUser = userRepository.getUserById(userId)
                                    if (retryUser != null) {
                                        Log.d("AuthViewModel", "User was created by another thread, using that instead")
                                        _currentUser.value = retryUser
                                    } else {
                                        Log.e("AuthViewModel", "Error adding user to DB: ${error.message}")
                                        _authState.value = AuthState.Error(error.message ?: "Error creating user profile")
                                        // Rollback Firebase account creation
                                        authenticationRepository.deleteAccount()
                                        return@onFailure
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("AuthViewModel", "Exception adding user to DB: ${e.message}")
                                _authState.value = AuthState.Error(e.message ?: "Error creating user profile")
                                // Rollback Firebase account creation
                                authenticationRepository.deleteAccount()
                                return@onSuccess
                            }
                        } else {
                            Log.d("AuthViewModel", "User already exists in local DB, using existing record")
                            _currentUser.value = existingUser
                        }

                        // Update email verification status with explicit Boolean conversion
                        _verificationEmailSent.value = isEmailSent

                        // IMPORTANT: Set state to EmailNotVerified instead of Authenticated
                        // This prevents NavGraph from thinking we're fully authenticated
                        _authState.value = AuthState.EmailNotVerified

                        // Sign out immediately to force proper re-authentication after verification
                        authenticationRepository.signout()
                    }
                    .onFailure { exception ->
                        // Handle signup failure
                        _authState.value = AuthState.Error(exception.message ?: "Signup failed")
                    }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }

    // Add this function to save signup data temporarily
    private fun saveUserSignupDataToPrefs(user: User) {
        val sharedPreferences = application.getSharedPreferences("user_signup_data", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("last_signup_userId", user.userId)
            putString("last_signup_email", user.email)
            putString("last_signup_firstName", user.firstName)
            putString("last_signup_lastName", user.lastName)
            putString("last_signup_username", user.username)
            putLong("last_signup_birthDate", user.birthDate?.time ?: -1)
            putString("last_signup_address", user.address)
            putString("last_signup_country", user.country)
            putString("last_signup_phoneNumber", user.phoneNumber)
            putBoolean("last_signup_acceptEmails", user.acceptEmails)
            apply()
        }
    }

    fun signout() {
        authenticationRepository.signout()
    }
}