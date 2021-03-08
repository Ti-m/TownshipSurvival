package com.example.settlers.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import com.example.settlers.Coordinates
import com.example.settlers.TileManager
import kotlin.math.roundToInt

class GameWorld(
    context: Context?,
    private val tileManager: TileManager
) : ViewGroup(context) {
    companion object {
        val TAG = "GameWorld"
    }

    init {
        tileManager.tiles.forEach {
            this.addView(it.value)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.i(TAG,"onInterceptTouchEvent")
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG,"onTouchEvent")
        return false
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //Android View layout is limited to whole pixels. So make sure no information is lost
        // while rounding, or you get ugly clilping. Hence, use full Ints
        tileManager.tiles.forEach {
            val w = it.value.coords.w
            val h = it.value.coords.h
            it.value.layout(
                (it.value.cell.coordinates.y * 3 * w).roundToInt(),
                (it.value.cell.coordinates.x * 2 * h + 4 * h).roundToInt(),
                (it.value.cell.coordinates.y * 3 * w + 4 * w).roundToInt(),
                (it.value.cell.coordinates.x * 2 * h + 8 * h).roundToInt()
            )
        }
    }
}