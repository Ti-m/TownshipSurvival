package com.example.settlers.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import com.example.settlers.Coordinates
import com.example.settlers.TileManager

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

        tileManager.tiles.forEach {
            val w = it.value.coords.w
            val h = it.value.coords.h
            it.value.layout(
                (it.value.cell.coordinates.y * 1.5 * w).toInt(),
                (it.value.cell.coordinates.x * h + 2 * h).toInt(),
                (it.value.cell.coordinates.y * 1.5 * w + 2 * w).toInt(),
                (it.value.cell.coordinates.x * h + 4 * h).toInt()
            )
        }
    }
}