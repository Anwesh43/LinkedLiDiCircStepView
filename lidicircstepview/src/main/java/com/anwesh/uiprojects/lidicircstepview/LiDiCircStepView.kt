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
val STROKE_FACTOR : Int = 90
val SIZE_FACTOR : Int = 3
val COLOR : Int = Color.parseColor("#01579B")
val BACK_COLOR : Int = Color.parseColor("#BDBDBD")
val DELAY : Long = 30
val scGap : Float = 0.1f / 2
val rFactor : Int = 10

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - n.getInverse() * i))

fun Float.getScaleFactor() : Float = Math.floor(this / 0.51).toFloat()

fun Float.getMirrorValue(a : Int, b : Int) : Float = (1 - this) * a.getInverse() + this * b.getInverse()

fun Float.updateScale(dir : Float, a : Int, b : Int) : Float = dir * scGap + getScaleFactor().getMirrorValue(a, b)

fun Canvas.drawLDCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    paint.strokeWidth = Math.min(w, h) / STROKE_FACTOR
    paint.color = COLOR
    paint.strokeCap = Paint.Cap.ROUND
    paint.style = Paint.Style.STROKE
    val size : Float = gap / SIZE_FACTOR
    val r : Float = size * Math.sqrt(2.0).toFloat() * rFactor.getInverse()
    for (j in 0..(lines - 1)) {
        val scs : Float = sc1.divideScale(j, lines)
        val scr : Float = sc2.divideScale(j, lines)
        val degStart : Float = (90f * j + 45f) + 180f
        save()
        rotate(90f * j)
        drawLine(0f, 0f, size * scs, size * scs, paint)
        save()
        translate(size + r, size + r)
        drawArc(RectF(-r, -r, r, r), degStart, 360f * scr, false, true)
        restore()
        restore()
    }
}

class LiDiCircStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}