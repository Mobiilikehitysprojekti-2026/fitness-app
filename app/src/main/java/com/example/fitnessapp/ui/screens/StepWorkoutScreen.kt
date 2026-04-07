package com.example.fitnessapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.fitnessapp.viewmodel.StepWorkoutViewModel
import com.example.fitnessapp.viewmodel.WorkoutListViewModel
import kotlinx.coroutines.launch

@Composable
fun StepWorkoutScreen(
    workoutType: String,
    navController: NavController,
    viewModel: StepWorkoutViewModel,
    workoutListViewModel: WorkoutListViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasActivityPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasActivityPermission = permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        val needed = buildList {
            if (!hasActivityPermission) add(Manifest.permission.ACTIVITY_RECOGNITION)
            if (!hasLocationPermission) add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (needed.isNotEmpty()) permissionLauncher.launch(needed.toTypedArray())
    }

    DisposableEffect(hasActivityPermission) {
        if (hasActivityPermission) viewModel.registerSensors()
        onDispose { viewModel.unregisterSensors() }
    }

    DisposableEffect(hasLocationPermission) {
        if (hasLocationPermission) viewModel.registerLocation()
        onDispose { viewModel.unregisterLocation() }
    }

    val isActive by viewModel.isActive.collectAsState()
    val seconds by viewModel.elapsedSeconds.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val totalSteps by viewModel.totalSteps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("$workoutType Workout", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Time", style = MaterialTheme.typography.labelLarge)
                Text(formatTime(seconds), fontSize = 52.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Distance", style = MaterialTheme.typography.labelLarge)
                Text(String.format("%.2f km", distanceKm), fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pace", style = MaterialTheme.typography.labelLarge)
                Text(viewModel.paceStr, fontSize = 22.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Steps", style = MaterialTheme.typography.labelLarge)
                Text(totalSteps.toString(), fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Steps/min", style = MaterialTheme.typography.labelLarge)
                Text(if (seconds > 0) viewModel.cadence.toString() else "--", fontSize = 22.sp)
            }
        }

        if (!hasActivityPermission) {
            Text(
                "Step counter requires Activity Recognition permission.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        if (!hasLocationPermission) {
            Text(
                "GPS distance requires Location permission.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { if (isActive) viewModel.stop() else viewModel.start() },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text(if (isActive) "STOP" else "START", fontSize = 20.sp)
        }

        if (!isActive && seconds > 0) {
            Button(
                onClick = {
                    workoutListViewModel.addWorkout(
                        type = workoutType,
                        durationSeconds = seconds,
                        distanceKm = distanceKm,
                        steps = totalSteps
                    )
                    scope.launch { viewModel.saveToDatabase(workoutType) }
                    viewModel.reset()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Workout")
            }
        }

        OutlinedButton(
            onClick = { viewModel.reset() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
}
