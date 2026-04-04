package com.example.fitnessapp.viewmodel

import android.location.Location
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.UserAccount
import com.example.fitnessapp.data.local.entity.WorkoutSession
import com.example.fitnessapp.data.model.Coordinates
import com.example.fitnessapp.data.model.WorkoutType
import com.example.fitnessapp.data.repository.UserAccountRepository
import com.example.fitnessapp.data.repository.WorkoutSessionRepository
import com.example.fitnessapp.managers.LocationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch



/*
* This viewmodel is just a sample. I created it to test the repositories.
* It shows how to use repositories.
* */
class SampleWorkoutViewModel(
    private val userAccountRepository: UserAccountRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val locationManager: LocationManager
): ViewModel() {
    // current user account
    val currentUserAccount: StateFlow<UserAccount?> = userAccountRepository.currentUserAccount

    // current session
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()

    private val _allSessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()
    // sample route
    //val routePoints: List<Coordinates> = listOf(Coordinates(40.7580, -73.9855), Coordinates(40.7739, -73.9709))


    // location and route
    val routePoints: StateFlow<List<Coordinates>> = locationManager.routePoints
    val currentLocation: StateFlow<Location?> = locationManager.currentLocation


    init {
        loadUser()
        //loadUserAccountFromDB()
        loadAllWorkoutsFromDB()
    }

    // load DataSTore
    private fun loadUser() {
        viewModelScope.launch {
            try {
                userAccountRepository.loadSavedUserAccount()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun addWorkoutToDB() {
        val userId = currentUserAccount.value?.id

        if (userId != null) {
            val session: WorkoutSession = WorkoutSession(type = WorkoutType.WALKING, userId = userId, routePoints = routePoints.value)
            _currentSession.value = session

            viewModelScope.launch {
                _currentSession.value?.let { session ->
                    workoutSessionRepository.insertWorkoutSession(session)
                }
            }
        }
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadAllWorkoutsFromDB() {
        viewModelScope.launch {
            currentUserAccount
                .flatMapLatest { user ->
                    if (user != null) {
                        workoutSessionRepository.getAllWorkoutSessionsOfUser(user.id)
                    } else {
                        // reset when user is logged out
                        flowOf(emptyList())
                    }
                }
                .collect { sessions ->
                    _allSessions.value = sessions
                }
        }
    }


    // testing if adding a  user works
    fun addUserAccountToDB() {
        val newUser: UserAccount = UserAccount(
            username = "test",
            password = "test",
            name = "Test Testi",
            age = 29,
            height = 175,
            weight = 80,
        )
        viewModelScope.launch {
            userAccountRepository.insertUserAccount(newUser)
            userAccountRepository.setCurrentUserAccount(newUser.id)
        }

    }

    // logout
    fun logout(){
        viewModelScope.launch {
            userAccountRepository.logout()
        }
    }

    /*
    fun loadUserAccountFromDB() {
        viewModelScope.launch {
            userAccountRepository.currentUserAccount.collect { userAccount ->
                _currentUserAccount.value = userAccount
            }
        }
    }

     */


    // functions for location
    fun startTracking() = locationManager.startTracking()
    fun stopTracking() = locationManager.stopTracking()
    fun resetRoute() = locationManager.resetRoute()

}