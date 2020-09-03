package com.example.settlers

import android.util.Log
import com.example.settlers.ui.FlagTile

class GameRunLoop(private val tiles: Map<Coordinates, FlagTile>, private val mapManager: MapManager, private val transportManager: TransportManager) {
    companion object {
        private val TAG = "GameRunLoop"
    }

//    fun calcTransports() {
//        Log.i(TAG, "calcTransports")
//
//    }

    fun tick() {
        moveRessources()
        redraw()
    }

    private fun moveRessources() {
        Log.i(TAG, "moveRessources")
        val states = transportManager.tick()
        mapManager.applyStates(states)
    }

    private fun redraw() {
        tiles.forEach {
            if (it.value.cell.redraw) {
                Log.i(TAG, "need to redraw")
                it.value.invalidate()
                it.value.cell.redraw = false
            }
        }
    }
}