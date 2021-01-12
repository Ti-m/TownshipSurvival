package com.example.settlers

import android.os.Handler
import android.view.View
import android.widget.CompoundButton
import com.example.settlers.util.Logger
import kotlin.math.log

class GameRunLoop(
    private val gameStateManager: GameStateManager,
    private val tileManager: TileManager
) {
    companion object {
        private val TAG = "GameRunLoop"
    }

    fun tick() {

        //First calculate a new gamestate
        gameStateManager.tick()
        //Then redraw everything
        tileManager.redrawAllTiles()
    }
}

class GameRunLoopControlHandler(
    private val gameRunLoop: GameRunLoop,
    private val handler: Handler,
    private val log: Logger
) : CompoundButton.OnCheckedChangeListener,
    View.OnClickListener {

    companion object {
        private val TAG = "GameRunLoop"
        val delay = 1000L
    }

    var keepRunning = true

    val loop = object : Runnable {
        override fun run() {
                log.logi(TAG, "every second")
                gameRunLoop.tick()
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
        gameRunLoop.tick()
    }

}