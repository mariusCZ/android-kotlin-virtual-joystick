package com.mbeddev.androidkotlinvirtualjoystick

import android.R.attr.centerX
import android.R.attr.centerY
import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView


class JoystickView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var centerX: Float = 0F
    private var centerY: Float = 0F

    private var baseRadius: Float = 0F
    private var hatRadius: Float = 0F

    private var baseA: Int = 0
    private var baseR: Int = 0
    private var baseG: Int = 0
    private var baseB: Int = 0

    private var hatA: Int = 0
    private var hatR: Int = 0
    private var hatG: Int = 0
    private var hatB: Int = 0

    private var stickShadeR: Int = 0
    private var stickShadeG: Int = 0
    private var stickShadeB: Int = 0

    private var drawBase: Boolean = false
    private var drawStick: Boolean = false
    private var shadeHat: Boolean = false

    private var ratio: Int = 0

    private fun setupDimensions() {
        centerX = (MainActivity.screenWidth / 2).toFloat()
        centerY = (MainActivity.screenHeight / 2).toFloat()
        baseRadius = kotlin.math.min(MainActivity.screenWidth
                * 0.93F, MainActivity.screenHeight * 0.93F) / 3
        hatRadius = kotlin.math.min(MainActivity.screenWidth
                * 0.93F, MainActivity.screenHeight * 0.93F) / 5
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }
}