package com.example.settlers

import java.io.Serializable

/*
A cell can offer resources, in case a transport is aborted
A Building can offer resources (Like a warehouse)
A cell can't request resources
A building can request resources
* */

data class Cell(
    var coordinates: Coordinates,
    var type: GroundType,
    //Has trees, cacteen, stones etc.
    var hasResources: Boolean = false,
    var building: Building? = null,
    //var carrier: Boolean = false,
    var worker: Worker? = null,
    //Zombies i.e, what else? Merge with workers?
    var movingObject: MovingObject? = null,
    //Resources in transport. visible on the map
    var transport: MutableList<Resource> = mutableListOf(),
    //This resources are requested for transport
    var requires: MutableList<Resource> = mutableListOf(),
    //This resources are available for transport
    var storage: MutableList<Resource> = mutableListOf(),
    //This resources are NOT available for transport, but are used for a pending production
    var production: MutableList<Resource> = mutableListOf(),
    var redraw: Boolean = false,
    //Touched in Round x ignore this round for transports
    var touched: Boolean = false
    //val value: Double//Used in map generation, remove?
)

data class Coordinates(val x: Int, val y: Int) : Serializable
enum class GroundType { Water, Grass, Desert, Mountain }
enum class Worker { Construction }


