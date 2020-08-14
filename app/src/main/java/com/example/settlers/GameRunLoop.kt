package com.example.settlers

import android.util.Log

class GameRunLoop(private val tiles: List<FlagTile>, private val cells: List<Cell>, private val transportManager: TransportManager) {
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
        transportManager.tick()
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