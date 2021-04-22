package com.ekik.protractor

import android.graphics.PointF
import android.view.View
import kotlin.math.*

fun PointF.horizontalAngleTo(point: PointF): Double {
    val horizontalPoint = PointF(this.x - 1, this.y)
    return vectorsAngle(point, this, horizontalPoint).toDouble()
}

fun vectorsAngle(P1: PointF, P2: PointF, P3: PointF): Float {
    return atan2(P3.y - P1.y, P3.x - P1.x) -
            atan2(P2.y - P1.y, P2.x - P1.x).radToDegree()
}

fun PointF.distanceTo(point: PointF): Double =
    sqrt((this.x - point.x).toDouble().pow(2) + (this.y - point.y).toDouble().pow(2))

fun Double.radToDegree(): Double = this * (180 / Math.PI)
fun Float.radToDegree(): Float = this * (180 / Math.PI).toFloat()

fun Double.round(decimals: Int = 2) = "%.${decimals}f".format(this).toFloat()

//View
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

val View.isVisible get() = visibility == View.VISIBLE

val DoNothing = Unit

