package com.ekik.protractor.usecases.processors

import com.ekik.protractor.usecases.realtodisplaymappers.ConvertUseCase

abstract class AngleProcessor {
    protected abstract val realAngleCorrector: ConvertUseCase<Double, Double>?
    protected abstract val displayAngleCorrector: ConvertUseCase<Double, String>

    fun correctRealAngle(angle: Double): Double = realAngleCorrector?.convert(angle) ?: angle
    fun correctDisplayAngle(angle: Double): String = displayAngleCorrector.convert(angle)
}