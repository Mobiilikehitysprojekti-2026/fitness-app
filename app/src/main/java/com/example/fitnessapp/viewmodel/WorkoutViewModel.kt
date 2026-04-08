package com.example.fitnessapp.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.UserAccount
import com.example.fitnessapp.data.local.entity.WorkoutSession
import com.example.fitnessapp.data.model.Coordinates
import com.example.fitnessapp.data.model.WorkoutType
import com.example.fitnessapp.data.repository.UserAccountRepository
import com.example.fitnessapp.data.repository.WorkoutSessionRepository
import com.example.fitnessapp.managers.LocationManager
import com.example.fitnessapp.managers.StepCounterManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val userAccountRepository: UserAccountRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val locationManager: LocationManager,
    private val stepCounterManager: StepCounterManager
) : ViewModel() {

    //---------------- Timer ---------------
    private var timerJob: Job? = null
    var secondsElapsed by mutableLongStateOf(0L)
        private set
    private var startTime = 0L
    //-------------------------------------


    // pace per minute
    private val _pacePerMinute = MutableStateFlow<List<Float>>(emptyList())
    val pacePerMinute: StateFlow<List<Float>> = _pacePerMinute.asStateFlow()


    // route point
    val routePoints: StateFlow<List<Coordinates>> = locationManager.routePoints
    val currentLocation: StateFlow<Location?> = locationManager.currentLocation


    // current session
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)  // mutable
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow() // read-only

    // user account
    val currentUserAccount: StateFlow<UserAccount?> = userAccountRepository.currentUserAccount

    // is the user running or walking
    private val _isMoving = MutableStateFlow(false)
    val isMoving: StateFlow<Boolean> = _isMoving.asStateFlow()

    // step count
    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    // total distance in meters
    private val _totalDistance = MutableStateFlow(0.0f)
    val totalDistance: StateFlow<Float> = _totalDistance.asStateFlow()

    // workout type
    private val _selectedWorkout = MutableStateFlow(WorkoutType.WALKING)
    val selectedWorkout: StateFlow<String> = _selectedWorkout.asStateFlow()


    // average pace
    val averagePace by derivedStateOf {
        val distanceKm = totalDistance.value / 1000.0
        val minutes = secondsElapsed / 60.0
        if (distanceKm > 0) minutes / distanceKm else 0.0
    }

    // step per minute
    val stepsPerMinute by derivedStateOf {
        val minutes = secondsElapsed / 60.0
        if (minutes > 0) {
            (_stepCount.value / minutes).toInt()
        } else {
            0
        }
    }


    init {
        viewModelScope.launch {
            // calculate the totalDistance when routPoints is updated
            locationManager.routePoints.collect { points ->
                calculateTotalDistance(points)
            }
        }
    }



    // set the workout type from a workout screen
    fun setWorkoutType(type: String) {
        _selectedWorkout.value = type
    }

    // start the workout
    fun startWorkout() {
        if (_isMoving.value) return

        val currentUserId = currentUserAccount.value?.id

        if (currentUserId != null) {
            _isMoving.value = true

            //--------- timer-------------------
            startTimer()
            //-----------------------------------

            // create session
            val session = WorkoutSession(userId = currentUserId, type = _selectedWorkout.value)    // entity/session
            _currentSession.value = session

            // cycling does not use the stepCounterManager
            if (_selectedWorkout.value != WorkoutType.CYCLING) {
                // start counting the steps
                stepCounterManager.startStepCounting {
                    // when a step detection occurs the step count is incremented
                    _stepCount.value += 1
                }
            } else {
                _stepCount.value = 0 // Ensure steps stay at zero for cycling
            }

            // start getting gps data
            locationManager.resetRoute()
            locationManager.startTracking()
        }
    }

    // stop the workout
    fun stopWorkout() {
        _isMoving.value = false
        timerJob?.cancel()

        stepCounterManager.stopStepCounting()
        locationManager.stopTracking()

        // update and insert all workoutSession data
        _currentSession.update {
            it?.copy(
                stepCount = stepCount.value,
                distanceMeters = totalDistance.value,
                routePoints = routePoints.value,
                pacePerMinute = pacePerMinute.value,
                calories = calculateCalories(),
                endTime = System.currentTimeMillis(),
                isActive = false
            )
        }
    }

    // save the workout to DB
    fun saveWorkoutToDB() {
        // save the session into the DB
        viewModelScope.launch {
            _currentSession.value?.let { session ->
                workoutSessionRepository.insertWorkoutSession(session)

                // reset the route
                locationManager.resetRoute()
            }
        }
    }

    // reset a workout session
    fun resetWorkout() {
        _isMoving.value = false
        timerJob?.cancel()
        timerJob = null

        stepCounterManager.stopStepCounting()
        locationManager.stopTracking()
        locationManager.resetRoute()

        // reset all states
        secondsElapsed = 0L
        startTime = 0L
        _stepCount.value = 0
        _totalDistance.value = 0.0f
        _pacePerMinute.value = emptyList()
        _currentSession.value = null
    }

    // resource cleanup
    override fun onCleared() {
        stopWorkout()
        super.onCleared()
        stepCounterManager.stopAll()
        locationManager.stopTracking()
        locationManager.resetRoute()
    }


    // helper function to calculate calories
    private fun calculateCalories(): Int {
        val distanceKm = _totalDistance.value / 1000f

        return when (selectedWorkout.value) {
            WorkoutType.WALKING -> (distanceKm * 40).toInt()
            WorkoutType.RUNNING -> (distanceKm * 60).toInt()
            WorkoutType.CYCLING -> (distanceKm * 30).toInt()
            else -> (secondsElapsed / 60 * 5).toInt() // 5 kcal per min
        }
    }


    // helper function to calculate total distance (in meters) from routPoints
    private fun calculateTotalDistance(points: List<Coordinates>) {
        var total = 0f
        // iterate through consecutive points
        points.windowed(2).forEach { (start, end) ->
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                start.latitude, start.longitude,
                end.latitude, end.longitude,
                results
            )
            total += results[0]
        }

        _totalDistance.value = total
    }


    // timer
    private var distanceAtLastSplit = 0f
    private fun startTimer() {
        startTime = System.currentTimeMillis() - (secondsElapsed * 1000)
        distanceAtLastSplit = _totalDistance.value

        timerJob = viewModelScope.launch {
            while (_isMoving.value) {
                secondsElapsed = (System.currentTimeMillis() - startTime) / 1000

                // pace in a minute
                if (secondsElapsed > 0 && secondsElapsed % 60 == 0L) {
                    val currentTotalDistance = _totalDistance.value
                    val distanceInThisMinute = currentTotalDistance - distanceAtLastSplit

                    val distanceKm = distanceInThisMinute / 1000f

                    if (distanceKm > 0.001) {
                        val minutePace = 1.0f / distanceKm
                        _pacePerMinute.update { it + minutePace }
                    } else {
                        _pacePerMinute.update { it + 0f } // No movement
                    }

                    distanceAtLastSplit = currentTotalDistance
                }
                delay(1000L)
            }
        }
    }


    // format the time (called from the UI)
    @SuppressLint("DefaultLocale")
    fun formatDisplayTime(): String {
        val totalSeconds = secondsElapsed
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            // Format as H:MM:SS
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            // Format as MM:SS
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    // formate the pace
    fun formatAveragePace(): String {
        val pace = averagePace
        if (pace <= 0 || pace.isInfinite()) return "0:00"
        val mins = pace.toInt()
        val secs = ((pace - mins) * 60).toInt().coerceIn(0, 59)
        return "%d:%02d".format(mins, secs)
    }
}
