package com.ekik.protractor.di

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import com.ekik.protractor.activities.BaseActivity
import com.ekik.protractor.navigation.MainActivityNavigator
import com.ekik.protractor.navigation.Navigator
import com.ekik.protractor.services.SensorService
import com.ekik.protractor.services.SensorServiceImpl
import com.ekik.protractor.usecases.*
import com.ekik.protractor.usecases.correctors.PlumbCorrectionUseCase
import com.ekik.protractor.usecases.correctors.PlumbRealAngleCorrectionUseCase
import com.ekik.protractor.usecases.correctors.TouchCorrectionUseCase
import com.ekik.protractor.usecases.processors.AngleProcessor
import com.ekik.protractor.usecases.processors.AngleProcessorImpl
import com.ekik.protractor.usecases.realtodisplaymappers.ConvertUseCase
import com.ekik.protractor.usecases.realtodisplaymappers.PlumbRealAngleToDisplayConvertUseCase
import com.ekik.protractor.usecases.realtodisplaymappers.TouchRealAngleToDisplayConvertUseCase
import com.ekik.protractor.viewmodels.HomeViewModel
import com.ekik.protractor.viewmodels.HomeViewModelImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    factory<Navigator>(qualifier = named(MAIN_NAVIGATOR)) {
        val context: Context = get()
        MainActivityNavigator(
            (context as AppCompatActivity).supportFragmentManager,
            (context as BaseActivity).rootId
        )
    } //NOTE!!!! does not work, need to investigate

    factory<RealAngleUseCase>(named(REAL_ANGLE_CONVERTER)) {
        RealAngleUseCaseImpl()
    }

    factory<AngleProcessor>(named(TOUCH_PROCESSOR)) {
        AngleProcessorImpl(
            null,
            get(named(TOUCH_DISPLAY_ANGLE_USE_CASE))
        )
    }

    factory<AngleProcessor>(named(PLUMB_PROCESSOR)) {
        AngleProcessorImpl(
            get(named(PLUMB_REAL_ANGLE_CORRECTOR)),
            get(named(PLUMB_DISPLAY_ANGLE_USE_CASE))
        )
    }

    //TODO; following 2 can be changed
    factory<AngleProcessor>(named(CAMERA_TOUCH_PROCESSOR)) {
        AngleProcessorImpl(
            null,
            get(named(TOUCH_DISPLAY_ANGLE_USE_CASE))
        )
    }

    factory<AngleProcessor>(named(CAMERA_PLUMB_PROCESSOR)) {
        AngleProcessorImpl(
            get(named(PLUMB_REAL_ANGLE_CORRECTOR)),
            get(named(PLUMB_DISPLAY_ANGLE_USE_CASE))
        )
    }

    factory<ConvertUseCase<Double, Double>>(named(PLUMB_REAL_ANGLE_CORRECTOR)) {
        PlumbRealAngleCorrectionUseCase()
    }

    factory<ConvertUseCase<Double, String>>(named(TOUCH_DISPLAY_ANGLE_USE_CASE)) {
        TouchRealAngleToDisplayConvertUseCase(get(named(TOUCH_CORRECTOR)))
    }

    factory<ConvertUseCase<Double, String>>(named(PLUMB_DISPLAY_ANGLE_USE_CASE)) {
        PlumbRealAngleToDisplayConvertUseCase(get(named(PLUMB_CORRECTOR)))
    }

    factory<ConvertUseCase<Double, Double>>(named(PLUMB_CORRECTOR)) {
        PlumbCorrectionUseCase()
    }

    factory<ConvertUseCase<Double, Double>>(named(TOUCH_CORRECTOR)) {
        TouchCorrectionUseCase()
    }

    single {
        androidContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    factory<Sensor>(named(GRAVITY_SENSOR)) {
        val sensorManager: SensorManager = get()
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    factory<SensorService> {
        SensorServiceImpl(get(), get(named(GRAVITY_SENSOR)))
    }

    factory<CalibrationUseCase> {
        CalibrationUseCaseImpl()
    }

    viewModel<HomeViewModel> {
        HomeViewModelImpl(
            get(named(TOUCH_PROCESSOR)),
            get(named(PLUMB_PROCESSOR)),
            get(named(CAMERA_TOUCH_PROCESSOR)),
            get(named(CAMERA_PLUMB_PROCESSOR)),
            get(named(REAL_ANGLE_CONVERTER)),
            get(),
            get()
        )
    }

}

const val MAIN_NAVIGATOR = "main"
const val REAL_ANGLE_CONVERTER = "real_angle"
const val TOUCH_PROCESSOR = "touch_processor"
const val PLUMB_PROCESSOR = "plumb_processor"
const val CAMERA_TOUCH_PROCESSOR = "camera_touch_processor"
const val CAMERA_PLUMB_PROCESSOR = "camera_plumb_processor"
const val PLUMB_REAL_ANGLE_CORRECTOR = "plumb_real_angle_corrector"
const val TOUCH_DISPLAY_ANGLE_USE_CASE = "touch_display_angle_use_case"
const val PLUMB_DISPLAY_ANGLE_USE_CASE = "plumb_display_angle_use_case"
const val PLUMB_CORRECTOR = "plumb_corrector"
const val TOUCH_CORRECTOR = "touch_corrector"
const val GRAVITY_SENSOR = "gravity_sensor"
