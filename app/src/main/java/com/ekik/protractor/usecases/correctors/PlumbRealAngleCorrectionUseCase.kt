package com.ekik.protractor.usecases.correctors

import com.ekik.protractor.usecases.realtodisplaymappers.ConvertUseCase

class PlumbRealAngleCorrectionUseCase: ConvertUseCase<Double, Double> {
    override fun convert(value: Double): Double {
        return if (value < 0.0) {
            value + 180.0
        } else value
    }
}