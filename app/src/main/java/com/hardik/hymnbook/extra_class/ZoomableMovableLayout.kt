package com.hardik.hymnbook.extra_class

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ScrollView
import com.hardik.hymnbook.common.Constants.BASE_TAG

class ZoomableMovableLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private val TAG = BASE_TAG + ZoomableMovableLayout::class.java.simpleName

    private val scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var previousScaleFactor = 1.0f
    private var lastFocusX = 0f
    private var lastFocusY = 0f

    private var translationX = 0f
    private var translationY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f


    private var isDragging = false

    private val matrix = Matrix()

    init {
        // Initialize ScaleGestureDetector
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        // Ensure smooth scrolling
        overScrollMode = OVER_SCROLL_NEVER
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)
        Log.d(TAG, "dispatchTouchEvent: ")
        // Handle scale gesture detection
        scaleGestureDetector.onTouchEvent(event)

        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Start dragging
                lastTouchX = touchX
                lastTouchY = touchY
                isDragging = true
            }
            MotionEvent.ACTION_MOVE -> {
                // Handle dragging
                if (isDragging) {
                    val dx = touchX - lastTouchX
                    val dy = touchY - lastTouchY
                    translationX += dx
                    translationY += dy
                    applyMatrix()
                    lastTouchX = touchX
                    lastTouchY = touchY

                    // Smooth scroll by considering the scale factor
//                    smoothScrollBy((dx / scaleFactor).toInt(), (dy / scaleFactor).toInt())
                }

            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // End dragging
                isDragging = false
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, "onDraw: ")
        // Apply matrix transformation
        super.onDraw(canvas)
        canvas.concat(matrix)
    }

    private fun applyMatrix() {
        Log.d(TAG, "applyMatrix: ")
        // Apply scaling and translation
        matrix.reset()
        matrix.postScale(scaleFactor, scaleFactor)

        // Adjust translation based on scale factor
        matrix.postTranslate(translationX, translationY)

        invalidate() // Redraw with updated matrix

        // Ensure smooth scrolling to adjusted coordinates
//        smoothScrollTo(translationX.toInt(), translationY.toInt())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            Log.d(TAG, "onScaleBegin: ")
            // Begin scaling
            lastFocusX = detector.focusX
            lastFocusY = detector.focusY
            previousScaleFactor = scaleFactor

            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Log.d(TAG, "onScale: ")
            // Handle scaling
            val newScaleFactor = previousScaleFactor * detector.scaleFactor
            scaleFactor = newScaleFactor.coerceIn(MIN_SCALE_FACTOR, MAX_SCALE_FACTOR)

            // Calculate translation delta based on the scale factor
            val scaleDelta = newScaleFactor / previousScaleFactor
            val focusDeltaX = detector.focusX - lastFocusX
            val focusDeltaY = detector.focusY - lastFocusY

            translationX -= focusDeltaX * (scaleDelta - 1)
            translationY -= focusDeltaY * (scaleDelta - 1)

            lastFocusX = detector.focusX
            lastFocusY = detector.focusY
            previousScaleFactor = newScaleFactor

            applyMatrix() // Apply updated matrix
            return true
        }
    }

    companion object {
        private const val MIN_SCALE_FACTOR = 1.0f
        private const val MAX_SCALE_FACTOR = 4.5f
    }
}
