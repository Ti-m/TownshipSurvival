package com.example.settlers

import android.os.Handler
import android.view.View
import android.widget.CompoundButton
import com.example.settlers.util.Logger

class GameRunLoop(
    private val gameStateManager: GameStateManager,
    private val tileManager: TileManager,
    private val mapSaver: MapSaver
) {
    companion object {
        private const val TAG = "GameRunLoop"
    }

    private var tickCount: Long = 0L

    fun tickProgress() {
        tickCount +=1
        if (tickCount % 200 == 0L) {
            gameStateManager.setNextSpawner()
        }
        //First calculate a new gamestate
        gameStateManager.tick()
        //save after each round
        mapSaver.save()
    }

    fun tickGraphics() {
        tileManager.redrawAllTiles()
    }
}

class GameRunLoopControlHandler(
    private val gameRunLoop: GameRunLoop,
    private val handler: Handler,
    private val log: Logger,
) : CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    companion object {
        private const val TAG = "GameRunLoopControlHandler"
        private const val delay = 1000L
    }

    private var keepRunning = true

    private val loop = object : Runnable {
        override fun run() {
                log.logi(TAG, "progressLoop")
                gameRunLoop.tickProgress()
            if (keepRunning) {
                handler.postDelayed(this, delay)
            }
        }
    }

    //TODO Use innerclasses or something else to set the interfaces?

    //CompoundButton.OnCheckedChangeListener
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        keepRunning = isChecked
        if(isChecked) {
            handler.postDelayed(loop, delay)
        }
    }

    //View.OnClickListener
    override fun onClick(v: View?) {
        gameRunLoop.tickProgress()
    }
}

class DrawLoop(
    private val gameRunLoop: GameRunLoop,
    private val handler: Handler,
    private val log: Logger,
) {
    companion object {
        private const val TAG = "DrawLoop"
        private const val delay = 1000L
    }

    private val loop = object : Runnable {
        override fun run() {
            log.logi(TAG, "drawLoop")
            gameRunLoop.tickGraphics()
            handler.postDelayed(this, delay)
        }
    }

    fun start() {
        handler.postDelayed(loop, delay)
    }
}