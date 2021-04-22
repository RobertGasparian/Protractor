package com.ekik.protractor.viewmodels

import android.graphics.PointF
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ekik.protractor.services.SensorService

abstract class HomeViewModel: ViewModel() {
    abstract val touchDisplayAngle: LiveData<String>
    abstract val plumbDisplayAngle: LiveData<String>
    abstract val touchRealAngle: LiveData<Float>
    abstract val plumbRealAngle: LiveData<Float>
    abstract val mode: LiveData<Mode>
    abstract val cameraHoldingMode: LiveData<HoldingMode>
    abstract val calibrationAction: LiveData<Double>
    abstract fun setTouchPoint(point: PointF)
    abstract fun setAnchorPoint(point: PointF)
    abstract fun setMode(mode: Mode)
    abstract fun setCalibration()
    abstract fun resetCalibration()
    abstract fun hold()
    abstract fun release()

    sealed class Mode {
        object Plumb : Mode()
        object Touch : Mode()
        object Camera : Mode()
    }

    enum class HoldingMode {
        HOLDING,
        FREE
    }
}

