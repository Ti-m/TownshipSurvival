package com.example.settlers

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.MotionEventCompat
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign


class ZoomingLayout : RelativeLayout, ScaleGestureDetector.OnScaleGestureListener {
    companion object {
        private val TAG = "ZoomingLayout"

        private val MIN_ZOOM = 1.0f
        private val MAX_ZOOM = 4.0f
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    // Where the finger first  touches the screen
    private var startX = 0f
    private var startY = 0f

    // How much to translate the canvas
    private var dx = 0f
    private var dy = 0f
    private var prevDx = 0f
    private var prevDy = 0f

    private enum class Mode {
        NONE, DRAG, ZOOM
    }

    private var mode = Mode.NONE
    private var scale = 1.0f
    private var lastScaleFactor = 0f

    private var scaleDetector = ScaleGestureDetector(context, this)

    private val childTouchListeners = mutableMapOf<String, (motionEvent: MotionEvent?) -> Boolean>()

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        super.onTouchEvent(motionEvent)
        Log.d("foo-zooming", motionEvent!!.action.toString())
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "DOWN")
//                    if (scale > MIN_ZOOM) {
                mode = Mode.DRAG
                startX = motionEvent.x - prevDx
                startY = motionEvent.y - prevDy
//                    }
            }
            MotionEvent.ACTION_MOVE -> if (mode == Mode.DRAG) {
                dx = motionEvent.x - startX
                dy = motionEvent.y - startY
            }
            MotionEvent.ACTION_POINTER_DOWN -> mode = Mode.ZOOM
            MotionEvent.ACTION_POINTER_UP -> mode = Mode.DRAG
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "UP")
                mode = Mode.NONE
                prevDx = dx
                prevDy = dy
            }
        }
        scaleDetector.onTouchEvent(motionEvent)
        if (mode == Mode.DRAG && scale >= MIN_ZOOM || mode == Mode.ZOOM) {
            parent.requestDisallowInterceptTouchEvent(true)
            val maxDx: Float =
                (child()!!.getWidth() - child()!!.getWidth() / scale) / 2 * scale
            val maxDy: Float =
                (child()!!.getHeight() - child()!!.getHeight() / scale) / 2 * scale
//                dx = Math.min(Math.max(dx, -maxDx), maxDx)
//                dy = Math.min(Math.max(dy, -maxDy), maxDy)
            Log.i(
                TAG,
                "Width: " + child()!!.getWidth()
                    .toString() + ", scale " + scale.toString() + ", dx " + dx
                    .toString() + ", max " + maxDx
            )
            applyScaleAndTranslation()
        }

        childTouchListeners.forEach {
            it.value.invoke(motionEvent)
        }

        return true
    }

    fun addTouchListener(identifier: String, func: (motionEvent: MotionEvent?) -> Boolean) {
        childTouchListeners[identifier] = func
    }

    fun removeTouchListener(identifier: String) {
        childTouchListeners.remove(identifier)
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        Log.i(TAG, "onScaleBegin")
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        Log.i(TAG, "onScaleEnd")
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        val scaleFactor: Float = detector!!.scaleFactor
        Log.i(TAG, "onScale$scaleFactor")
        if (lastScaleFactor == 0.0f || sign(scaleFactor) == sign(
                lastScaleFactor
            )
        ) {
            scale *= scaleFactor
            scale = max(MIN_ZOOM, min(scale, MAX_ZOOM))
            lastScaleFactor = scaleFactor
        } else {
            lastScaleFactor = 0.0f
        }
        return true
    }

    private fun applyScaleAndTranslation() {
        child()!!.scaleX = scale
        child()!!.scaleY = scale
        child()!!.translationX = dx
        child()!!.translationY = dy
    }

    private fun child(): View? {
        return getChildAt(0)
    }
}