package com.example.fitnessapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnessapp.di.AppContainer
import com.example.fitnessapp.ui.screens.HomeScreen
import com.example.fitnessapp.ui.screens.LoginScreen
import com.example.fitnessapp.ui.screens.ProfileScreen
import com.example.fitnessapp.ui.screens.SettingsScreen
import com.example.fitnessapp.ui.screens.SignupScreen
import com.example.fitnessapp.ui.screens.WorkoutDataScreen
import com.example.fitnessapp.ui.screens.WorkoutsScreen
import com.example.fitnessapp.viewmodel.SampleWorkoutViewModel

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(container: AppContainer) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // view models
    // sampleWorkout viewmodel (for testing)
    val sampleWorkoutViewModel = remember {
        SampleWorkoutViewModel(
            container.userAccountRepository,
            container.workoutSessionRepository
        )
    }

    val screenTitle = when (currentRoute) {
        ROUTE_HOME         -> "Home"
        ROUTE_PROFILE      -> "Profile"
        ROUTE_SETTINGS     -> "Settings"
        ROUTE_WORKOUT_DATA -> "Workout Data"
        ROUTE_WORKOUTS     -> "Workouts"
        else               -> null
    }

    val bottomBarRoutes = setOf(ROUTE_HOME, ROUTE_WORKOUTS, ROUTE_PROFILE, ROUTE_SETTINGS)

    Scaffold(
        topBar = {
            screenTitle?.let {
                TopAppBar(
                    title = { Text(it) },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ROUTE_HOME)         { HomeScreen(navController, sampleWorkoutViewModel) }
            composable(ROUTE_LOGIN)        { LoginScreen(navController) }
            composable(ROUTE_PROFILE)      { ProfileScreen(navController) }
            composable(ROUTE_SETTINGS)     { SettingsScreen(navController) }
            composable(ROUTE_SIGNUP)       { SignupScreen(navController) }
            composable(ROUTE_WORKOUT_DATA) { WorkoutDataScreen(navController) }
            composable(ROUTE_WORKOUTS)     { WorkoutsScreen(navController) }
        }
    }
}