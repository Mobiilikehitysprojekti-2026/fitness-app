package com.example.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.fitnessapp.ui.navigation.AppNavigation
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.viewmodel.ThemeViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize OSMDroid configuration
        Configuration.getInstance().userAgentValue = packageName
        
        val container = (application as FitnessApplication).container
        val themeViewModel: ThemeViewModel by viewModels {
            ThemeViewModel.Factory(container.userPreferencesManager)
        }

        setContent {
            val isDarkThemeStored by themeViewModel.isDarkMode.collectAsState()
            val isDarkTheme = isDarkThemeStored ?: isSystemInDarkTheme()

            FitnessAppTheme(darkTheme = isDarkTheme) {
                AppNavigation(container)
            }
        }
    }
}
