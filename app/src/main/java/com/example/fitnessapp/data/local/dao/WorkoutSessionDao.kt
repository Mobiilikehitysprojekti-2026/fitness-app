package com.example.fitnessapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnessapp.data.local.entity.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(workoutSession: WorkoutSession): Long

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    suspend fun deleteWorkoutSession(id: String)   // delete a workoutSession by id (changed to String as per entity)

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId")
    fun getAllWorkoutSessionsOfUser(userId: String): Flow<List<WorkoutSession>>


    @Query("DELETE FROM workout_sessions WHERE userId = :userId")
    suspend fun deleteWorkoutSessionsOfUser(userId: String)
}