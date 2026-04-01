package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.dao.WorkoutSessionDao
import com.example.fitnessapp.data.local.entity.WorkoutSession
import kotlinx.coroutines.flow.Flow

/**
 * This repository provides access to workoutSessions.
 */
class WorkoutSessionRepository(private val workoutSessionDao: WorkoutSessionDao) {
    // insert a new workoutSession
    suspend fun insertWorkoutSession(workoutSession: WorkoutSession): Long {
        return workoutSessionDao.insertWorkoutSession(workoutSession)
    }

    // delete a workoutSession by id
    suspend fun deleteWorkoutSession(id: String) {
        workoutSessionDao.deleteWorkoutSession(id)
    }

    // delete all workoutSessions of a user
    suspend fun deleteAllWorkoutSessionsOfUser(userId: String) {
        workoutSessionDao.deleteWorkoutSessionsOfUser(userId)
    }

    // get all workoutSessions of a user
    fun getAllWorkoutSessionsOfUser(userId: String): Flow<List<WorkoutSession>> {
        return workoutSessionDao.getAllWorkoutSessionsOfUser(userId)
    }
}