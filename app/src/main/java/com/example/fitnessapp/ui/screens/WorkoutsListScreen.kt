package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.data.model.WorkoutType
import com.example.fitnessapp.ui.navigation.ROUTE_WORKOUT
import com.example.fitnessapp.viewmodel.WorkoutViewModel

@Composable
fun WorkoutsListScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel
    ) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Choose a Workout",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // running
        Button(
            onClick = {
                workoutViewModel.setWorkoutType(WorkoutType.RUNNING)
                navController.navigate(ROUTE_WORKOUT) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {

            Text("Running", fontSize = 22.sp)
        }

        // cycling
        Button(
            onClick = {
                workoutViewModel.setWorkoutType(WorkoutType.CYCLING)
                navController.navigate(ROUTE_WORKOUT) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {

            Text("Cycling", fontSize = 22.sp)
        }

        // walking
        Button(
            onClick = {
                workoutViewModel.setWorkoutType(WorkoutType.WALKING)
                navController.navigate(ROUTE_WORKOUT) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Text("Walking", fontSize = 22.sp)
        }

    }
}
