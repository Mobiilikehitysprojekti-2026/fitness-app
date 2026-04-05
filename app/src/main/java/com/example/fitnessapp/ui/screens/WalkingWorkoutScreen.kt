package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.viewmodel.WorkoutDataViewModel
import kotlinx.coroutines.delay

@Composable
fun WalkingWorkoutScreen(
    navController: NavController,
    viewModel: WorkoutDataViewModel
) {
    var isRunning by remember { mutableStateOf(false) }
    var seconds by remember { mutableStateOf(0) }

    // Average walking speed: 5 km/h (around 12:00 min/km pace)
    val avgSpeedKmh = 5.0
    val distanceKm = seconds / 3600.0 * avgSpeedKmh

    // Pace = time / distance
    val paceStr = if (distanceKm > 0.001) {

        val paceSeconds = (seconds / distanceKm).toInt()
        val paceMin = paceSeconds / 60
        val paceSec = paceSeconds % 60
        "$paceMin:${paceSec.toString().padStart(2, '0')} /km"
    } else {

        "--:-- /km"
    }

    // Step count: average walking cadence is about 100 steps/min at 5 km/h


    val stepsPerMin = 100
    val totalSteps = (seconds / 60.0 * stepsPerMin).toInt()

    // Cadence based on total steps and time
    val currentCadence = if (seconds > 0) (totalSteps.toDouble() / seconds * 60).toInt() else 0

    LaunchedEffect(isRunning) {

        while (isRunning) {
            delay(1000L)
            seconds++
        }

    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Walking Workout", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)

        // Big timer display
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Time", style = MaterialTheme.typography.labelLarge)
                Text(formatWalkingTime(seconds), fontSize = 52.sp)
            }

        }

        // Distance and Pace
        Row(


            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Distance", style = MaterialTheme.typography.labelLarge)
                Text(String.format("%.2f km", distanceKm), fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pace", style = MaterialTheme.typography.labelLarge)
                Text(paceStr, fontSize = 22.sp)
            }

        }

        // Steps
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Steps", style = MaterialTheme.typography.labelLarge)
                Text(totalSteps.toString(), fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Steps/min", style = MaterialTheme.typography.labelLarge)
                Text(if (seconds > 0) currentCadence.toString() else "--", fontSize = 22.sp)
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        Button(

            onClick = { isRunning = !isRunning },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {

            Text(if (isRunning) "STOP" else "START", fontSize = 20.sp)
        }

        if (!isRunning && seconds > 0) {
            Button(
                onClick = {
                    viewModel.saveWorkout(
                        type = "Walking",
                        durationSeconds = seconds,
                        distanceKm = distanceKm,
                        steps = totalSteps
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Workout")
            }

        }

        OutlinedButton(
            onClick = {
                isRunning = false
                seconds = 0
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }
    }

}

private fun formatWalkingTime(totalSeconds: Int): String {

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }

}
