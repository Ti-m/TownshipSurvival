package com.example.settlers


class Cell(
    var coordinates: Coordinates,
    var type: GroundType,
    var building: Building? = null,
    //var carrier: Boolean = false,
    var worker: Worker? = null,
    var ressource1: Ressource? = null,
    var ressource2: Ressource? = null,
    var requires: List<Ressource>? = null, //TODO require/ offer here or in the building?
    var offers: List<Ressource>? = null,
    var redraw: Boolean = false,
    val value: Double//Used in map generation, remove?
)

data class Coordinates(val x: Int, val y: Int)
enum class GroundType { Water, Grass, Desert, Mountain }
enum class BuildingType { Townhall, Lumberjack, Road }
enum class Ressource { Wood, Stone }
enum class Worker { Construction }

abstract class Building {
    abstract val type: BuildingType
    abstract var requires: MutableList<Ressource>
    abstract var offers: MutableList<Ressource>
    abstract var requested: MutableList<Ressource>

    fun markRequested(item: Ressource) {
        offers.remove(item)
        requested.add(item)
    }
}
class Townhall : Building() {
    override val type: BuildingType = BuildingType.Townhall
    override var requires: MutableList<Ressource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override var offers: MutableList<Ressource> = mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)
    override var requested: MutableList<Ressource> = mutableListOf()
}

class Lumberjack : Building() {
    override val type: BuildingType = BuildingType.Lumberjack
    override var requires: MutableList<Ressource> = mutableListOf(Ressource.Wood, Ressource.Wood)
    override var offers: MutableList<Ressource> = mutableListOf()
    override var requested: MutableList<Ressource> = mutableListOf()
}

class Road : Building() {
    override val type: BuildingType = BuildingType.Road
    override var requires: MutableList<Ressource> = mutableListOf()
    override var offers: MutableList<Ressource> = mutableListOf()
    override var requested: MutableList<Ressource> = mutableListOf()
}
