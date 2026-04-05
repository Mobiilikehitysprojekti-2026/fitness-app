package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fitnessapp.ui.components.MonthlyWorkoutsChart
import com.example.fitnessapp.ui.components.WorkoutsByTypeChart
import com.example.fitnessapp.viewmodel.WorkoutDataViewModel

@Composable
fun WorkoutDataScreen(
    navController: NavController,
    viewModel: WorkoutDataViewModel
) {
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Detailed Workout Statistics",
            style = MaterialTheme.typography.headlineMedium
        )

        MonthlyWorkoutsChart(sessions = sessions)

        WorkoutsByTypeChart(sessions = sessions)
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
