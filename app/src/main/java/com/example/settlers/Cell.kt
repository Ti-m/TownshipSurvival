package com.example.settlers


class Cell(
    var coordinates: Coordinates,
    var type: GroundType,
    var building: BuildingType? = null,
    var carrier: Boolean = false,
    var worker: Worker? = null,
    var ressource1: Ressource? = null,
    var ressource2: Ressource? = null,
    var requires: List<Ressource>? = null,
    var offers: List<Ressource>? = null,
    val value: Double//Used in map generation, remove?
)

enum class GroundType { Water, Grass, Desert, Mountain }
enum class BuildingType { Townhall, Lumberjack, Road }

class Coordinates(val x: Int, val y: Int)

class Ressource
class Worker