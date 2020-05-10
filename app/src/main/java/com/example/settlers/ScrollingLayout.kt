package com.example.settlers
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.core.view.MotionEventCompat

class ScrollingLayout : RelativeLayout {
    private var mPosX = 0f
    private var mPosY = 0f

    companion object {
        private val TAG = "ScrollingLayout"
    }

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.i(TAG, "onTouchEvent $ev")
        val action = MotionEventCompat.getActionMasked(ev)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mPosX = x - ev.rawX
                mPosY = y - ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {

                // Find the index of the active pointer and fetch its position
                // Calculate the distance moved
                x = ev.rawX + mPosX
                y = ev.rawY + mPosY
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_CANCEL -> {
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }
        }
        return true
    }
}