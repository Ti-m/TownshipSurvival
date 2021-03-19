package com.example.settlers

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import com.example.settlers.ui.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
        val flagDistance = 64.0f
        val flagDiameter = flagDistance / 40
        val tileGridSize = 33
        val gameBoardBorder = (4 * flagDistance).toInt()
    }

    lateinit var buildDialogClickHandler: BuildDialogCallback
    lateinit var inspectDialogClickHandler: InspectDialogCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //https://developer.android.com/guide/navigation/navigation-getting-started#navigate
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val model: MainViewModel by viewModels()


        val randomGenerator = Random
        val keyValueStorage = DefaultKeyValueStorage(getSharedPreferences("GameStateStorage", Context.MODE_PRIVATE))
        val mapGen = MapGenerator(TerrainInterpolator(randomGenerator), randomGenerator)
        val mapSaver = MapSaver(model.cells, mapGen, keyValueStorage)

        if (mapSaver.isSaveAvailable()) {
            navController.navigate(LaunchScreenFragmentDirections.actionLaunchScreenFragmentToGameFragment())
        } else {
            mapSaver.load()
            if (mapSaver.isSaveAvailable()) {
                navController.navigate(LaunchScreenFragmentDirections.actionLaunchScreenFragmentToGameFragment())
            } else {
                navController.navigate(LaunchScreenFragmentDirections.actionLaunchScreenFragmentToStartMenuFragment())
            }
        }
    }
}

object MainActivityHelper {
    fun createInitialState(gameStateManager: GameStateManager) {
        gameStateManager.applyStates(GameStateCreator.G1_L2_T3_unfinishedRoad())
    }

    fun setInitialSpawner(gameStateManager: GameStateManager, mapManager: MapManager) {
        gameStateManager.applyState(GameStateCreator.createSpawner(mapManager.getSouthEastEdge()))
    }

    fun setAZombie(gameStateManager: GameStateManager) {
        gameStateManager.applyState(GameStateCreator.createZombie(Coordinates(29,11)))
    }

    fun setExplosion(gameStateManager: GameStateManager) {
        gameStateManager.applyState(GameStateCreator.createExplosion(Coordinates(9,9)))
    }
}