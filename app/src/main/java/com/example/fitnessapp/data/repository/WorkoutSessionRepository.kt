package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.WorkoutSessionDao
import com.example.fitnessapp.data.local.entity.WorkoutSession
import kotlinx.coroutines.flow.Flow

class WorkoutSessionRepository(private val workoutSessionDao: WorkoutSessionDao) {

    // allWorkoutSessions can be directly used in ViewModels
    val allWorkoutSessions: Flow<List<WorkoutSession>> = workoutSessionDao.getAllWorkoutSessions()

    // insert a new workoutSession
    suspend fun insertWorkoutSession(workoutSession: WorkoutSession): Long {
        return workoutSessionDao.insertWorkoutSession(workoutSession)
    }

    // delete a workoutSession by id
    suspend fun deleteWorkoutSession(id: String) {
        workoutSessionDao.deleteWorkoutSession(id)
    }

    // delete all workoutSessions
    suspend fun deleteAllWorkoutSessions() {
        workoutSessionDao.deleteAllWorkoutSessions()
    }
}