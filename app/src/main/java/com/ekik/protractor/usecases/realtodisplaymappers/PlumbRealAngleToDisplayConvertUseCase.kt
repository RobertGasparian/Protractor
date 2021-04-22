package com.ekik.protractor.usecases.realtodisplaymappers

import com.ekik.protractor.round
import kotlin.math.abs

class PlumbRealAngleToDisplayConvertUseCase(
    private val correctionUseCase: ConvertUseCase<Double, Double>
): ConvertUseCase<Double, String> {
    override fun convert(value: Double): String {
        return abs(correctionUseCase.convert(value)).round(1).toString() + " °"
    }
}