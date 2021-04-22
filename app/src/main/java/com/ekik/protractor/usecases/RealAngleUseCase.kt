package com.ekik.protractor.usecases

import android.graphics.PointF

interface RealAngleUseCase {
    var anchorPoint: PointF
    fun getRealAngle(point: PointF): Double
}