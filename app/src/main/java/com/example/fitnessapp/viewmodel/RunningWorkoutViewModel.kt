package com.example.fitnessapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitnessapp.repository.UserAccountRepository
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class RunningWorkoutViewModel(
    val userAccountRepository: UserAccountRepository
) : ViewModel() {

    var isActive by mutableStateOf(false)
        private set
    var elapsedSeconds by mutableStateOf(0)
        private set
    var totalSteps by mutableStateOf(0)
        private set
    var distanceKm by mutableStateOf(0.0)
        private set

    private var stepBaseline = -1
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    fun start() {
        isActive = true
    }

    fun stop() {
        isActive = false
    }

    fun tick() {
        if (isActive) elapsedSeconds++
    }

    fun onStepSensorEvent(cumulativeSteps: Int) {
        if (!isActive) return
        if (stepBaseline < 0) stepBaseline = cumulativeSteps
        totalSteps = cumulativeSteps - stepBaseline
    }

    fun onLocationUpdate(lat: Double, lon: Double) {
        if (!isActive) return
        val prev = lastLat
        val prevLon = lastLon
        if (prev != null && prevLon != null) {
            distanceKm += haversineKm(prev, prevLon, lat, lon)
        }
        lastLat = lat
        lastLon = lon
    }

    fun reset() {
        isActive = false
        elapsedSeconds = 0
        totalSteps = 0
        distanceKm = 0.0
        stepBaseline = -1
        lastLat = null
        lastLon = null
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
