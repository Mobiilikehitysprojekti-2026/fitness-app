package com.example.fitnessapp.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fitnessapp.data.model.Coordinates
import com.example.fitnessapp.data.model.WorkoutType
import com.example.fitnessapp.ui.components.WorkoutMap
import com.example.fitnessapp.ui.navigation.BottomNavItem
import com.example.fitnessapp.viewmodel.WorkoutDataViewModel
import com.example.fitnessapp.viewmodel.WorkoutViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel
) {

    //----------------------------Permissions--------------------------------
    // Asking for permission (GPS, Step-count)
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // needed for step counter
    val activityRecognitionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted */ }

    // similar to useEffect in React Native
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // ask for permission
            activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    // if no permission then ask for it
    if (!permissionState.allPermissionsGranted) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Location permission needed")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    permissionState.launchMultiplePermissionRequest()
                }) {
                Text("Grant permission")
            }
        }
        return
    }

    //------------------------------------------------------------------------------------

    //----------------------State variables for the workoutViewModel----------------------
    val currentSession by workoutViewModel.currentSession.collectAsStateWithLifecycle()
    //val currentUserAccount by workoutViewModel.currentUserAccount.collectAsStateWithLifecycle()
    val isMoving by workoutViewModel.isMoving.collectAsStateWithLifecycle()
    val stepCount by workoutViewModel.stepCount.collectAsStateWithLifecycle()
    val totalDistance by workoutViewModel.totalDistance.collectAsStateWithLifecycle()
    val selectedWorkout by workoutViewModel.selectedWorkout.collectAsStateWithLifecycle()
    //val pacePerMinute by workoutViewModel.pacePerMinute.collectAsStateWithLifecycle()
    val routePoints by workoutViewModel.routePoints.collectAsStateWithLifecycle()
    val currentLocation by workoutViewModel.currentLocation.collectAsStateWithLifecycle()
    val currentCoords = currentLocation?.let { Coordinates(it.latitude, it.longitude) }
    //------------------------------------------------------------------------------------
    val stepsPerMinute = workoutViewModel.stepsPerMinute


    //-------------------------- Back navigation dialog ----------------------------------
    val showDialog by workoutViewModel.showExitDialog.collectAsState()
    // handle system back button
    BackHandler(enabled = currentSession != null) {
        workoutViewModel.requestExit()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { workoutViewModel.dismissExitDialog() },
            title = { Text("Quit Workout?") },
            text = { Text("Going back will stop and reset progress.") },
            confirmButton = {
                TextButton(onClick = {
                    workoutViewModel.dismissExitDialog()
                    workoutViewModel.resetWorkout()
                    navController.popBackStack()
                }) { Text("Quit") }
            },
            dismissButton = {
                TextButton(onClick = { workoutViewModel.dismissExitDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
    //------------------------------------------------------------------------------------




    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = "${selectedWorkout.replaceFirstChar { it.uppercase() }} Workout", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)

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
                Text(workoutViewModel.formatDisplayTime(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Distance and Pace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Distance", style = MaterialTheme.typography.labelLarge)
                Text(String.format("%.2f km", totalDistance/1000f), fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pace", style = MaterialTheme.typography.labelLarge)
                Text("${workoutViewModel.formatAveragePace()} /km", fontSize = 22.sp)
            }

        }

        // Steps
        if (selectedWorkout != WorkoutType.CYCLING) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Steps", style = MaterialTheme.typography.labelLarge)
                    Text(text = stepCount.toString(), fontSize = 22.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Steps/min", style = MaterialTheme.typography.labelLarge)
                    Text(stepsPerMinute.toString(), fontSize = 22.sp)
                }
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Button(

            onClick = {
                if (!isMoving) {
                    workoutViewModel.startWorkout()
                } else {
                    workoutViewModel.stopWorkout()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {

            Text(if (isMoving) "STOP" else "START", fontSize = 20.sp)
        }


        if (!isMoving && workoutViewModel.secondsElapsed > 0) {
            // save button
            Button(
                onClick = {
                    workoutViewModel.saveWorkoutToDB()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Workout")
            }

            // reset button
            OutlinedButton(
                onClick = { workoutViewModel.resetWorkout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset", fontSize = 16.sp)
            }
        }
    }

}


