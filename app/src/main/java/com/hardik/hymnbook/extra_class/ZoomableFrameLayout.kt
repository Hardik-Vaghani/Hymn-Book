package com.hardik.hymnbook.extra_class

/*
class ZoomableFrameLayout : FrameLayout {

    private var scaleGestureDetector: ScaleGestureDetector

    private var scaleFactor = 1.0f

    constructor(context: Context) : super(context) {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        super.dispatchTouchEvent(ev)
        return scaleGestureDetector.onTouchEvent(ev)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(0.7f, Math.min(scaleFactor, 4.3f)) // Adjust min and max scale limits as needed
            scaleChildren()
            return true
        }
    }

    private fun scaleChildren() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.scaleX = scaleFactor
            child.scaleY = scaleFactor
        }
    }
}*/

/*

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout

class ZoomableFrameLayout : FrameLayout {

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    constructor(context: Context) : super(context) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context)
    }

    private fun setup(context: Context) {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        return ev?.let { scaleGestureDetector.onTouchEvent(it) } ?: super.dispatchTouchEvent(ev)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.7f, 4.3f) // Adjust min and max scale limits as needed
            scaleChildren()
            return true
        }
    }

    private fun scaleChildren() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.scaleX = scaleFactor
            child.scaleY = scaleFactor
        }
    }
}
*/

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.WindowManager
import android.widget.FrameLayout

class ZoomableFrameLayout : FrameLayout {

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    // Variables for dragging
    private var lastTouchX: Float = 0.toFloat()
    private var lastTouchY: Float = 0.toFloat()
    private var activePointerId = MotionEvent.INVALID_POINTER_ID

    constructor(context: Context) : super(context) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context)
    }

    private fun setup(context: Context) {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        return scaleGestureDetector.onTouchEvent(ev!!)|| handleDragEvent(ev)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(1.0f, 3.5f) // Adjust min and max scale limits as needed
            scaleChildren()
            return true
        }
    }

    private fun scaleChildren() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.scaleX = scaleFactor
            child.scaleY = scaleFactor
        }
    }

    private fun scaleChildren1() {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            // Calculate pivot point as center of each child
            val pivotX = child.width / 2f
            val pivotY = child.height / 2f

            // Set pivot point
            child.pivotX = pivotX
            child.pivotY = pivotY

            // Calculate scaled width and height
            val scaledWidth = screenWidth * scaleFactor
            val scaledHeight = screenHeight * scaleFactor

            // Apply scale factors
            child.layoutParams.width = scaledWidth.toInt()
            child.layoutParams.height = scaledHeight.toInt()
            child.requestLayout()
        }
    }

    private fun handleDragEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = event.actionIndex
                lastTouchX = event.getX(pointerIndex)
                lastTouchY = event.getY(pointerIndex)
                activePointerId = event.getPointerId(pointerIndex)
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex != -1) {
                    val x = event.getX(pointerIndex)
                    val y = event.getY(pointerIndex)
                    val dx = x - lastTouchX
                    val dy = y - lastTouchY
//                    scrollBy(-dx.toInt(), -dy.toInt()) // Invert the direction for natural scrolling
                    translationX += dx
                    translationY += dy
                    lastTouchX = x
                    lastTouchY = y
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = event.getX(newPointerIndex)
                    lastTouchY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }
}
