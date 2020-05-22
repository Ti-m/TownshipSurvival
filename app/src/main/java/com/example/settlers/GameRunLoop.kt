package com.example.settlers

import android.util.Log

class GameRunLoop(private val tiles: List<FlagTile>, private val cells: List<Cell>, private val transports: MutableList<Transport>) {
    companion object {
        private val TAG = "GameRunLoop"
    }

//    fun calcTransports() {
//        Log.i(TAG, "calcTransports")
//
//    }

    fun moveRessources() {
        Log.i(TAG, "moveRessources")
        transports.removeIf { it.route.count() < 2 }
        transports.forEach {
            Log.i(TAG, "moveRessources - doSomething")
            it.tick(cells)
        }
    }

    fun redraw() {
        tiles.forEach {
            if (it.cell.redraw) {
                Log.i(TAG, "need to redraw")
                it.invalidate()
                it.cell.redraw = false
            }
        }
    }
}