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
import com.example.fitnessapp.ui.navigation.ROUTE_CYCLING_WORKOUT
import com.example.fitnessapp.ui.navigation.ROUTE_RUNNING_WORKOUT
import com.example.fitnessapp.ui.navigation.ROUTE_WALKING_WORKOUT

@Composable
fun WorkoutsScreen(navController: NavController) {

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


        Button(

            onClick = { navController.navigate(ROUTE_RUNNING_WORKOUT) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {

            Text("Running", fontSize = 22.sp)
        }

        Button(
            onClick = { navController.navigate(ROUTE_CYCLING_WORKOUT) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {

            Text("Cycling", fontSize = 22.sp)
        }

        Button(

            onClick = { navController.navigate(ROUTE_WALKING_WORKOUT) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Text("Walking", fontSize = 22.sp)
        }

    }
}
