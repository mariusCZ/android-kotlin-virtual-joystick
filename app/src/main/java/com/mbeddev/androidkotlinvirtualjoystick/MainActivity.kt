package com.mbeddev.androidkotlinvirtualjoystick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnLayout

class MainActivity : AppCompatActivity(), JoystickView.JoyStickListener {

    companion object {
        // Global screen dimensions.
        var screenHeight: Int = 0
        var screenWidth: Int = 0
    }

    var joystickId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // This lambda is for specific procedures that need to be done when the layout is created.
        val cLayout: ConstraintLayout = findViewById(R.id.constraintLayout)
        cLayout.doOnLayout {
            screenHeight = it.measuredHeight
            screenWidth = it.measuredWidth
        }

        val joystick: JoystickView = findViewById(R.id.my_joystick)
        joystick.setJoystickListener(this)

        val lp: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        val joystickTwo = JoystickView(this)
        joystickTwo.id = View.generateViewId()

        cLayout.addView(joystickTwo, lp)

        joystickTwo.layoutParams.width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100F, resources.displayMetrics).toInt()
        joystickTwo.layoutParams.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100F, resources.displayMetrics).toInt()

        val set = ConstraintSet()
        set.clone(cLayout)

        set.centerHorizontally(joystickTwo.id, ConstraintSet.PARENT_ID)
        set.connect(
            joystickTwo.id, ConstraintSet.TOP, joystick.id, ConstraintSet.BOTTOM, 40
        )
        set.applyTo(cLayout)
        joystickTwo.setJoystickListener(this)
        joystickId = joystickTwo.id
    }

    override fun onJoystickMoved(xPercent: Float, yPercent: Float, id: Int) {
        if(id == R.id.my_joystick) {
            Log.d("app", "1: x - $xPercent, y - $yPercent")
        }
        else if(id == joystickId) {
            Log.d("app", "2: x - $xPercent, y - $yPercent")
        }
    }

}