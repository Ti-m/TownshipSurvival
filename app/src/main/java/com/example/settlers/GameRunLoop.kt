package com.example.settlers

import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.CompoundButton

class GameRunLoop(
    private val gameStateManager: GameStateManager,
    private val tileManager: TileManager
) {
    companion object {
        private val TAG = "GameRunLoop"
    }

//    fun calcTransports() {
//        Log.i(TAG, "calcTransports")
//
//    }

    fun tick() {
        Log.i(TAG, "tick")
        gameStateManager.tick()
        tileManager.redrawAllRequestedTiles()
    }
}

class GameRunLoopControlHandler(
    private val gameRunLoop: GameRunLoop,
    private val handler: Handler
) : CompoundButton.OnCheckedChangeListener,
    View.OnClickListener {

    companion object {
        private val TAG = "GameRunLoop"
        val delay = 1000L
    }

    var keepRunning = true

    val loop = object : Runnable {
        override fun run() {
//            todo()
                Log.i(TAG, "every second")
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