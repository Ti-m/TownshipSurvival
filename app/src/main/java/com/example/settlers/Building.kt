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
    abstract val requiresProduction: List<Resource>
    //This is a blueprint for the items needed to construct a building
    abstract val requiresConstruction: List<Resource>
    //This is a blueprint for the items produced in the moment construction of the building happens and be put into storage
    abstract val offers: List<Resource>
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

    fun isProductionBuilding(): Boolean = producesItem != null && !isWorldResourceConsumingProductionBuilding()

    //When producing, the building consumes this WorldResource
    abstract val produceConsumesWorldResource: WorldResource?
    //Consumes a WorldResource when producing
    fun isWorldResourceConsumingProductionBuilding(): Boolean = produceConsumesWorldResource != null

    //When producing, the building creates this WorldResource
    abstract val produceCreatesWorldResource: WorldResource?
    //Creates a WorldResource when producing
    fun isWorldResourceCreatingProductionBuilding(): Boolean = produceCreatesWorldResource != null

    //Which GameObject is produced
    abstract val producesItem: GameObject?
    //How should the produced item be Stored
    abstract val producesItemOutputType: Type?

    fun produce(coordinates: Coordinates): Collection<GameState> {
        if (producesItem == null && produceCreatesWorldResource == null) return emptyList()
        if (!isConstructed()) return emptyList()
        val result = mutableListOf<GameState>()
        for (x in 0 until productionTimeMultiplier) {
            productionCount += 1
            if (productionCount == 100) {
                if (producesItem != null) {
                    //if producesItem == null, produce is only a timer to know when the next WorldResource is created
                    result.add(GameState(coordinates, Operator.Set, producesItemOutputType!!, producesItem))
                }
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

    //Stop delivery of items
    var stopDelivery: Boolean = false
}

class Townhall : Building() {

    //no build time
    override var constructionCount: Int = 100
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: MutableList<Resource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override val requiresProduction: MutableList<Resource> = mutableListOf()

    override val offers: MutableList<Resource> = mutableListOf(Lumber, Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone)
}

class Lumberjack : Building() {

    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource = Tree
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject = Wood
    override val producesItemOutputType: Type = Type.Storage

    override val requiresConstruction: List<Resource> = listOf(Lumber, Lumber)
    override val requiresProduction: List<Resource> = listOf()

    override val offers: List<Resource> = listOf()

}

class Stonemason : Building() {

    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource = Rock
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject = Stone
    override val producesItemOutputType: Type = Type.Storage

    override val requiresConstruction: List<Resource> = listOf(Lumber, Lumber)
    override val requiresProduction: List<Resource> = listOf()

    override val offers: List<Resource> = listOf()

}

class Forester : Building() {

    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 5
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource = Tree
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: List<Resource> = listOf(Lumber, Lumber)
    override val requiresProduction: List<Resource> = listOf()

    override val offers: MutableList<Resource> = mutableListOf()

}

class Road : Building() {
    //no build time
    override var constructionCount: Int = 100
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: List<Resource> = listOf()
    override val requiresProduction: List<Resource> = listOf()

    override val offers: MutableList<Resource> = mutableListOf()
}

class Tower : Building() {
    override var constructionCount: Int = 0
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: List<Resource> = listOf(Lumber, Stone, Stone)
    override val requiresProduction: List<Resource> = listOf(Arrow, Arrow, Arrow)

    override val offers: MutableList<Resource> = mutableListOf()
    val range = 3
}

class Spawner : Building() {
    override var constructionCount: Int = 100//no build time yet

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 1
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject = Zombie
    override val producesItemOutputType: Type = Type.MovingObject

    override val requiresConstruction: List<Resource> = listOf()
    override val requiresProduction: List<Resource> = listOf()
    override val offers: List<Resource> = listOf()

}

class Fletcher : Building() {
    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject = Arrow
    override val producesItemOutputType: Type = Type.Storage

    override val requiresConstruction: List<Resource> = listOf(Lumber, Lumber, Stone)
    override val requiresProduction: List<Resource> = listOf(Wood)
    override val offers: List<Resource> = listOf()
}

class Lumbermill : Building() {
    override var constructionCount: Int = 0

    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 10
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject = Lumber
    override val producesItemOutputType: Type = Type.Storage

    override val requiresConstruction: List<Resource> = listOf(Lumber, Lumber, Stone)
    override val requiresProduction: List<Resource> = listOf(Wood)
    override val offers: List<Resource> = listOf()
}

class Pyramid : Building() {
    override var constructionCount: Int = 0
    //no production
    override var productionCount: Int = 0
    override val productionTimeMultiplier: Int = 1
    override val produceConsumesWorldResource: WorldResource? = null
    override val produceCreatesWorldResource: WorldResource? = null
    override val producesItem: GameObject? = null
    override val producesItemOutputType: Type? = null

    override val requiresConstruction: List<Resource> = mutableListOf<Resource>().apply {
        this.addAll(MutableList(50) { Lumber })
        this.addAll(MutableList(50) { Stone })
    }
    override val requiresProduction: List<Resource> = listOf()

    override val offers: List<Resource> = listOf()
}