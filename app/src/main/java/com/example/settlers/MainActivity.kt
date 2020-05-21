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
import com.otaliastudios.zoom.ZoomApi.Companion.MAX_ZOOM_DEFAULT_TYPE
import com.otaliastudios.zoom.ZoomApi.Companion.MIN_ZOOM_DEFAULT_TYPE
import com.otaliastudios.zoom.ZoomLayout


class MainActivity : AppCompatActivity() {

    companion object {
        val flagDistance = 33.0f //33.0f
        val flagDiameter = flagDistance / 7
        val tileGridSize = 65
        val gameBoardBorder = (4 * flagDistance).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        val zoomingLayout = ZoomLayout( context = this)
        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
        //zoomingLayout.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
        zoomingLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        zoomingLayout.isHorizontalScrollBarEnabled = true
        zoomingLayout.isVerticalScrollBarEnabled = true
        zoomingLayout.setMinZoom(1.0f, MIN_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setMaxZoom(8.0f, MAX_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setHasClickableChildren(true)
//Das zoomen geht ejtz aber die scale informationg elangt nicht in den subview...
//        workaround doer doch lieber wieder das eigene. Prinzipiell ist das zoom verhalten mit dem zoomlayout schon schöner...
        val gw2 = GameWorld(tileGridSize = tileGridSize, context = this)
        //gw2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        gw2.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
//        gw2.setBackgroundColor(Color.parseColor("#aaaaaa"))
        zoomingLayout.addView(gw2)
        setContentView(zoomingLayout)
//        setContentView(gw2)

//        val zoomingLayout = ZoomingLayout(this)
//        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
//        zoomingLayout.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
//        val gw2 = GameWorld(context = this, parent = zoomingLayout, tileGridSize = tileGridSize)
//        gw2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        zoomingLayout.addView(gw2)
//        setContentView(zoomingLayout)
    }
}