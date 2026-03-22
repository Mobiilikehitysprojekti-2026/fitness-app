package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Exercise(val name: String, val duration: String, val calories: Int)

val mockExercises = listOf(
    Exercise("Running", "30 min", 320),
    Exercise("Cycling", "45 min", 410),
    Exercise("Running", "20 min", 210),
    Exercise("Cycling", "60 min", 550),
    Exercise("Running", "40 min", 400)
)

@Composable
fun HomeScreen(navController: NavController) {

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "My Exercises", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {

            items(mockExercises) { exercise ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Duration: ${exercise.duration}")
                        Text(text = "Calories: ${exercise.calories} kcal")
                    }
                }

            }
        }

    }
}
