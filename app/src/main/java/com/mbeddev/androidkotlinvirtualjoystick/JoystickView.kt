package com.mbeddev.androidkotlinvirtualjoystick

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.annotation.Nullable
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.pow


class JoystickView : SurfaceView, SurfaceHolder.Callback,
    View.OnTouchListener{

    constructor(context: Context, attributes: AttributeSet): super(context, attributes) {
        setupJoystickView()
        initAttributes(context, attributes)
    }

    constructor(context: Context, attributes: AttributeSet, style: Int) : super(context, attributes) {
        setupJoystickView()
        initAttributes(context, attributes)
    }

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

    @Nullable
    private lateinit var joystickListener: JoyStickListener

    private fun setupDimensions() {
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
        baseRadius = min(width * 0.93F, height * 0.93F) / 3
        hatRadius = min(width * 0.93F, height * 0.93F) / 5
    }

    private fun setupJoystickView() {
        holder.addCallback(this)
        setOnTouchListener(this)
        setBackgroundColor(Color.TRANSPARENT)
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
    }

    fun initAttributes(context: Context, attrs: AttributeSet) {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.JoystickView)
        val base = a.getColor(R.styleable.JoystickView_base_color, Color.parseColor("#303F9F"))
        val hat = a.getColor(R.styleable.JoystickView_hat_color, Color.parseColor("#5E5E92"))
        val stickShade =
            a.getColor(R.styleable.JoystickView_stick_shade_color, Color.parseColor("#afffff"))

        // Conversion from int to ARGB value
        baseA = base shr 24 and 0xff
        baseR = base shr 16 and 0xff
        baseG = base shr 8 and 0xff
        baseB = base and 0xff

        hatA = hat shr 24 and 0xff
        hatR = hat shr 16 and 0xff
        hatG = hat shr 8 and 0xff
        hatB = hat and 0xff

        stickShadeR = stickShade shr 16 and 0xff
        stickShadeG = stickShade shr 8 and 0xff
        stickShadeB = stickShade and 0xff

        ratio = a.getInteger(R.styleable.JoystickView_ratio, 5)
        drawStick = a.getBoolean(R.styleable.JoystickView_draw_stick_shading, true)
        shadeHat = a.getBoolean(R.styleable.JoystickView_draw_hat_shading, true)
        drawBase = a.getBoolean(R.styleable.JoystickView_draw_base, true)

        a.recycle()
    }

    private fun drawJoystick(newX: Float, newY: Float) {
        if(holder.surface.isValid) {
            val myCanvas = this.holder.lockCanvas()
            val colors = Paint()
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            val hypotenuse: Float = sqrt((newX - centerX).pow(2) + (newY - centerY).pow(2))
            val sin: Float = (newY - centerY) / hypotenuse
            val cos: Float = (newX - centerX) / hypotenuse

            if(drawBase) {
                colors.setARGB(baseA, baseR, baseG, baseB)
                myCanvas.drawCircle(centerX, centerY, baseRadius, colors)
            }

            if(drawStick) {
                for(i in (1..(baseRadius/ratio).toInt())) {
                    colors.setARGB(150 / i, stickShadeR, stickShadeG, stickShadeB)
                    myCanvas.drawCircle(newX - cos * hypotenuse * (ratio / baseRadius) * i,
                        newY - sin * hypotenuse * (ratio / baseRadius) * i,
                        i * (hatRadius * ratio / baseRadius), colors)
                }
            }

            if(shadeHat) {
                val numLoops: Int = (hatR / ratio)
                val rChange: Int = (255 - hatR) / numLoops
                val gChange: Int = (255 - hatG) / numLoops
                val bChange: Int = (255 - hatB) / numLoops

                for(i in (0..(hatRadius / ratio).toInt())) {
                    colors.setARGB(255, hatR + (i * rChange),
                        hatG + (i * gChange), hatB + (i * bChange))
                    myCanvas.drawCircle(newX, newY, hatRadius - i.toFloat() * (ratio) / 2, colors)
                }
            }
            else {
                colors.setARGB(hatA, hatR, hatG, hatB)
                myCanvas.drawCircle(newX, newY, hatRadius, colors)
            }

            holder.unlockCanvasAndPost(myCanvas)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        setupDimensions()
        drawJoystick(centerX, centerY)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(v?.equals(this) == true) {
            if(event?.action != MotionEvent.ACTION_UP) {
                val displacement: Float =
                    sqrt((event!!.x - centerX).pow(2) + (event.y - centerY).pow(2))
                if(displacement < baseRadius) {
                    drawJoystick(event.x, event.y)
                    updateListener((event.x - centerX)/baseRadius, (event.y - centerY)/baseRadius)
                }
                else {
                    val ratio: Float = baseRadius / displacement
                    val constrainedX: Float = centerX + (event.x - centerX) * ratio
                    val constrainedY: Float = centerY + (event.y - centerY) * ratio
                    drawJoystick(constrainedX, constrainedY)
                    updateListener((constrainedX-centerX)/baseRadius, (constrainedY-centerY)/baseRadius)
                }
            }
            else {
                drawJoystick(centerX, centerY)
                updateListener((0).toFloat(),(0).toFloat())
            }
        }
        return true
    }

    private fun updateListener(xPercent: Float, yPercent: Float) {
        joystickListener.onJoystickMoved(xPercent, yPercent, id)
    }

    fun setJoystickListener(@Nullable joystickListener: JoyStickListener) {
        this.joystickListener = joystickListener
    }

    interface JoyStickListener {
        fun onJoystickMoved(xPercent: Float, yPercent: Float, id: Int)
    }
}