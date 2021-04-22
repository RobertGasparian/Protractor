package com.ekik.protractor.usecases.correctors

import com.ekik.protractor.usecases.realtodisplaymappers.ConvertUseCase

class TouchCorrectionUseCase(
    private val minAngle: Double = MIN_ANGLE,
    private val maxAngle: Double = MAX_ANGLE,
    private val deviation: Double = DEVIATION
): ConvertUseCase<Double, Double> {
    companion object {
        private const val MIN_ANGLE = 0.0
        private const val MAX_ANGLE = 180.0
        private const val DEVIATION = 0.0
    }

    override fun convert(value: Double): Double {
        val angle = value + deviation
        return when {
            angle > maxAngle -> {
                value - maxAngle
            }
            angle < minAngle -> {
                maxAngle - minAngle + angle - minAngle
            }
            else -> angle
        }
    }
}