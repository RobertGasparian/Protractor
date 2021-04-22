package com.ekik.protractor.usecases

class CalibrationUseCaseImpl(
    override val calibrationAnchor: Double = CalibrationUseCase.DEFAULT_CALIBRATION_ANCHOR
): CalibrationUseCase {
    override var calibration: Double = 0.0
    set(value) {
        field = value - calibrationAnchor
    }
}