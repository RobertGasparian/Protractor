package com.ekik.protractor.services

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.ekik.protractor.DoNothing

class SensorServiceImpl(
    private val sensorManager: SensorManager,
    private val sensor: Sensor
) : SensorService {

    private var listener: SensorListener? = null

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            listener?.invoke(event.values[0], event.values[1], event.values[2])
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            DoNothing
        }
    }

    override fun setListener(listener: SensorListener) {
        if (this.listener != null) {
            removeListener()
        }
        this.listener = listener
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun removeListener() {
        listener = null
        sensorManager.unregisterListener(sensorEventListener)
    }

}