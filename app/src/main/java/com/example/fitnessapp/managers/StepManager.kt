package com.example.fitnessapp.managers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manages the step detector sensor and exposes the step count as a StateFlow.
 * Uses TYPE_STEP_DETECTOR which fires once per step
 */
class StepManager(context: Context) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    private var listener: SensorEventListener? = null

    fun startTracking() {
        listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                _stepCount.update { it + 1 }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        stepSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopTracking() {
        listener?.let { sensorManager.unregisterListener(it) }
        listener = null
    }

    fun reset() {
        _stepCount.value = 0
    }

    fun isAvailable(): Boolean = stepSensor != null
}
