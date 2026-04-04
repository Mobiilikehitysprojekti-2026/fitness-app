package com.example.fitnessapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.fitnessapp.viewmodel.CyclingWorkoutViewModel
import com.example.fitnessapp.viewmodel.WorkoutListViewModel
import kotlinx.coroutines.delay

@Composable
fun CyclingWorkoutScreen(
    navController: NavController,
    viewModel: CyclingWorkoutViewModel,
    workoutListViewModel: WorkoutListViewModel
) {
    val context = LocalContext.current
    val locationManager = remember { context.getSystemService(LocationManager::class.java) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // GPS location updates
    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@DisposableEffect onDispose { }
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                viewModel.onLocationUpdate(location.latitude, location.longitude, location.speed)
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
    val currentSpeedKmh = viewModel.currentSpeedKmh
    val avgSpeedKmh = viewModel.avgSpeedKmh

    val paceStr = if (distanceKm > 0.001 && seconds > 0) {
        val paceSeconds = (seconds / distanceKm).toInt()
        val paceMin = paceSeconds / 60
        val paceSec = paceSeconds % 60
        "$paceMin:${paceSec.toString().padStart(2, '0')} /km"
    } else {
        "--:-- /km"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Cycling Workout", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)

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
                Text("Speed", style = MaterialTheme.typography.labelLarge)
                Text(
                    if (viewModel.isActive && currentSpeedKmh > 0)
                        String.format("%.1f km/h", currentSpeedKmh)
                    else "-- km/h",
                    fontSize = 22.sp
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Avg Speed", style = MaterialTheme.typography.labelLarge)
                Text(
                    if (seconds > 0) String.format("%.1f km/h", avgSpeedKmh) else "-- km/h",
                    fontSize = 22.sp
                )
            }
        }

        if (!hasLocationPermission) {
            Text(
                "GPS distance and speed require Location permission.",
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
                        type = "Cycling",
                        durationSeconds = seconds,
                        distanceKm = distanceKm
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
