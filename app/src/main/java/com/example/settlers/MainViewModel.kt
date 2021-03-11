package com.example.settlers

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.settlers.terrain.MapGenerator

/*
* SavedStateHandle:
* The game data (cells: Map<Coordinates, Cell>) is to big to be saved in a Bundle as SavedStateHandle.
* It needs to be saved in Room.
* State like ButtonState is currently saved automatically. why?
* SavedStateHandle can be re added later, if it's needed.
* */

//class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
class MainViewModel : ViewModel() {

    //Contains the game state
    private var cells: Map<Coordinates, Cell>? = null

    //TODO replace MapGenerator injection with Hilt field injection?
    fun initCells(mapGenerator: MapGenerator) {
        if (cells == null) {
            cells = mapGenerator.createMap(MainActivity.tileGridSize)
        }
//        val cells: Map<Coordinates, Cell>? = savedStateHandle["cells"]
//        if (cells == null) {
//            Log.i("MainViewModel", "Cells not in SavedStateHandle. Creating new ...")
//            val fetched = mapGenerator.createMap(MainActivity.tileGridSize)
//            savedStateHandle["cells"] = fetched
//        }
    }

    fun getCells(): Map<Coordinates, Cell> {
        return cells!!
    //return savedStateHandle["cells"]!!
    }
}