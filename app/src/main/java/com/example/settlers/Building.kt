package com.example.settlers

abstract class Building : GameObject() {

    //TODO shall these counters also be part of the GameStateObjects? Or is it ok, that the
    // buildings handle stuff on their own?
    //raises from 0 to 100
    abstract var productionCount: Int
    abstract var constructionCount: Int

    fun construct() {
        if (isConstructed()) return
        for (x in 0..9) {
            if (constructionCount < 100) {
                constructionCount += 1
            }
        }
    }

    fun setConstructionFinished() { constructionCount = 100 }

    //This is a blueprint for the items needed to produce an item
    abstract val requiresProduction: MutableList<Resource>
    //This is a blueprint for the items needed to construct a building
    abstract val requiresConstruction: MutableList<Resource>
    //This is a blueprint for the items produced in the moment construction of the building happens and be put into storage
    abstract val offers: MutableList<Resource>
//    abstract var requested: MutableList<Resource>

    fun isConstructed() : Boolean {
        return constructionCount == 100
    }

    fun isConstructionInProgress() : Boolean {
        return constructionCount in 1..99
    }

    fun isProductionInProgress() : Boolean {
        return productionCount in 1..99
    }

    //How many percents of a produce are handled within one tick while producing
    abstract val productionTimeMultiplier: Int

    fun isProductionBuilding(): Boolean = producesItem != null && !isWorldResourceProductionBuilding()

    //When producing, the building consumes this WorldResource
    abstract val produceConsumesWorldResource: WorldResource?
    //Consumes a WorldResource when producing
    fun isWorldResourceProductionBuilding(): Boolean = produceConsumesWorldResource != null

    //Which GameObject is produced
    abstract val producesItem: GameObject?
    //How should the produced item be Stored
    abstract val producesItemOutputType: Type?

    fun produce(coordinates: Coordinates): Collection<GameState> {
        if (producesItem == null) return emptyList()
        if (!isConstructed()) return emptyList()
        val result = mutableListOf<GameState>()
        for (x in 0 until productionTimeMultiplier) {
            productionCount += 1
            if (productionCount == 100) {
                result.add(GameState(coordinates, Operator.Set, producesItemOutputType!!, producesItem))
                productionCount = 0
            }
        }
        return result
    }

    fun removeProductionRequirementsFromProduction(coordinates: Coordinates) : Collection<GameState> {
        return requiresProduction.map {
            GameState(coordinates = coordinates, operator = Operator.Remove, type = Type.Production, data = it)
        }
    }

    fun removeConstructionRequirementsFromProduction(coordinates: Coordinates) : Collection<GameState> {
        return requiresConstruction.map {
            GameState(coordinates = coordinates, operator = Operator.Remove, type = Type.Production, data = it)
        }
    }
}

class Townhall : Building() {

    //no build time
    override var constructionCount: Int = 100
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: MutableList<Resource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override val requiresProduction: MutableList<Resource> = mutableListOf()

    override val offers: MutableList<Resource> = mutableListOf(Wood, Wood, Wood, Stone, Stone, Stone)
}

class Lumberjack : Building() {

    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource = Tree
    override val producesItem: GameObject = Wood
    override val producesItemOutputType: Type = Type.Storage

    override val requiresConstruction: MutableList<Resource> = mutableListOf(Wood, Wood)
    override val requiresProduction: MutableList<Resource> = mutableListOf()

    override val offers: MutableList<Resource> = mutableListOf()

}

class Stonemason : Building() {

    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource = Rock
    override val producesItem: GameObject = Stone
    override val producesItemOutputType: Type = Type.Storage

    override val requiresConstruction: MutableList<Resource> = mutableListOf(Wood, Wood)
    override val requiresProduction: MutableList<Resource> = mutableListOf()

    override val offers: MutableList<Resource> = mutableListOf()

}

class Road : Building() {
    //no build time
    override var constructionCount: Int = 100
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: MutableList<Resource> = mutableListOf()
    override val requiresProduction: MutableList<Resource> = mutableListOf()

    override val offers: MutableList<Resource> = mutableListOf()
}

class Tower : Building() {
    override var constructionCount: Int = 0
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: MutableList<Resource> = mutableListOf(Wood, Stone, Stone)
    override val requiresProduction: MutableList<Resource> = mutableListOf(Arrow)

    override val offers: MutableList<Resource> = mutableListOf()
    val range = 3
}

class Spawner : Building() {
    override var constructionCount: Int = 100//no build time yet

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 1
    override val produceConsumesWorldResource: WorldResource? = null
    override val producesItem: GameObject = Zombie
    override val producesItemOutputType: Type = Type.MovingObject

    override val requiresConstruction: MutableList<Resource> = mutableListOf()
    override val requiresProduction: MutableList<Resource> = mutableListOf()
    override val offers: MutableList<Resource> = mutableListOf()

}

class Fletcher : Building() {
    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val producesItem: GameObject = Arrow
    override val producesItemOutputType: Type = Type.Storage

    override val requiresConstruction: MutableList<Resource> = mutableListOf(Wood, Wood)
    override val requiresProduction: MutableList<Resource> = mutableListOf(Wood)
    override val offers: MutableList<Resource> = mutableListOf()
}

class Pyramid : Building() {
    override var constructionCount: Int = 0
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 1
    override val produceConsumesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: MutableList<Resource> = MutableList(100) { Wood }
    override val requiresProduction: MutableList<Resource> = mutableListOf()

    override val offers: MutableList<Resource> = mutableListOf()
}