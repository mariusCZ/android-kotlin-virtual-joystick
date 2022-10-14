package com.mbeddev.androidkotlinvirtualjoystick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout

class MainActivity : AppCompatActivity(), JoystickView.JoyStickListener {

    companion object {
        // Global screen dimensions.
        var screenHeight: Int = 0
        var screenWidth: Int = 0
    }

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
    }

    override fun onJoystickMoved(xPercent: Float, yPercent: Float, id: Int) {
        if(id == R.id.my_joystick) {
            Log.d("app", "joystick moved")
        }
    }
}