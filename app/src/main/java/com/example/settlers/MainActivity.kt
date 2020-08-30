package com.example.settlers

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import com.example.settlers.ui.GameWorld
import com.example.settlers.util.DefaultLogger
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
//        setContentView(R.layout.activity_main)
        val zoomingLayout = ZoomLayout( context = this)
        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
        //zoomingLayout.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
        zoomingLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        zoomingLayout.isHorizontalScrollBarEnabled = true
        zoomingLayout.isVerticalScrollBarEnabled = true
        zoomingLayout.setMinZoom(4.0f, MIN_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setMaxZoom(8.0f, MAX_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setHasClickableChildren(true)
//Das zoomen geht ejtz aber die scale informationg elangt nicht in den subview...
//        workaround doer doch lieber wieder das eigene. Prinzipiell ist das zoom verhalten mit dem zoomlayout schon schöner...
        val fragmentManager = supportFragmentManager

        val logger = DefaultLogger()

        val mapGen = MapGenerator(TerrainInterpolator())
        val cells = mapGen.createMap(tileGridSize)
        //val transports = mutableListOf<Transport>()

        //val transportManager = TransportManager(cells = cells)
        val mapManager = MapManager(cells, logger)
        val transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)
        val tiles = mapGen.createTiles(cells,transportManager,fragmentManager,this, mapManager)
        val gw2 = GameWorld(tiles = tiles, context = this)
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
        val gameRunLoop = GameRunLoop(tiles = tiles, mapManager = mapManager, transportManager = transportManager)
        val delay = 1000L
        RepeatHelper.repeatDelayed(delay) {
            Log.i(TAG, "every second")
            gameRunLoop.tick()
        }
    }
}

