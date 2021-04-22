package com.ekik.protractor.usecases

interface CalibrationUseCase {
    val calibrationAnchor: Double
    var calibration: Double
    fun resetCalibration() {
        calibration = calibrationAnchor
    }

    companion object {
        const val DEFAULT_CALIBRATION_ANCHOR = 90.0
    }
}