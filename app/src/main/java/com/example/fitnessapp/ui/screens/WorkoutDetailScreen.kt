package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.data.local.entity.WorkoutSession
import com.example.fitnessapp.ui.components.WorkoutDetailChart
import com.example.fitnessapp.viewmodel.WorkoutDataViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: String,
    viewModel: WorkoutDataViewModel
) {
    var session by remember { mutableStateOf<WorkoutSession?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(workoutId) {
        session = viewModel.getWorkoutSessionById(workoutId)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val workout = session
    if (workout == null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Workout not found.")
        }
        return
    }

    val durationSeconds = workout.endTime?.let { (it - workout.startTime) / 1000 }?.toInt() ?: 0
    val distanceKm = workout.distanceMeters / 1000.0

    // Pace = durationSeconds / distanceKm -> seconds per km -> format as min:sec
    val paceStr = if (distanceKm > 0.001) {
        val paceSeconds = (durationSeconds / distanceKm).toInt()
        val paceMin = paceSeconds / 60
        val paceSec = paceSeconds % 60
        "$paceMin:${paceSec.toString().padStart(2, '0')} /km"
    } else {
        "--:-- /km"
    }

    val durationStr = formatDetailTime(durationSeconds)
    val dateStr = DateFormat.getDateTimeInstance().format(Date(workout.startTime))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(text = "${workout.type} Workout", style = MaterialTheme.typography.headlineMedium)
        Text(text = dateStr, color = MaterialTheme.colorScheme.onSurfaceVariant)

        HorizontalDivider()

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatRow(label = "Duration", value = durationStr)
                StatRow(label = "Distance", value = "${"%.2f".format(distanceKm)} km")
                StatRow(label = "Pace", value = paceStr)

                if (workout.type == "Running" || workout.type == "Walking") {
                    StatRow(label = "Total Steps", value = workout.stepCount.toString())
                }

                if (workout.type == "Cycling") {
                    // Note: WorkoutSession entity doesn't have avgPowerW yet, 
                    // if it's needed we'd need to add it to the entity.
                    // For now, I'll omit or use 0 if it was there before.
                    // StatRow(label = "Avg Power", value = "0 W")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pace chart for running/walking workouts
        if (workout.type == "Running" || workout.type == "Walking") {
            WorkoutDetailChart(
                paceData = workout.pacePerMinute,
                modifier = Modifier.fillMaxWidth()
            )
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
