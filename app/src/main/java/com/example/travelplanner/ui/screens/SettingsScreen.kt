package com.example.travelplanner.ui.screens

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.travelplanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(getSavedLanguage(context)) }
    var refresh by remember { mutableStateOf(false) } // 🔄 Triggers recomposition

    Scaffold(
        topBar = { CommonTopBar(title = stringResource(R.string.settingsScreen), navController) }, // ✅ Now using `CommonTopBar`
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.settings_screen), style = MaterialTheme.typography.headlineMedium) // "Settings Screen"
            Text(stringResource(R.string.settings_notifications)) // "Notification settings"

            // Language Selection Dropdown
            Text(stringResource(R.string.settings_language)) // "Language settings"
            LanguageSelector(context, navController) { newLanguage ->
                selectedLanguage = newLanguage
                setLocale(context, newLanguage)
                refresh = !refresh // 🔄 Force recomposition
                navController.navigate("settings") // Reload settings to update language instantly
            }

            Text(stringResource(R.string.settings_theme)) // "Dark or Light Theme"
            HorizontalDivider(thickness = 1.dp)
            Text(stringResource(R.string.settings_advanced)) // "More advanced settings..."
        }
    }
}

@Composable
fun LanguageSelector(context: Context, navController: NavController, onLanguageChange: (String) -> Unit) {
    val languages = listOf("English", "Español", "Français", "Português")
    val languageCodes = listOf("en", "es", "fr", "pt")
    var selectedLanguage by remember { mutableStateOf(getSavedLanguage(context)) }
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedLanguage.uppercase()) // Show selected language
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

fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Save selection to SharedPreferences
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("language", languageCode).apply()
}
