package com.example.settlers.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import com.example.settlers.*
import com.example.settlers.databinding.ViewTopBarBinding
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import com.example.settlers.util.DefaultLogger
import com.otaliastudios.zoom.ZoomApi
import com.otaliastudios.zoom.ZoomLayout
import kotlin.random.Random

class GameFragment : Fragment() {

    private lateinit var bindingViewTopBar: ViewTopBarBinding

    private val handler = Handler(Looper.getMainLooper())

    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //val baseLayout = layoutInflater.inflate(R.layout.activity_main, null)
        val constraintLayout = ConstraintLayout(requireActivity())
        bindingViewTopBar = ViewTopBarBinding.inflate(layoutInflater, constraintLayout, false)
        val topBar = bindingViewTopBar.root

        val zoomingLayout = ZoomLayout( context = requireActivity())
        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
        zoomingLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        zoomingLayout.isHorizontalScrollBarEnabled = true
        zoomingLayout.isVerticalScrollBarEnabled = true
        zoomingLayout.setMinZoom(2.0f, ZoomApi.MIN_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setMaxZoom(16.0f, ZoomApi.MAX_ZOOM_DEFAULT_TYPE)
        zoomingLayout.setHasClickableChildren(true)
        zoomingLayout.id = View.generateViewId()//creates error message "Invalid ID" in logcat, but seems to work anyway?

        val logger = DefaultLogger()

        val randomGenerator = Random
        val keyValueStorage = DefaultKeyValueStorage(requireActivity().getSharedPreferences("GameStateStorage", Context.MODE_PRIVATE))
        val mapGen = MapGenerator(TerrainInterpolator(randomGenerator), randomGenerator)
        val mapSaver = MapSaver(model.cells, mapGen, keyValueStorage)

        val mapManager = MapManager(model.cells, logger, MainActivity.tileGridSize)
        val neighbourCalculator = HexagonNeighbourCalculator(mapManager)
        val shuffledNeighbourCalculator = ShuffledNeighbourCalculator(randomGenerator, mapManager)
        val emptyCellFinder = EmptyCellFinder(mapManager, shuffledNeighbourCalculator)
        val nearbyWorldResourceFinder = NearbyWorldResourceFinder(mapManager, shuffledNeighbourCalculator)
        val towerTargetFinder = TowerTargetFinder(mapManager, neighbourCalculator)
        val zombieTargetFinder = ZombieTargetFinder(mapManager, neighbourCalculator)
        val nextItemWithAccessFinder = NextItemWithAccessFinder(mapManager, neighbourCalculator)
        val transportManager = TransportManager(
            mapManager,
            BreadthFirstSearchRouting(neighbourCalculator),
            emptyCellFinder,
            nearbyWorldResourceFinder,
            towerTargetFinder,
            zombieTargetFinder,
            nextItemWithAccessFinder
        )
        val gameStateManager = GameStateManager(transportManager, mapManager, logger)

        //MainActivityHelper.createInitialState(gameStateManager, mapManager)
        //MainActivityHelper.setInitialSpawner(gameStateManager, mapManager)
        //MainActivityHelper.setAZombie(gameStateManager)
        //MainActivityHelper.setExplosion(gameStateManager)

//        val cTown = Coordinates(0,0)
//        val cRoad1 = Coordinates(2,0)
//        val cRoad2 = Coordinates(4,0)
//        val cRoad3 = Coordinates(5,1)
////        val cLumber = Coordinates(1,1)
////        val cFletcher1 = Coordinates(4,2)
//        val cLumberMill1 = Coordinates(6,0)
//        val tree1 = Coordinates(3,1)
//        val tree2 = Coordinates(7,1)
//        val cLumberMill2 = Coordinates(7,1)
//        val cLumberMill3 = Coordinates(6,2)
//        val tree3 = Coordinates(0,2)
//
//        gameStateManager.applyStates(listOf(
//            GameStateCreator.createTownhall(cTown),
//            GameStateCreator.createRoad(cRoad1),
//            GameStateCreator.createRoad(cRoad2),
//            GameStateCreator.createRoad(cRoad3),
//            GameStateCreator.createLumbermill(cLumberMill1),
//            GameStateCreator.createLumbermill(cLumberMill2),
//            GameStateCreator.createLumbermill(cLumberMill3),
////            GameStateCreator.createLumberjack(cLumber),
////            GameStateCreator.createFletcher(cFletcher1),
//            GameStateCreator.createTree(tree1),
//            GameStateCreator.createTree(tree2),
//            GameStateCreator.createTree(tree3),
//        ))

        val modeController = ModeController()
        val isLowDpi = resources.displayMetrics.density < 2
        val tileManager = TileManager(tiles = mapGen.createTiles(requireActivity(), model.cells, modeController, neighbourCalculator, isLowDpi))
        val gw2 = GameWorld(tileManager = tileManager, context = requireActivity())
        //gw2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        gw2.layoutParams = ViewGroup.LayoutParams(
            MainActivity.gameBoardBorderWidth + MainActivity.tileGridSize * MainActivity.flagDistance.toInt(),
            MainActivity.gameBoardBorderHeight + MainActivity.tileGridSize * MainActivity.flagDistance.toInt()
        )
//        gw2.setBackgroundColor(Color.parseColor("#aaaaaa"))
        zoomingLayout.addView(gw2)
        constraintLayout.addView(topBar)
        constraintLayout.addView(zoomingLayout)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(zoomingLayout.id, ConstraintSet.TOP, topBar.id, ConstraintSet.BOTTOM)
        constraintSet.applyTo(constraintLayout)

        val gameRunLoop = GameRunLoop(
            tileManager = tileManager,
            gameStateManager = gameStateManager,
            mapSaver = mapSaver
        )

        val switchHandler = GameRunLoopControlHandler(gameRunLoop = gameRunLoop, handler = handler, log = logger)
        bindingViewTopBar.switchAutoPause.setOnCheckedChangeListener(switchHandler)
        bindingViewTopBar.stepButton.setOnClickListener(switchHandler)
        bindingViewTopBar.switchBuildMode.setOnCheckedChangeListener(modeController)
        
        val buildDialogHandler = BuildDialogHandler(gameStateManager)

        val drawLoop = DrawLoop(gameRunLoop, handler, logger)
        drawLoop.start()

        //This Proxy keeps the TileManager out of the BuildDialogHandler
        (requireActivity() as MainActivity).buildDialogClickHandler = object : BuildDialogCallback {
            override fun selectedCallback(selectedBuilding: Building, coordinates: Coordinates) {
                buildDialogHandler.selectedCallback(selectedBuilding, coordinates)
                gameRunLoop.tickGraphics()
            }
        }

        val inspectDialogHandler = InspectDialogHandler(mapManager)

        (requireActivity() as MainActivity).inspectDialogClickHandler = object : InspectDialogCallback {
            override fun inspectCallback(coordinates: Coordinates, stopDelivery: StopDeliveryState) {
                inspectDialogHandler.inspectCallback(coordinates, stopDelivery)
                gameRunLoop.tickGraphics()
            }
        }


        return constraintLayout
    }
}