package com.anwesh.uiprojects.linkedlidicircstepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.lidicircstepview.LiDiCircStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LiDiCircStepView = LiDiCircStepView.create(this)
    }
}
