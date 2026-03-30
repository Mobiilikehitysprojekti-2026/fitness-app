package com.example.fitnessapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitnessapp.data.local.converters.RouteConverters
import com.example.fitnessapp.data.local.dao.UserAccountDao
import com.example.fitnessapp.data.local.dao.WorkoutSessionDao
import com.example.fitnessapp.data.local.entity.UserAccount
import com.example.fitnessapp.data.local.entity.WorkoutSession


@Database(
    entities = [UserAccount::class, WorkoutSession::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RouteConverters::class)   // convert from/to json
abstract class AppDatabase : RoomDatabase() {
    abstract val userAccountDao: UserAccountDao
    abstract val workoutSessionDao: WorkoutSessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_db"
                )
                    .fallbackToDestructiveMigration()  // wipes the database if schema is changed
                    .build().also { INSTANCE = it }
            }
        }
    }
}
