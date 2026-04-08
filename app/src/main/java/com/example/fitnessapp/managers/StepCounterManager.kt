package com.example.fitnessapp.managers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


//const val STEP_LENGTH_METERS = 0.74f

class StepCounterManager(context: Context) {

    // managing the sensors
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager


    // step step detection sensor
    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private var stepListener: SensorEventListener? = null

    // start counting the steps
    fun startStepCounting(onStep: () -> Unit) {
        // step listener
        stepListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // one step detected
                if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                    onStep()    // call onStep when a step is detected
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                //
            }
        }

        // register the listener
        stepSensor?.let {
            val registered = sensorManager.registerListener(
                stepListener,   // listener
                it,     // sensor
                SensorManager.SENSOR_DELAY_NORMAL //sampling period
            )
            if (!registered) {
                //
            }
        }
    }

    // stop counting the steps
    fun stopStepCounting() {
        stepListener?.let {  sensorManager.unregisterListener(it) }
        stepListener = null
    }


    // stop
    fun stopAll() {
        stopStepCounting()
    }

    // check if the device has step sensor
    fun isStepSensorAvailable(): Boolean = stepSensor != null
}

