package com.example.settlers

import java.io.Serializable

//CellBase handles the utility functions for proper drawing from the Cell.
open class CellBase(
    //Touched in Round x ignore this round for transports
    var touched: Boolean = false,
    // Stores a random number which defines the texture variant, if multiple are available
    var textureVariant: Int = 0,
)

/*
A cell can offer resources, in case a transport is aborted
A Building can offer resources (Like a warehouse)
A cell can't request resources
A building can request resources
* */

//This class is only manipulated by GameStateManger with GameState Objects.
//Cell contains the game logic related data
data class Cell(
    var coordinates: Coordinates,
    var type: GroundType,
    //Has trees, stones, ore etc.
    var worldResource: WorldResource? = null, //TODO make this a list to allow multiple?
    var building: Building? = null,
    //var carrier: Boolean = false,
    var worker: Worker? = null,
    //Zombies i.e, what else? Merge with workers? Rename this to enemy, because this is only used for hostiles?
    var movingObject: MovingObject? = null,
    //Resources in transport. visible on the map
    var transport: MutableList<Resource> = mutableListOf(),
    //This resources are requested for transport
    var requires: MutableList<Resource> = mutableListOf(),
    //This resources are available for transport
    var storage: MutableList<Resource> = mutableListOf(),
    //This resources are NOT available for transport, but are used for a pending production
    var production: MutableList<Resource> = mutableListOf(),
    //The GameState will trigger an animation through setting this value. The value is the animation in the next tick
    var animation: Animation? = null,
) : CellBase()

data class Coordinates(val x: Int, val y: Int) : Serializable
data class TargetCoordinates(val start: Coordinates, val path: List<Coordinates>, val destination: Coordinates)
enum class GroundType { Water, Grass, Desert, Mountain }
enum class Worker { Construction }
