package com.example.settlers

import android.util.Log
import com.example.settlers.ui.FlagTile

class TileManager(val tiles: Map<Coordinates, FlagTile>) {

    companion object {
        private val TAG = "TileManager"
    }

    fun redrawAllRequestedTiles() {
        tiles.forEach {
            if (it.value.cell.redraw) {
                Log.i(TAG, "need to redraw")
                it.value.invalidate()
                it.value.cell.redraw = false
            }
        }
    }

    fun redrawTileWithCoordinates(coordinates: Coordinates) {
        (tiles[coordinates] ?: error("The coordinates are not part of the map!")).invalidate()
    }
}