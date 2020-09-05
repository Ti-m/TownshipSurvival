package com.example.settlers

/*
A cell can offer resources, in case a transport is aborted
A Building can offer resources (Like a warehouse)
A cell can't request resources
A building can request resources
* */

data class Cell(
    var coordinates: Coordinates,
    var type: GroundType,
    var building: Building? = null,
    //var carrier: Boolean = false,
    var worker: Worker? = null,
    var resource1: Resource? = null,
    var resource2: Resource? = null,
    //var requires: List<Resource>? = null, //TODO require/ offer here or in the building?
    var offers: MutableList<Resource> = mutableListOf(),
    var redraw: Boolean = false,
    //val value: Double//Used in map generation, remove?
)

data class Coordinates(val x: Int, val y: Int)
enum class GroundType { Water, Grass, Desert, Mountain }
enum class Worker { Construction }


