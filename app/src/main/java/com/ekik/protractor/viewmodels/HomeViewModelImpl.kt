package com.ekik.protractor.viewmodels

import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.ekik.protractor.DoNothing
import com.ekik.protractor.services.SensorListener
import com.ekik.protractor.services.SensorService
import com.ekik.protractor.usecases.CalibrationUseCase
import com.ekik.protractor.usecases.RealAngleUseCase
import com.ekik.protractor.usecases.processors.AngleProcessor

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

    private val touchRealAngleObs = Observer<Double> { angle ->
        when (_mode.value) {
            is Mode.Camera -> {
                val realAngle = cameraTouchAngleProcessor.correctRealAngle(angle).toFloat()
                _touchCorrectedRealAngle.value = realAngle
                val displayAngle = cameraTouchAngleProcessor.correctDisplayAngle(angle)
                _touchDisplayAngle.value = displayAngle
            }
            is Mode.Touch -> {
                val realAngle = touchAngleProcessor.correctRealAngle(angle).toFloat()
                _touchCorrectedRealAngle.value = realAngle
                val displayAngle = touchAngleProcessor.correctDisplayAngle(angle)
                _touchDisplayAngle.value = displayAngle
            }
            else -> {
                DoNothing
            }
        }
    }

    private val _touchCorrectedRealAngle = MutableLiveData<Float>()
    override val touchRealAngle: LiveData<Float>
        get() = Transformations.distinctUntilChanged(_touchCorrectedRealAngle)

    private val _touchDisplayAngle = MutableLiveData<String>()
    override val touchDisplayAngle: LiveData<String>
        get() = Transformations.distinctUntilChanged(_touchDisplayAngle)

    private val _plumbRealAngle = MutableLiveData(0.0)

    private val plumbRealAngleObs = Observer<Double> { angle ->
        if (_cameraHoldingMode.value == HoldingMode.FREE) {
            when (_mode.value) {
                is Mode.Camera -> {
                    val calibratedAngle = angle + calibrationUseCase.calibration
                    val realAngle =
                        cameraPlumbAngleProcessor.correctRealAngle(calibratedAngle).toFloat()
                    _correctedPlumbAngle.value = realAngle
                    val displayAngle =
                        cameraPlumbAngleProcessor.correctDisplayAngle(calibratedAngle)
                    _plumbDisplayAngle.value = displayAngle
                }
                is Mode.Plumb -> {
                    val calibratedAngle = angle + calibrationUseCase.calibration
                    val realAngle = plumbAngleProcessor.correctRealAngle(calibratedAngle).toFloat()
                    _correctedPlumbAngle.value = realAngle
                    val displayAngle = plumbAngleProcessor.correctDisplayAngle(calibratedAngle)
                    _plumbDisplayAngle.value = displayAngle
                }
                else -> {
                    DoNothing
                }
            }
        }
    }

    private val _correctedPlumbAngle = MutableLiveData<Float>()
    override val plumbRealAngle: LiveData<Float>
        get() = Transformations.distinctUntilChanged(_correctedPlumbAngle)

    private val _plumbDisplayAngle = MutableLiveData<String>()
    override val plumbDisplayAngle: LiveData<String>
        get() = Transformations.distinctUntilChanged(_plumbDisplayAngle)

    private val _mode = MutableLiveData<Mode>(Mode.Touch)
    override val mode: LiveData<Mode>
        get() = Transformations.distinctUntilChanged(_mode)

    private val _cameraHoldingMode = MutableLiveData(HoldingMode.FREE)

    override val cameraHoldingMode: LiveData<HoldingMode>
        get() = _cameraHoldingMode

    private val _calibrationAction = MutableLiveData<Double>()
    override val calibrationAction: LiveData<Double>
        get() = _calibrationAction

    private val _darkMode = MutableLiveData(false)
    override val darkMode: LiveData<Boolean>
        get() = Transformations.distinctUntilChanged(_darkMode)

    private val sensorListener: SensorListener = { x, y, z ->
        val point = PointF(realAngleUseCase.anchorPoint.x + y, realAngleUseCase.anchorPoint.y + x)
        Log.d("point_checker", "$point")
        val angle = realAngleUseCase.getRealAngle(point)
        _plumbRealAngle.value = angle
    }

    private val modeChangeObserver: Observer<Mode> = Observer<Mode> {
        when (it) {
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
        _mode.observeForever(modeChangeObserver)
        _touchRealAngle.observeForever(touchRealAngleObs)
        _plumbRealAngle.observeForever(plumbRealAngleObs)
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
        _touchRealAngle.removeObserver(touchRealAngleObs)
        _plumbRealAngle.removeObserver(plumbRealAngleObs)
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

    override fun setDarkMode() {
        _darkMode.value = true
    }

    override fun setLightMode() {
        _darkMode.value = false
    }
}