package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.ui.navigation.ROUTE_WORKOUT_DETAIL
import com.example.fitnessapp.viewmodel.WorkoutListViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    workoutListViewModel: WorkoutListViewModel
) {
    val workouts = workoutListViewModel.workouts

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(text = "My Workouts", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (workouts.isEmpty()) {
            Text(
                text = "No workouts saved yet. Complete a workout to see it here!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn {
                items(workouts) { workout ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate("$ROUTE_WORKOUT_DETAIL/${workout.id}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = workout.type, style = MaterialTheme.typography.titleMedium)
                            Text(text = workout.date)
                            Text(text = "Duration: ${workout.durationSeconds / 60} min")
                            Text(text = "Distance: ${"%.2f".format(workout.distanceKm)} km")
                        }
                    }
                }
            }
        }
    }
}
