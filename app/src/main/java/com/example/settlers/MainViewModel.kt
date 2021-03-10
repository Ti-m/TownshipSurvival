package com.example.settlers

import androidx.lifecycle.ViewModel
import com.example.settlers.terrain.MapGenerator

class MainViewModel : ViewModel() {

    //Contains the game state
    var cells: Map<Coordinates, Cell>? = null

    //TODO replace MapGenerator injection with Hilt field injection?
    fun initCells(mapGenerator: MapGenerator) {
        if (cells == null) {
            cells = mapGenerator.createMap(MainActivity.tileGridSize)
        }
    }
}