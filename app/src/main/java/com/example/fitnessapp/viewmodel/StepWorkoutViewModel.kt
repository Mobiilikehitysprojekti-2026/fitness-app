package com.example.fitnessapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.WorkoutSession
import com.example.fitnessapp.data.repository.UserAccountRepository
import com.example.fitnessapp.data.repository.WorkoutSessionRepository
import com.example.fitnessapp.managers.LocationManager
import com.example.fitnessapp.managers.StepManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StepWorkoutViewModel(
    private

    val locationManager: LocationManager,
    val stepManager: StepManager,
    val userAccountRepository: UserAccountRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {


    // True while a workout is running
    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    val totalSteps: StateFlow<Int> = stepManager.stepCount

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm.asStateFlow()


    val paceStr: String
        get() {
            val d = _distanceKm.value
            val s = _elapsedSeconds.value
            return if (d > 0.001 && s > 0) {
                val paceSeconds = (s / d).toInt()
                "${paceSeconds / 60}:${(paceSeconds % 60).toString().padStart(2, '0')} /km"
            } else "--:-- /km"
        }

    // Steps per minute based on elapsed time
    val cadence: Int
        get() {
            val s = _elapsedSeconds.value
            return if (s > 0) (stepMgr.stepCount.value.toDouble() / s * 60).toInt() else 0
        }

    private var workoutStartTime: Long = 0L
    private var timerJob: Job? = null
    private var locationJob: Job? = null

    fun registerSensors() {
        stepMgr.startTracking()
    }

    fun unregisterSensors() {
        stepMgr.stopTracking()
    }

    // Starts location tracking and updates distanceKm whenever new route points arrive
    fun registerLocation() {
        locationMgr.startTracking()
        locationJob = viewModelScope.launch {
            locationMgr.routePoints.collect {
                _distanceKm.value = (locationMgr.calculateTotalDistance() / 1000.0)
            }
        }
    }

    fun unregisterLocation() {
        locationJob?.cancel()
        locationMgr.stopTracking()
    }

    // Starts the 1-second timer and records the workout start time
    fun start() {
        workoutStartTime = System.currentTimeMillis()
        _isActive.value = true
        timerJob = viewModelScope.launch {
            while (_isActive.value) {
                delay(1000L)
                _elapsedSeconds.value++
            }
        }
    }

    fun stop() {
        _isActive.value = false
        timerJob?.cancel()
    }

    // Builds a WorkoutSession from current state
    suspend fun saveToDatabase(type: String) {
        val userId = userAccountRepository.currentUserAccount.value?.id ?: return
        val session = WorkoutSession(
            userId = userId,
            type = type,
            startTime = workoutStartTime,
            endTime = System.currentTimeMillis(),
            stepCount = stepMgr.stepCount.value,
            distanceMeters = locationMgr.calculateTotalDistance(),
            routePoints = locationMgr.routePoints.value,
            isActive = false
        )
        workoutSessionRepository.insertWorkoutSession(session)
    }

    // Stops the workout and clears all data
    fun reset() {
        stop()
        _elapsedSeconds.value = 0
        _distanceKm.value = 0.0
        stepMgr.reset()
        locationMgr.resetRoute()
    }

    // Clean up sensors and location when the ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        unregisterSensors()
        unregisterLocation()
    }
}
