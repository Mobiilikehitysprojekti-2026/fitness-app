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
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.data.model.Coordinates
import com.example.fitnessapp.ui.components.WorkoutMap
import com.example.fitnessapp.viewmodel.WorkoutDataViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CyclingWorkoutScreen(
    navController: NavController,
    viewModel: WorkoutDataViewModel
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    var isRunning by remember { mutableStateOf(false) }

    var seconds by remember { mutableStateOf(0) }

    var currentPower by remember { mutableStateOf(0) }

    var totalPower by remember { mutableStateOf(0) }


    // Average cycling speed: 20 km/h
    val avgSpeedKmh = 20.0

    val distanceKm = seconds / 3600.0 * avgSpeedKmh

    val routePoints by viewModel.routePoints.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val currentCoords = currentLocation?.let { Coordinates(it.latitude, it.longitude) }

    // Pace in min/km
    val paceStr = if (distanceKm > 0.001) {

        val paceSeconds = (seconds / distanceKm).toInt()
        val paceMin = paceSeconds / 60
        val paceSec = paceSeconds % 60
        "$paceMin:${paceSec.toString().padStart(2, '0')} /km"
    } else {

        "--:-- /km"
    }

    // Average power in watts
    val avgPower = if (seconds > 0) totalPower / seconds else 0

    LaunchedEffect(isRunning) {
        if (isRunning) {
            viewModel.startTracking()
        } else {
            viewModel.stopTracking()
        }
        while (isRunning) {
            delay(1000L)
            seconds++
            // Simulate power output: recreational cyclist averages around 150W
            // Random variation between 120 and 200 watts to simulate real riding
            currentPower = 120 + Random.nextInt(0, 80)
            totalPower += currentPower
        }

    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Cycling Workout", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)

        // Map "Window"
        WorkoutMap(
            routePoints = routePoints,
            currentLocation = currentCoords,
            modifier = Modifier.fillMaxWidth()
        )

        // Big timer display
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Duration", style = MaterialTheme.typography.titleMedium)
                Text(formatCyclingTime(seconds), fontSize = 32.sp, fontWeight = FontWeight.Bold)
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

        // Power output
        Row(

            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Current Power", style = MaterialTheme.typography.labelLarge)
                Text(
                    if (isRunning && seconds > 0) "$currentPower W" else "-- W",
                    fontSize = 22.sp
                )

            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Avg Power", style = MaterialTheme.typography.labelLarge)
                Text(if (seconds > 0) "$avgPower W" else "-- W", fontSize = 22.sp)
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
                        type = "Cycling",
                        durationSeconds = seconds,
                        distanceKm = distanceKm,
                        avgPowerW = avgPower
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
                currentPower = 0
                totalPower = 0
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Reset", fontSize = 16.sp)
        }
    }

}

private fun formatCyclingTime(totalSeconds: Int): String {

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }

}
