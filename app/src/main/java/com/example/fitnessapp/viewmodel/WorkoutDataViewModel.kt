package com.example.fitnessapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.WorkoutSession
import com.example.fitnessapp.data.repository.UserAccountRepository
import com.example.fitnessapp.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutDataViewModel(
    private val userAccountRepository: UserAccountRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val allSessions: StateFlow<List<WorkoutSession>> = userAccountRepository.currentUserAccount
        .flatMapLatest { user ->
            if (user != null) {
                workoutSessionRepository.getAllWorkoutSessionsOfUser(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveWorkout(
        type: String,
        durationSeconds: Int,
        distanceKm: Double,
        steps: Int = 0,
        avgPowerW: Int = 0,
        pacePerMinute: List<Float> = emptyList()
    ) {
        val user = userAccountRepository.currentUserAccount.value ?: return
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (durationSeconds * 1000L)
        
        val session = WorkoutSession(
            userId = user.id,
            type = type,
            startTime = startTime,
            endTime = endTime,
            stepCount = steps,
            distanceMeters = (distanceKm * 1000).toFloat(),
            routePoints = emptyList(),
            pacePerMinute = pacePerMinute,
            calories = calculateCalories(type, durationSeconds, distanceKm),
            isActive = false
        )
        
        viewModelScope.launch {
            workoutSessionRepository.insertWorkoutSession(session)
        }
    }

    suspend fun getWorkoutSessionById(id: String): WorkoutSession? {
        return workoutSessionRepository.getWorkoutSessionById(id)
    }

    private fun calculateCalories(type: String, durationSeconds: Int, distanceKm: Double): Int {
        return when (type.lowercase()) {
            "running" -> (distanceKm * 60).toInt()
            "cycling" -> (distanceKm * 30).toInt()
            "walking" -> (distanceKm * 40).toInt()
            else -> (durationSeconds / 60 * 5) // 5 kcal per min
        }
    }
}
