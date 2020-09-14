package com.example.settlers

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import com.example.settlers.ui.GameWorld
import com.example.settlers.util.DefaultLogger
import com.example.settlers.util.RepeatHelper
import com.otaliastudios.zoom.ZoomApi.Companion.MAX_ZOOM_DEFAULT_TYPE
import com.otaliastudios.zoom.ZoomApi.Companion.MIN_ZOOM_DEFAULT_TYPE
import com.otaliastudios.zoom.ZoomLayout


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
        val flagDistance = 33.0f //33.0f
        val flagDiameter = flagDistance / 10
        val tileGridSize = 33
        val gameBoardBorder = (4 * flagDistance).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val zoomingLayout = ZoomLayout( context = this)
        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
        //zoomingLayout.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
        zoomingLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        zoomingLayout.isHorizontalScrollBarEnabled = true
        zoomingLayout.isVerticalScrollBarEnabled = true
        zoomingLayout.setMinZoom(4.0f, MIN_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setMaxZoom(8.0f, MAX_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setHasClickableChildren(true)

        val logger = DefaultLogger()

        val mapGen = MapGenerator(TerrainInterpolator())
        val cells = mapGen.createMap(tileGridSize)

        val mapManager = MapManager(cells, logger, tileGridSize)
        val transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)
        val gameStateManager = GameStateManager(transportManager, mapManager, logger)
        val buildDialogHandler = BuildDialogHandler(gameStateManager)
        val tiles = mapGen.createTiles(cells, buildDialogHandler, this)
        val gw2 = GameWorld(tiles = tiles, context = this)
        //gw2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        gw2.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
//        gw2.setBackgroundColor(Color.parseColor("#aaaaaa"))
        zoomingLayout.addView(gw2)
        setContentView(zoomingLayout)

        val gameRunLoop = GameRunLoop(
            tiles = tiles,
            gameStateManager = gameStateManager
        )
        val delay = 1000L
        RepeatHelper.repeatDelayed(delay) {
            Log.i(TAG, "every second")
            gameRunLoop.tick()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouchEvent")
        return super.onTouchEvent(event)
    }
}

