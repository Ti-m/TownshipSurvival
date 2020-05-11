package com.example.settlers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.settlers.MainActivity.Companion.flagDiameter
import com.example.settlers.MainActivity.Companion.flagDistance


class MainActivity : AppCompatActivity() {

    companion object {
        val flagDistance = 33.0f //33.0f
        val flagDiameter = flagDistance / 7
        val tileGridSize = 33
        val gameBoardBorder = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val zoomingLayout = ZoomingLayout(this)
        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
        zoomingLayout.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
        val gw2 = GameWorld(context = this, tileGridSize = tileGridSize)
        gw2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        zoomingLayout.addView(gw2)
        setContentView(zoomingLayout)
    }
}