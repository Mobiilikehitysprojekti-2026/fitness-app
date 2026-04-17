package com.example.fitnessapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.UserAccountRepository
import com.example.fitnessapp.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class ProfileUiState(
    val height: Float = 175f,
    val weight: Float = 75f,
    val caloriesLast7Days: List<Int> = emptyList(),
    val averageCalories: Float = 0f,
    val hasWorkoutsLast7Days: Boolean = false
) {
    val bmi: Float
        get() {
            val h = height / 100f
            return if (h > 0) weight / (h * h) else 0f
        }

    val bmiCategory: String
        get() = when {
            bmi < 18.5f -> "Underweight"
            bmi < 25f   -> "Normal"
            bmi < 30f   -> "Overweight"
            else       -> "Obese"
        }
}

class ProfileViewModel(
    private val userAccountRepository: UserAccountRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProfileUiState> = combine(
        userAccountRepository.currentUserAccount,
        userAccountRepository.currentUserAccount.flatMapLatest { user ->
            if (user != null) {
                workoutSessionRepository.getAllWorkoutSessionsOfUser(user.id)
            } else {
                flowOf(emptyList())
            }
        }
    ) { user, sessions ->
        val height = user?.height?.toFloat() ?: 175f
        val weight = user?.weight?.toFloat() ?: 75f
        
        // Calculate calories for last 7 days
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val todayStart = calendar.timeInMillis
        val oneDayMillis = TimeUnit.DAYS.toMillis(1)
        
        val dailyCalories = mutableListOf<Int>()
        var anyWorkout = false
        
        for (i in 6 downTo 0) {
            val dayStart = todayStart - (i * oneDayMillis)
            val dayEnd = dayStart + oneDayMillis
            
            val sessionsInDay = sessions.filter { 
                it.startTime in dayStart until dayEnd
            }
            
            if (sessionsInDay.isNotEmpty()) {
                anyWorkout = true
            }
            
            val caloriesForDay = sessionsInDay.sumOf { it.calories }
            dailyCalories.add(caloriesForDay)
        }
        
        val avg = if (dailyCalories.isNotEmpty()) dailyCalories.average().toFloat() else 0f
        
        ProfileUiState(
            height = height,
            weight = weight,
            caloriesLast7Days = dailyCalories,
            averageCalories = avg,
            hasWorkoutsLast7Days = anyWorkout
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun saveHeight(value: String) {
        value.toFloatOrNull()?.let { height ->
            viewModelScope.launch {
                userAccountRepository.updateHeight(height.toInt())
            }
        }
    }

    fun saveWeight(value: String) {
        value.toFloatOrNull()?.let { weight ->
            viewModelScope.launch {
                userAccountRepository.updateWeight(weight.toInt())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userAccountRepository.logout()
        }
    }
}