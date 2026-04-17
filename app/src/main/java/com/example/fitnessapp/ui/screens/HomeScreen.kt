package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fitnessapp.ui.components.MonthlyWorkoutsChart
import com.example.fitnessapp.ui.components.WorkoutsByTypeChart
import com.example.fitnessapp.ui.navigation.ROUTE_WORKOUT_DETAIL
import com.example.fitnessapp.viewmodel.WorkoutDataViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WorkoutDataViewModel
) {
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Workout Statistics", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            MonthlyWorkoutsChart(sessions = sessions)
        }

        item {
            WorkoutsByTypeChart(sessions = sessions)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Recent Workouts", style = MaterialTheme.typography.titleLarge)
        }

        if (sessions.isEmpty()) {
            item {
                Text(
                    text = "No workouts saved yet. Complete a workout to see it here!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(sessions.sortedByDescending { it.startTime }) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("${ROUTE_WORKOUT_DETAIL}/${session.id}")
                        }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = session.type, style = MaterialTheme.typography.titleMedium)
                        val date = remember(session.startTime) {
                            java.text.DateFormat.getDateTimeInstance().format(java.util.Date(session.startTime))
                        }
                        Text(text = date)
                        val durationMillis = session.endTime?.let { it - session.startTime } ?: 0
                        val minutes = durationMillis / 60000
                        val seconds = (durationMillis % 60000) / 1000
                        Text(text = "Duration: ${"%d:%02d".format(minutes, seconds)}")
                        Text(text = "Distance: ${"%.2f".format(session.distanceMeters / 1000f)} km")
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
