package com.ekik.protractor.usecases

import android.graphics.PointF
import android.util.Log
import com.ekik.protractor.horizontalAngleTo
import com.ekik.protractor.vectorsAngle

class RealAngleUseCaseImpl: RealAngleUseCase {
    override var anchorPoint: PointF = PointF()

    override fun getRealAngle(point: PointF): Double {
        return anchorPoint.horizontalAngleTo(point)
    }
}