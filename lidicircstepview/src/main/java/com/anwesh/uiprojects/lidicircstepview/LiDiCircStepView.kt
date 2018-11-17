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
import android.util.Log

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
        drawArc(RectF(-r, -r, r, r), degStart, 360f * scr, false, paint)
        restore()
        restore()
    }
}

class LiDiCircStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            val k : Float = scale.updateScale(dir, lines, lines)
            scale += k
            Log.d("updated scale by", "$k")
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class LDCNode(var i : Int, val state : State = State()) {
        private var next : LDCNode? = null
        private var prev : LDCNode? = null

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = LDCNode(i + 1)
                next?.prev = this
            }
        }

        init {
            addNeighbor()
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawLDCNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LDCNode {
            var curr : LDCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LiDiCircStep(var i : Int) {

        private var curr : LDCNode = LDCNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(DELAY)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class Renderer(var view : LiDiCircStepView) {

        private val animator : Animator = Animator(view)

        private val ldcs : LiDiCircStep = LiDiCircStep(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(BACK_COLOR)
            ldcs.draw(canvas, paint)
            animator.animate {
                ldcs.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ldcs.startUpdating {
                animator.start()
            }
        }
    }
}