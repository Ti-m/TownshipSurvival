package com.example.settlers


class Cell(
    var coordinates: Coordinates,
    var type: GroundType,
    var building: Building? = null,
    //var carrier: Boolean = false,
    var worker: Worker? = null,
    var resource1: Resource? = null,
    var resource2: Resource? = null,
    var requires: List<Resource>? = null, //TODO require/ offer here or in the building?
    var offers: MutableList<Resource> = mutableListOf(),
    var redraw: Boolean = false,
    val value: Double//Used in map generation, remove?
)

data class Coordinates(val x: Int, val y: Int)
enum class GroundType { Water, Grass, Desert, Mountain }
enum class BuildingType { Townhall, Lumberjack, Road }
enum class Resource { Wood, Stone }
enum class Worker { Construction }

abstract class Building {
    abstract val type: BuildingType
    abstract var requires: MutableList<Resource>
    abstract var offers: MutableList<Resource>
    abstract var requested: MutableList<Resource>

    fun markRequested(item: Resource) {
        offers.remove(item)
        requested.add(item)
    }
}
class Townhall : Building() {
    override val type: BuildingType = BuildingType.Townhall
    override var requires: MutableList<Resource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override var offers: MutableList<Resource> = mutableListOf(Resource.Wood, Resource.Wood, Resource.Wood, Resource.Stone, Resource.Stone, Resource.Stone)
    override var requested: MutableList<Resource> = mutableListOf()
}

class Lumberjack : Building() {
    override val type: BuildingType = BuildingType.Lumberjack
    override var requires: MutableList<Resource> = mutableListOf(Resource.Wood, Resource.Wood)
    override var offers: MutableList<Resource> = mutableListOf()
    override var requested: MutableList<Resource> = mutableListOf()
}

class Road : Building() {
    override val type: BuildingType = BuildingType.Road
    override var requires: MutableList<Resource> = mutableListOf()
    override var offers: MutableList<Resource> = mutableListOf()
    override var requested: MutableList<Resource> = mutableListOf()
}
