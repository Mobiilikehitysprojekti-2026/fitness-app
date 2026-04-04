package com.example.fitnessapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.fitnessapp.repository.UserAccountRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CompletedWorkout(
    val id: Int,
    val type: String,
    val durationSeconds: Int,
    val distanceKm: Double,
    val date: String,
    val steps: Int = 0,
    val avgPowerW: Int = 0
)

class WorkoutListViewModel(
    val userAccountRepository: UserAccountRepository
) : ViewModel() {

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
