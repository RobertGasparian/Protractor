package com.ekik.protractor.usecases.processors

import com.ekik.protractor.usecases.realtodisplaymappers.ConvertUseCase

class AngleProcessorImpl(
    override val realAngleCorrector: ConvertUseCase<Double, Double>?,
    override val displayAngleCorrector: ConvertUseCase<Double, String>
) : AngleProcessor()