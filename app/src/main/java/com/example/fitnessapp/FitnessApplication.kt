package com.example.fitnessapp

import android.app.Application
import com.example.fitnessapp.di.AppContainer

class FitnessApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}