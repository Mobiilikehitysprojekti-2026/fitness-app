package com.example.fitnessapp.model

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CompletedWorkout(

    val id: Int,
    val type: String,           // "Running", "Cycling", "Walking"
    val durationSeconds: Int,
    val distanceKm: Double,     // currently mock speed * time; will be replaced with something else

    val date: String,
    val steps: Int = 0,         // for running/walking
    val avgPowerW: Int = 0      // for cycling
)

object WorkoutRepository {
    val workouts = mutableStateListOf<CompletedWorkout>()
    private var nextId = 1

    fun addWorkout(

        type: String,
        durationSeconds: Int,
        distanceKm: Double,
        steps: Int = 0,
        avgPowerW: Int = 0
    ) {

        val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
        workouts.add(
            0,
            CompletedWorkout(
                id = nextId++,
                type = type,
                durationSeconds = durationSeconds,
                distanceKm = distanceKm,
                date = dateStr,
                steps = steps,
                avgPowerW = avgPowerW
            )

        )
    }


    fun getById(id: Int): CompletedWorkout? = workouts.find { it.id == id }
}
