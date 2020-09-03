package com.example.settlers.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup

class GameWorld(private val tiles: List<FlagTile>, context: Context?) : ViewGroup(context) {
    companion object {
        val TAG = "GameWorld"
    }

    init {
        tiles.forEach {
            this.addView(it)
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

        tiles.forEach {
            val w = it.coords.w
            val h = it.coords.h
            if (it.cell.coordinates.y.rem(2) == 0) {
                it.layout(
                    (it.cell.coordinates.x * 1.5 * w).toInt(),
                    (it.cell.coordinates.y * 2 * h + 2 * h).toInt(),
                    (it.cell.coordinates.x * 1.5 * w + 2 * w).toInt(),
                    (it.cell.coordinates.y * 2 * h + 2 * h + 2 * h).toInt()
                )
            } else {
                it.layout(
                    (it.cell.coordinates.x * 1.5 * w).toInt(),
                    (it.cell.coordinates.y * 2 * h + h).toInt(),
                    (it.cell.coordinates.x * 1.5 * w + 2 * w).toInt(),
                    (it.cell.coordinates.y * 2 * h + 3 * h).toInt()
                )
            }
        }
    }

    private val overlayBackgroundPaint = Paint().apply {
        this.color = Color.WHITE
        this.style = Paint.Style.FILL_AND_STROKE
    }
}