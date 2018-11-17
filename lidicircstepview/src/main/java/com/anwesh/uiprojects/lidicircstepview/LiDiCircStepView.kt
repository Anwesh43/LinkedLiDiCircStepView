package com.anwesh.uiprojects.lidicircstepview

/**
 * Created by anweshmishra on 17/11/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val lines : Int = 4
val STROKE_FACTOR : Int = 60
val SIZE_FACTOR : Int = 3
val COLOR : Int = Color.parseColor("#01579B")
val BACK_COLOR : Int = Color.parseColor("#BDBDBD")
val DELAY : Long = 30
val scGap : Float = 0.1f / 2

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - n.getInverse() * i))

fun Float.getScaleFactor() : Float = Math.floor(this / 0.51).toFloat()

fun Float.getMirrorValue(a : Int, b : Int) : Float = (1 - this) * a.getInverse() + this * b.getInverse()

fun Float.updateScale(dir : Float, a : Int, b : Int) : Float = dir * scGap + getScaleFactor().getMirrorValue(a, b)

