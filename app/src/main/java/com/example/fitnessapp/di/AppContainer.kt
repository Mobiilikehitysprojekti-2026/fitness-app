package com.example.fitnessapp.di

import android.content.Context
import com.example.fitnessapp.data.local.AppDatabase
import com.example.fitnessapp.data.repository.UserAccountRepository
import com.example.fitnessapp.data.repository.WorkoutSessionRepository
import com.example.fitnessapp.managers.UserPreferencesManager

/*
* AppContainer contains app dependencies such as database, repositories, managers (services)
* */
class AppContainer(context: Context) {

    // database
    private val database = AppDatabase.getDatabase(context)

    // DataStore
    val userPreferencesManager = UserPreferencesManager(context)
    // managers (services)


    // repositories
    val userAccountRepository = UserAccountRepository(database.userAccountDao, userPreferencesManager)
    val workoutSessionRepository = WorkoutSessionRepository(database.workoutSessionDao)
}