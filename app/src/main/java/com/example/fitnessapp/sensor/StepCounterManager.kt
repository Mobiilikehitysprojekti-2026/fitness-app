package com.example.fitnessapp.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Manages the step detector sensor and delivers a callback on each detected step.
 * Uses TYPE_STEP_DETECTOR which fires once per step, making session-relative
 * counting straightforward — no baseline subtraction required.
 */
class StepCounterManager(context: Context) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // TYPE_STEP_DETECTOR fires an event for each individual step
    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private var stepListener: SensorEventListener? = null

    /**
     * Starts step counting. [onStep] is invoked on the sensor thread for every step detected.
     */
    fun startStepCounting(onStep: () -> Unit) {
        stepListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    onStep()
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        stepSensor?.let {
            sensorManager.registerListener(
                stepListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    /** Stops step counting and unregisters the sensor listener. */
    fun stopStepCounting() {
        stepListener?.let { sensorManager.unregisterListener(it) }
        stepListener = null
    }

    /** Returns true if the device has a step detector sensor. */
    fun isStepSensorAvailable(): Boolean = stepSensor != null

    companion object {
        /** Average step length in metres used for distance estimation. */
        const val STEP_LENGTH_METERS = 0.74f
    }
}
