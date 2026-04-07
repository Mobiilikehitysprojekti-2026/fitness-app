package com.example.fitnessapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.fitnessapp.viewmodel.StepWorkoutViewModel
import com.example.fitnessapp.viewmodel.WorkoutListViewModel

@Composable
fun RunningWorkoutScreen(
    navController: NavController,
    viewModel: StepWorkoutViewModel,
    workoutListViewModel: WorkoutListViewModel
) {
    StepWorkoutScreen("Running", navController, viewModel, workoutListViewModel)
}
