package com.example.fitnessapp.ui.navigation

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
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.di.AppContainer
import com.example.fitnessapp.repository.UserAccountRepository
import com.example.fitnessapp.ui.screens.CyclingWorkoutScreen
import com.example.fitnessapp.viewmodel.AuthViewModel
import com.example.fitnessapp.ui.screens.HomeScreen
import com.example.fitnessapp.ui.screens.LoginScreen
import com.example.fitnessapp.ui.screens.ProfileScreen
import com.example.fitnessapp.ui.screens.RunningWorkoutScreen
import com.example.fitnessapp.ui.screens.SettingsScreen
import com.example.fitnessapp.ui.screens.SignupScreen
import com.example.fitnessapp.ui.screens.WalkingWorkoutScreen
import com.example.fitnessapp.ui.screens.WorkoutDataScreen
import com.example.fitnessapp.ui.screens.WorkoutDetailScreen
import com.example.fitnessapp.ui.screens.WorkoutsScreen
import com.example.fitnessapp.viewmodel.CyclingWorkoutViewModel
import com.example.fitnessapp.viewmodel.ProfileViewModel
import com.example.fitnessapp.viewmodel.RunningWorkoutViewModel
import com.example.fitnessapp.viewmodel.WalkingWorkoutViewModel
import com.example.fitnessapp.viewmodel.WorkoutListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(container: AppContainer) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Initialize all ViewModels here
    val profileViewModel = viewModel { ProfileViewModel(UserAccountRepository) }
    val workoutListViewModel = viewModel { WorkoutListViewModel(UserAccountRepository) }
    val runningWorkoutViewModel = viewModel { RunningWorkoutViewModel(UserAccountRepository) }
    val walkingWorkoutViewModel = viewModel { WalkingWorkoutViewModel(UserAccountRepository) }
    val cyclingWorkoutViewModel = viewModel { CyclingWorkoutViewModel(UserAccountRepository) }
    val authViewModel = viewModel { AuthViewModel(container.userAccountRepository) }

    val screenTitle = when (currentRoute) {
        ROUTE_HOME                           -> "Home"
        ROUTE_PROFILE                        -> "Profile"
        ROUTE_SETTINGS                       -> "Settings"
        ROUTE_WORKOUT_DATA                   -> "Workout Data"
        ROUTE_WORKOUTS                       -> "Workouts"
        ROUTE_RUNNING_WORKOUT                -> "Running"
        ROUTE_CYCLING_WORKOUT                -> "Cycling"
        ROUTE_WALKING_WORKOUT                -> "Walking"
        "$ROUTE_WORKOUT_DETAIL/{workoutId}"  -> "Workout Details"
        else                                 -> null
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
            composable(ROUTE_HOME) {
                HomeScreen(navController, workoutListViewModel)
            }
            composable(ROUTE_LOGIN) {
                LoginScreen(navController, authViewModel)
            }
            composable(ROUTE_PROFILE) {
                ProfileScreen(navController, profileViewModel)
            }
            composable(ROUTE_SETTINGS) {
                SettingsScreen(navController)
            }
            composable(ROUTE_SIGNUP) {
                SignupScreen(navController, authViewModel)
            }
            composable(ROUTE_WORKOUT_DATA) {
                WorkoutDataScreen(navController)
            }
            composable(ROUTE_WORKOUTS) {
                WorkoutsScreen(navController)
            }
            composable(ROUTE_RUNNING_WORKOUT) {
                RunningWorkoutScreen(navController, runningWorkoutViewModel, workoutListViewModel)
            }
            composable(ROUTE_CYCLING_WORKOUT) {
                CyclingWorkoutScreen(navController, cyclingWorkoutViewModel, workoutListViewModel)
            }
            composable(ROUTE_WALKING_WORKOUT) {
                WalkingWorkoutScreen(navController, walkingWorkoutViewModel, workoutListViewModel)
            }
            composable(
                route = "$ROUTE_WORKOUT_DETAIL/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.IntType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: -1
                WorkoutDetailScreen(navController, workoutId, workoutListViewModel)
            }
        }
    }
}
