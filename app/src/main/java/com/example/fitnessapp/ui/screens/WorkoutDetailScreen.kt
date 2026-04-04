package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.viewmodel.WorkoutListViewModel

@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: Int,
    workoutListViewModel: WorkoutListViewModel
) {
    val workout = workoutListViewModel.getById(workoutId)

    if (workout == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Workout not found.")
        }
        return
    }

    val paceStr = if (workout.distanceKm > 0.001) {
        val paceSeconds = (workout.durationSeconds / workout.distanceKm).toInt()
        val paceMin = paceSeconds / 60
        val paceSec = paceSeconds % 60
        "$paceMin:${paceSec.toString().padStart(2, '0')} /km"
    } else {
        "--:-- /km"
    }

    val durationStr = formatDetailTime(workout.durationSeconds)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "${workout.type} Workout", style = MaterialTheme.typography.headlineMedium)
        Text(text = workout.date, color = MaterialTheme.colorScheme.onSurfaceVariant)

        HorizontalDivider()

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatRow(label = "Duration", value = durationStr)
                StatRow(label = "Distance", value = "${"%.2f".format(workout.distanceKm)} km")
                StatRow(label = "Pace", value = paceStr)

                if (workout.type == "Running" || workout.type == "Walking") {
                    StatRow(label = "Total Steps", value = workout.steps.toString())
                }

                if (workout.type == "Cycling") {
                    StatRow(label = "Avg Power", value = "${workout.avgPowerW} W")
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, fontSize = 18.sp)
    }
}

private fun formatDetailTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
}
