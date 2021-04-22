package com.ekik.protractor.services

typealias SensorListener = (x: Float, y: Float, z: Float) -> Unit

interface SensorService {
    fun setListener(listener: SensorListener)
    fun removeListener()
}

