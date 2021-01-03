package com.example.settlers

import com.example.settlers.ui.FlagTile

class TileManager(val tiles: Map<Coordinates, FlagTile>) {

    companion object {
        private val TAG = "TileManager"
    }

    fun redrawAllTiles() {
        tiles.forEach {
            it.value.invalidate()
        }
    }

//    fun redrawTileWithCoordinates(coordinates: Coordinates) {
//        (tiles[coordinates] ?: error("The coordinates are not part of the map!")).invalidate()
//    }
}