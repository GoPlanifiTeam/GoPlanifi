package com.example.goplanify.ui.screens

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goplanify.R
import com.example.goplanify.domain.model.User
import com.example.goplanify.ui.viewmodel.AuthViewModel
import com.example.goplanify.ui.viewmodel.SettingsViewModel
import com.example.goplanify.ui.viewmodel.TripViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    tripViewModel: TripViewModel = hiltViewModel() // Asegúrate de tenerlo disponible
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Obtener usuario desde BD y establecer como usuario actual
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = authViewModel.getUserById("test123")
            if (user != null) {
                authViewModel.setCurrentUser(user)
                tripViewModel.getObjectUserTrips(user)
            } else {
                Log.e("SettingsScreen", "User not found in the database.")
            }
        }
    }

    val user = authViewModel.currentUser.collectAsState().value
    val settings by settingsViewModel.settingsState.collectAsState()

    Log.d("User Detected", "$user")

    LaunchedEffect(user) {
        user?.let { settingsViewModel.loadPreferences(it.userId) }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = stringResource(R.string.settingsScreen),
                navController = navController
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
            Text(text = stringResource(R.string.settings), style = MaterialTheme.typography.headlineSmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.enable_notifications))
                Switch(
                    checked = settings.notificationsEnabled,
                    onCheckedChange = { enabled ->
                        user?.let { settingsViewModel.toggleNotifications(it.userId, enabled) }
                    }
                )
            }

            LanguageSelector(
                context = context,
                onLanguageChange = { selectedLanguage ->
                    user?.let {
                        settingsViewModel.changeLanguage(it.userId, selectedLanguage, context)
                    }
                    navController.popBackStack()
                    navController.navigate("settings")
                }
            )
        }
    }
}


@Composable
fun LanguageSelector(context: Context, onLanguageChange: (String) -> Unit) {
    val languages = listOf("🇬🇧 English", "🇪🇸 Español", "🇫🇷 Français", "🇵🇹 Português")
    val languageCodes = listOf("en", "es", "fr", "pt")

    val savedLanguage = getSavedLanguage(context)
    var selectedLanguage by remember { mutableStateOf(savedLanguage) }

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = stringResource(R.string.select_language))
        Button(onClick = { expanded = true }) {
            Text(selectedLanguage.uppercase())
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        selectedLanguage = languageCodes[index]
                        onLanguageChange(languageCodes[index])
                        expanded = false
                    }
                )
            }
        }
    }
}

fun getSavedLanguage(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return sharedPreferences.getString("language", Locale.getDefault().language) ?: "en"
}

fun setLocale(context: Context, languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    val newContext = context.createConfigurationContext(config)

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit { putString("language", languageCode) }

    return newContext
}

