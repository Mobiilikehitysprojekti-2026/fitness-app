package com.example.fitnessapp.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.fitnessapp.data.model.Coordinates
import com.example.fitnessapp.managers.LocationManager
import kotlinx.coroutines.flow.StateFlow

class MapViewModel(
    private val locationManager: LocationManager
) : ViewModel() {

    // Route status and current location directly from the manager
    val routePoints: StateFlow<List<Coordinates>> = locationManager.routePoints
    val currentLocation: StateFlow<Location?> = locationManager.currentLocation

    fun startTracking() {
        locationManager.startTracking()
    }

    fun stopTracking() {
        locationManager.stopTracking()
    }

    fun resetMap() {
        locationManager.resetRoute()
    }

    fun calculateDistance(): Float {
        return locationManager.calculateTotalDistance()
    }
}
