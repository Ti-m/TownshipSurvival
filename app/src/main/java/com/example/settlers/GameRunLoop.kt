package com.example.settlers

import android.util.Log

class GameRunLoop(private val tiles: List<FlagTile>, private val mapManager: MapManager, private val transportManager: TransportManagerNew) {
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
            if (it.cell.redraw) {
                Log.i(TAG, "need to redraw")
                it.invalidate()
                it.cell.redraw = false
            }
        }
    }
}