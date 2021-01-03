package com.example.settlers

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.settlers.databinding.ViewTopBarBinding
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import com.example.settlers.ui.BuildDialogCallback
import com.example.settlers.ui.GameWorld
import com.example.settlers.util.DefaultLogger
import com.otaliastudios.zoom.ZoomApi.Companion.MAX_ZOOM_DEFAULT_TYPE
import com.otaliastudios.zoom.ZoomApi.Companion.MIN_ZOOM_DEFAULT_TYPE
import com.otaliastudios.zoom.ZoomLayout
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
        val flagDistance = 33.0f //33.0f
        val flagDiameter = flagDistance / 20
        val tileGridSize = 33
        val gameBoardBorder = (4 * flagDistance).toInt()
    }

    private lateinit var bindingViewTopBar: ViewTopBarBinding

    private val handler = Handler(Looper.getMainLooper())
    lateinit var buildDialogClickHandler: BuildDialogCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val baseLayout = layoutInflater.inflate(R.layout.activity_main, null)
        val constraintLayout = ConstraintLayout(this)
        bindingViewTopBar = ViewTopBarBinding.inflate(layoutInflater, constraintLayout, false)
        val topBar = bindingViewTopBar.root

        val zoomingLayout = ZoomLayout( context = this)
        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
        //zoomingLayout.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
        zoomingLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        zoomingLayout.isHorizontalScrollBarEnabled = true
        zoomingLayout.isVerticalScrollBarEnabled = true
        zoomingLayout.setMinZoom(4.0f, MIN_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setMaxZoom(8.0f, MAX_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setHasClickableChildren(true)
        zoomingLayout.id = View.generateViewId()//creates error message "Invalid ID" in logcat, but seems to work anyway?

        val logger = DefaultLogger()

        val mapGen = MapGenerator(TerrainInterpolator(), Random)
        val cells = mapGen.createMap(tileGridSize)

        val mapManager = MapManager(cells, logger, tileGridSize)
        val neighbourCalculator = HexagonNeighbourCalculator(mapManager)
        val transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager, neighbourCalculator), logger)
        val gameStateManager = GameStateManager(transportManager, mapManager, logger)

        MainActivityHelper.createInitialState(gameStateManager, mapManager)
        MainActivityHelper.setAZombie(gameStateManager)
        MainActivityHelper.setExplosion(gameStateManager)

        val modeController = ModeController()
        val tileManager = TileManager(tiles = mapGen.createTiles(cells, modeController, neighbourCalculator, this))
        val gw2 = GameWorld(tileManager = tileManager, context = this)
        //gw2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        gw2.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
//        gw2.setBackgroundColor(Color.parseColor("#aaaaaa"))
        zoomingLayout.addView(gw2)
        constraintLayout.addView(topBar)
        constraintLayout.addView(zoomingLayout)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(zoomingLayout.id, ConstraintSet.TOP, topBar.id, ConstraintSet.BOTTOM)
        constraintSet.applyTo(constraintLayout)

        setContentView(constraintLayout)

        val gameRunLoop = GameRunLoop(
            tileManager = tileManager,
            gameStateManager = gameStateManager
        )

        val switchHandler = GameRunLoopControlHandler(gameRunLoop = gameRunLoop, handler = handler)
        bindingViewTopBar.switchAutoPause.setOnCheckedChangeListener(switchHandler)
        bindingViewTopBar.stepButton.setOnClickListener(switchHandler)
        bindingViewTopBar.switchBuildMode.setOnCheckedChangeListener(modeController)

        val buildDialogHandler = BuildDialogHandler(gameStateManager)

        //This Proxy keeps the TileManager out of the BuildDialogHandler
        buildDialogClickHandler = object : BuildDialogCallback {
            override fun selectedCallback(selectedBuilding: Building, coordinates: Coordinates) {
                buildDialogHandler.selectedCallback(selectedBuilding, coordinates)
                tileManager.redrawAllTiles()
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouchEvent")
        return super.onTouchEvent(event)
    }
}

object MainActivityHelper {
    fun createInitialState(gameStateManager: GameStateManager, mapManager: MapManager) {
        val gameStateCreator = GameStateCreator()
        gameStateManager.applyStates(gameStateCreator.G1_L2_T3_unfinishedRoad())//TODO for debugging
        //Set initial spawner
        gameStateManager.applyState(gameStateCreator.createSpawner(mapManager.getSouthEastEdge()))

        //Finish construction of the tower to allow shooting
        //TODO Remove this again
        mapManager.getCellsWithTowers().values.first().building!!.setConstructionFinished()
    }

    fun setAZombie(gameStateManager: GameStateManager) {
        val gameStateCreator = GameStateCreator()
        gameStateManager.applyState(gameStateCreator.createZombie(Coordinates(29,11)))
    }

    fun setExplosion(gameStateManager: GameStateManager) {
        val gameStateCreator = GameStateCreator()
        gameStateManager.applyState(gameStateCreator.createExplosion(Coordinates(9,9)))
    }
}