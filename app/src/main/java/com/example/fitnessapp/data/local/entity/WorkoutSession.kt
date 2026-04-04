package com.example.fitnessapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fitnessapp.data.model.Coordinates
import java.util.UUID

/*
* WorkoutSession is an entity class for the database.
* */
@Entity(
    tableName = "workout_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserAccount::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
    )
data class WorkoutSession(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,  // foreign key
    val type: String, // WorkoutType.kt
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val stepCount: Int = 0,
    val distanceMeters: Float = 0f,
    val routePoints: List<Coordinates>,    // routePoints is a list of Coordinates (latitude, longitude)
    val calories: Int = 0,
    val isActive: Boolean = true
)