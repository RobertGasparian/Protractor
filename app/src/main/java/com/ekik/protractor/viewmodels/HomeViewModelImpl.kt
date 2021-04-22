package com.ekik.protractor.viewmodels

import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.ekik.protractor.services.SensorListener
import com.ekik.protractor.services.SensorService
import com.ekik.protractor.usecases.CalibrationUseCase
import com.ekik.protractor.usecases.RealAngleUseCase
import com.ekik.protractor.usecases.processors.AngleProcessor
import java.lang.RuntimeException

class HomeViewModelImpl(
    private val touchAngleProcessor: AngleProcessor,
    private val plumbAngleProcessor: AngleProcessor,
    private val cameraTouchAngleProcessor: AngleProcessor,
    private val cameraPlumbAngleProcessor: AngleProcessor,
    private val realAngleUseCase: RealAngleUseCase,
    private val calibrationUseCase: CalibrationUseCase,
    private val sensorService: SensorService
) : HomeViewModel() {

    private val _touchRealAngle = MutableLiveData(0.0)

    private val correctedTouchRealAngle = Transformations.map(_touchRealAngle) { angle ->
        when (_mode.value) {
            is Mode.Camera -> cameraTouchAngleProcessor.correctRealAngle(angle)
            //TODO: workaround, need to find better way
            is Mode.Plumb -> plumbAngleProcessor.correctRealAngle(_plumbRealAngle.value ?: 0.0)
            is Mode.Touch -> touchAngleProcessor.correctRealAngle(angle)
            null -> throw RuntimeException("Mode cannot be null")
        }.toFloat()
    }

    override val touchRealAngle: LiveData<Float>
        get() = Transformations.distinctUntilChanged(correctedTouchRealAngle)

    override val touchDisplayAngle: LiveData<String>
        get() = Transformations.distinctUntilChanged(
            Transformations.map(_touchRealAngle) { angle ->
                when (_mode.value) {
                    is Mode.Camera -> cameraTouchAngleProcessor.correctDisplayAngle(angle)
                    //TODO: workaround, need to find better way
                    is Mode.Plumb -> plumbAngleProcessor.correctDisplayAngle(_plumbRealAngle.value ?: 0.0)
                    is Mode.Touch -> touchAngleProcessor.correctDisplayAngle(angle)
                    null -> throw RuntimeException("Must not be null")
                }
            }
        )

    private val _plumbRealAngle = MutableLiveData(0.0)

    private val correctedPlumbRealAngle = Transformations.map(_plumbRealAngle) { angle ->
        when (_mode.value) {
            is Mode.Camera -> {
                val calibratedAngle = angle + calibrationUseCase.calibration
                cameraPlumbAngleProcessor.correctRealAngle(calibratedAngle)
            }
            is Mode.Plumb -> {
                val calibratedAngle = angle + calibrationUseCase.calibration
                plumbAngleProcessor.correctRealAngle(calibratedAngle)
            }
            //TODO: workaround, need to find better way
            is Mode.Touch -> touchAngleProcessor.correctRealAngle(_touchRealAngle.value ?: 0.0)
            null -> throw RuntimeException("Mode cannot be null")
        }.toFloat()
    }

    override val plumbRealAngle: LiveData<Float>
        get() = Transformations.distinctUntilChanged(correctedPlumbRealAngle)

    override val plumbDisplayAngle: LiveData<String>
        get() = Transformations.distinctUntilChanged(
            Transformations.map(_plumbRealAngle) { angle ->
                when (_mode.value) {
                    is Mode.Camera -> {
                        val calibratedAngle = angle + calibrationUseCase.calibration
                        cameraPlumbAngleProcessor.correctDisplayAngle(calibratedAngle)
                    }
                    is Mode.Plumb -> {
                        val calibratedAngle = angle + calibrationUseCase.calibration
                        plumbAngleProcessor.correctDisplayAngle(calibratedAngle)
                    }
                    //TODO: workaround, need to find better way
                    is Mode.Touch -> touchAngleProcessor.correctDisplayAngle(_touchRealAngle.value ?: 0.0)
                    null -> throw RuntimeException("Must not be null")
                }
            }
        )

    private val _mode = MutableLiveData<Mode>(Mode.Touch)
    override val mode: LiveData<Mode>
        get() = Transformations.distinctUntilChanged(_mode)

    private val _cameraHoldingMode = MutableLiveData(HoldingMode.FREE)

    override val cameraHoldingMode: LiveData<HoldingMode>
        get() = _cameraHoldingMode

    private val _calibrationAction = MutableLiveData<Double>()
    override val calibrationAction: LiveData<Double>
        get() = _calibrationAction

    private val sensorListener: SensorListener = { x, y, z ->
        val point = PointF(realAngleUseCase.anchorPoint.x + y, realAngleUseCase.anchorPoint.y + x)
        Log.d("point_checker", "$point")
        val angle = realAngleUseCase.getRealAngle(point)
        _plumbRealAngle.value = angle
    }

    private val modeChangeObserver: Observer<Mode> = Observer<Mode> {
        when(it) {
            is Mode.Touch -> {
                sensorService.removeListener()
            }
            is Mode.Plumb -> {
                sensorService.setListener(sensorListener)
            }
            is Mode.Camera -> {
                sensorService.setListener(sensorListener)
            }
        }
    }

    init {
        mode.observeForever(modeChangeObserver)
    }

    override fun setTouchPoint(point: PointF) {
        val realAngle = realAngleUseCase.getRealAngle(point)
        _touchRealAngle.value = realAngle
    }

    override fun setAnchorPoint(point: PointF) {
        realAngleUseCase.anchorPoint = point
    }

    override fun setMode(mode: Mode) {
        _mode.value = mode
        _touchRealAngle.value = _touchRealAngle.value
        _plumbRealAngle.value = _plumbRealAngle.value
    }

    override fun setCalibration() {
        val angle = plumbAngleProcessor.correctRealAngle(_plumbRealAngle.value ?: 0.0)
        calibrationUseCase.calibration = -angle
        _calibrationAction.value = angle
        _plumbRealAngle.value = _plumbRealAngle.value
    }

    override fun resetCalibration() {
        calibrationUseCase.resetCalibration()
        _calibrationAction.value = 0.0
        _plumbRealAngle.value = _plumbRealAngle.value
    }

    override fun onCleared() {
        _mode.removeObserver(modeChangeObserver)
        super.onCleared()
    }

    override fun hold() {
        if (_mode.value == Mode.Camera) {
            _cameraHoldingMode.value = HoldingMode.HOLDING
        }
    }

    override fun release() {
        if (_mode.value == Mode.Camera) {
            _cameraHoldingMode.value = HoldingMode.FREE
            _plumbRealAngle.value = _plumbRealAngle.value
        }
    }
}