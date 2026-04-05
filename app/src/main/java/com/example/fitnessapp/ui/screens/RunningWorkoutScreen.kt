package com.example.fitnessapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
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
import kotlinx.coroutines.delay

@Composable
fun RunningWorkoutScreen(
    navController: NavController,
    viewModel: StepWorkoutViewModel,
    workoutListViewModel: WorkoutListViewModel
) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }
    val locationManager = remember { context.getSystemService(LocationManager::class.java) }

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

    // Step counter sensor
    DisposableEffect(hasActivityPermission) {
        if (!hasActivityPermission) return@DisposableEffect onDispose { }
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                viewModel.onStepSensorEvent(event.values[0].toInt())
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        stepSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        onDispose { sensorManager.unregisterListener(listener) }
    }

    // GPS location updates
    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@DisposableEffect onDispose { }
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                viewModel.onLocationUpdate(location.latitude, location.longitude)
            }
        }
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000L,
                5f,
                locationListener,
                Looper.getMainLooper()
            )
        } catch (_: SecurityException) {}
        onDispose { locationManager.removeUpdates(locationListener) }
    }

    // Timer tick
    LaunchedEffect(viewModel.isActive) {
        while (viewModel.isActive) {
            delay(1000L)
            viewModel.tick()
        }
    }

    val seconds = viewModel.elapsedSeconds
    val distanceKm = viewModel.distanceKm
    val totalSteps = viewModel.totalSteps

    val paceStr = if (distanceKm > 0.001 && seconds > 0) {
        val paceSeconds = (seconds / distanceKm).toInt()
        val paceMin = paceSeconds / 60
        val paceSec = paceSeconds % 60
        "$paceMin:${paceSec.toString().padStart(2, '0')} /km"
    } else {
        "--:-- /km"
    }

    val cadence = if (seconds > 0) (totalSteps.toDouble() / seconds * 60).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Running Workout", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)

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
                Text(paceStr, fontSize = 22.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Steps", style = MaterialTheme.typography.labelLarge)
                Text(totalSteps.toString(), fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Steps/min", style = MaterialTheme.typography.labelLarge)
                Text(if (seconds > 0) cadence.toString() else "--", fontSize = 22.sp)
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
            onClick = { if (viewModel.isActive) viewModel.stop() else viewModel.start() },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text(if (viewModel.isActive) "STOP" else "START", fontSize = 20.sp)
        }

        if (!viewModel.isActive && seconds > 0) {
            Button(
                onClick = {
                    workoutListViewModel.addWorkout(
                        type = "Running",
                        durationSeconds = seconds,
                        distanceKm = distanceKm,
                        steps = totalSteps
                    )
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
