package com.ekik.protractor.usecases.correctors

import com.ekik.protractor.usecases.realtodisplaymappers.ConvertUseCase
import kotlin.math.abs

class PlumbCorrectionUseCase(
    private val minAngle: Double = MIN_ANGLE,
    private val maxAngle: Double = MAX_ANGLE,
    private val deviation: Double = DEVIATION
) : ConvertUseCase<Double, Double> {
    companion object {
        private const val MIN_ANGLE = -90.0
        private const val MAX_ANGLE = 90.0
        private const val DEVIATION = 90.0
    }

    override fun convert(value: Double): Double {
        var convertedAngle = value + deviation
        if (convertedAngle < minAngle) {
            convertedAngle = abs(convertedAngle + minAngle)
        } else if (convertedAngle > maxAngle) {
            convertedAngle = abs(convertedAngle - maxAngle)
        }
        return convertedAngle
    }
}