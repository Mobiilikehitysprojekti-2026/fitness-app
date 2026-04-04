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

class CyclingWorkoutViewModel(
    val userAccountRepository: UserAccountRepository
) : ViewModel() {

    var isActive by mutableStateOf(false)
        private set
    var elapsedSeconds by mutableStateOf(0)
        private set
    var distanceKm by mutableStateOf(0.0)
        private set
    var currentSpeedKmh by mutableStateOf(0.0)
        private set

    private var lastLat: Double? = null
    private var lastLon: Double? = null

    val avgSpeedKmh: Double
        get() = if (elapsedSeconds > 0) distanceKm / (elapsedSeconds / 3600.0) else 0.0

    fun start() {
        isActive = true
    }

    fun stop() {
        isActive = false
    }

    fun tick() {
        if (isActive) elapsedSeconds++
    }

    fun onLocationUpdate(lat: Double, lon: Double, speedMs: Float) {
        if (!isActive) return
        val prev = lastLat
        val prevLon = lastLon
        if (prev != null && prevLon != null) {
            distanceKm += haversineKm(prev, prevLon, lat, lon)
        }
        lastLat = lat
        lastLon = lon
        if (speedMs >= 0f) currentSpeedKmh = speedMs * 3.6
    }

    fun reset() {
        isActive = false
        elapsedSeconds = 0
        distanceKm = 0.0
        currentSpeedKmh = 0.0
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
