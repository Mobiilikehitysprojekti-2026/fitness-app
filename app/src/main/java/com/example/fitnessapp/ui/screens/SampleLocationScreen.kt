package com.example.fitnessapp.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.viewmodel.SampleWorkoutViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


// This is screen is for testing the location
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SampleLocationScreen(
    navController: NavController,
    sampleWorkoutViewModel: SampleWorkoutViewModel
) {

    // state variables
    val routePoints by sampleWorkoutViewModel.routePoints.collectAsState()
    val currentLocation by sampleWorkoutViewModel.currentLocation.collectAsState()

    ///////////////////////////////////////////////
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

    //////////////////////////////////////////////////////////////////////////

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text("Current Location: $currentLocation")

        Button(
            onClick = {sampleWorkoutViewModel.startTracking()}
        ) {
            Text("Start tracking")
        }
        Button(
            onClick = {sampleWorkoutViewModel.stopTracking()}
        ) {
            Text("Stop tracking")
        }
        Button(
            onClick = {sampleWorkoutViewModel.resetRoute()}
        ) {
            Text("Reset route")
        }


        Text(
            routePoints.toString()
        )
    }

}