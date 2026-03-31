package com.example.fitnessapp.viewmodel

import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.entity.UserAccount
import com.example.fitnessapp.data.local.entity.WorkoutSession
import com.example.fitnessapp.data.model.Coordinates
import com.example.fitnessapp.data.model.WorkoutType
import com.example.fitnessapp.data.repository.UserAccountRepository
import com.example.fitnessapp.data.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



/*
* This viewmodel is just a sample. I created it to test the repositories.
* It shows how to use repositories.
* */
class SampleWorkoutViewModel(
    private val userAccountRepository: UserAccountRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
): ViewModel() {
    //val db = AppDatabase.getDatabase(application)

    // current user account
    private val _currentUserAccount = MutableStateFlow<UserAccount?>(null)
    val currentUserAccount: StateFlow<UserAccount?> = _currentUserAccount.asStateFlow()

    // current session
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()

    private val _allSessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val allSessions: StateFlow<List<WorkoutSession>> = _allSessions.asStateFlow()
    // sample route
    val routePoints: List<Coordinates> = listOf(Coordinates(40.7580, -73.9855), Coordinates(40.7739, -73.9709))


    init {
        loadUserAccountFromDB()
        loadAllWorkoutsFromDB()
    }

    fun addWorkoutToDB() {
        val session: WorkoutSession = WorkoutSession(type = WorkoutType.WALKING, routePoints = routePoints)
        _currentSession.value = session

        viewModelScope.launch {
            _currentSession.value?.let { session ->
                //db.workoutDao.insertWorkoutSession(session)
                workoutSessionRepository.insertWorkoutSession(session)
            }
        }
    }



    fun loadAllWorkoutsFromDB() {
        viewModelScope.launch {
            /*
            db.workoutDao.getAllWorkoutSessions().collect { sessions ->
                _allSessions.value = sessions
            }

             */

            workoutSessionRepository.allWorkoutSessions.collect { sessions ->
                _allSessions.value = sessions
            }

        }
    }


    // testing if adding a  user works
    fun addUserAccountToDB() {
        val newUser: UserAccount = UserAccount(
            username = "test",
            name = "Test Testi",
            age = 29,
            height = 175,
            weight = 80,
        )
        viewModelScope.launch {
            userAccountRepository.insertUserAccount(newUser)
        }

    }

    fun loadUserAccountFromDB() {
        viewModelScope.launch {
            userAccountRepository.currentUserAccount.collect { userAccount ->
                _currentUserAccount.value = userAccount
            }
        }
    }

}