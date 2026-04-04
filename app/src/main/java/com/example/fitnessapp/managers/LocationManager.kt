package com.example.fitnessapp.managers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.example.fitnessapp.data.model.Coordinates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * LocationManager manages GPS sensor and captures GPS location
 */
class LocationManager(context: Context){

    // location manager
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager

    // current location
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    // route points
    private val _routePoints = MutableStateFlow<List<Coordinates>>(emptyList())
    val routePoints: StateFlow<List<Coordinates>> = _routePoints.asStateFlow()

    // locationListener captures gps changes
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _currentLocation.value = location
            // a new point to the route
            val newPoint = Coordinates(location.latitude, location.longitude)
            _routePoints.value += newPoint  // add a new point to the route
        }

        // for older android versions
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    // This function must be called when a workout session has started
    @SuppressLint("MissingPermission")
    fun startTracking() {
        try {
            val provider = when {
                // GPS location
                locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ->
                    android.location.LocationManager.GPS_PROVIDER
                // Network location
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER) ->
                    android.location.LocationManager.NETWORK_PROVIDER
                else -> return
            }

            // get gps updates
            locationManager.requestLocationUpdates(
                provider,
                5000L, // 5 sec
                5f, // 5 meters
                locationListener
            )
        } catch (_: SecurityException) {
            // permission is not granted
        }
    }

    fun stopTracking() {
        locationManager.removeUpdates(locationListener)
    }

    fun resetRoute() {
        _routePoints.value = emptyList()
    }

    // distance calculation
    fun calculateTotalDistance(): Float {
        val points = _routePoints.value
        if (points.size < 2) return 0f  // distance is 0

        var totalDistance = 0f
        for (i in 0 until points.size - 1) {
            val results = FloatArray(1)
            // calculate distance between 2 points
            Location.distanceBetween(
                points[i].latitude, points[i].longitude,
                points[i + 1].latitude, points[i + 1].longitude,
                results
            )
            totalDistance += results[0]
        }
        return totalDistance
    }
}