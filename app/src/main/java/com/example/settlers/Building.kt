package com.example.settlers

abstract class Building : GameObject() {

    //TODO shall these counters also be part of the GameStateObjects? Or is it ok, that the
    // buildings handle stuff on their own?
    //raises from 0 to 100
    abstract var productionCount: Int
    abstract var constructionCount: Int

    abstract fun produce(coordinates: Coordinates): Collection<GameState>

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
    abstract var requiresProduction: MutableList<Resource>
    //This is a blueprint for the items needed to construct a building
    abstract var requiresConstruction: MutableList<Resource>
    //This is a blueprint for the items produced in the moment construction of the building happens and be put into storage
    abstract var offers: MutableList<Resource>
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

    open var isProductionBuilding = false

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

    override var requiresConstruction: MutableList<Resource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override var requiresProduction: MutableList<Resource> = mutableListOf()

    override var offers: MutableList<Resource> = mutableListOf(Wood, Wood, Wood, Stone, Stone, Stone)

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        return emptyList()
    }
}

class Lumberjack : Building() {

    override var constructionCount: Int = 0
    override var productionCount: Int = 0

    override var requiresConstruction: MutableList<Resource> = mutableListOf(Wood, Wood)
    override var requiresProduction: MutableList<Resource> = mutableListOf()

    override var offers: MutableList<Resource> = mutableListOf()

    override var isProductionBuilding: Boolean = true

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        if (!isConstructed()) return emptyList()
        val result = mutableListOf<GameState>()
        for (x in 0..9) {
            productionCount = productionCount + 1
            if (productionCount == 100) {
                result.add(GameState(coordinates, Operator.Set, Type.Storage, Wood))
                productionCount = 0
            }
        }
        return result
    }
}

class Road : Building() {
    //no build time
    override var constructionCount: Int = 100
    //no production
    override var productionCount: Int = 0

    override var requiresConstruction: MutableList<Resource> = mutableListOf()
    override var requiresProduction: MutableList<Resource> = mutableListOf()

    override var offers: MutableList<Resource> = mutableListOf()

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        return emptyList()
    }
}

class Tower : Building() {
    override var constructionCount: Int = 0
    //no production //TODO can i use this for shooting "progress"?
    override var productionCount: Int = 0

    override var requiresConstruction: MutableList<Resource> = mutableListOf(Wood, Stone, Stone)
    override var requiresProduction: MutableList<Resource> = mutableListOf(Arrow)

    override var offers: MutableList<Resource> = mutableListOf()
    val range = 3

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        return emptyList()
    }
}

class Spawner : Building() {
    override var constructionCount: Int = 100//no build time yet
    override var productionCount: Int = 0

    override var isProductionBuilding: Boolean = true

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        if (!isConstructed()) return emptyList()
        val result = mutableListOf<GameState>()
        productionCount += 1
        if (productionCount == 100) {
            result.add(GameState(coordinates, Operator.Set, Type.MovingObject, Zombie))
            productionCount = 0
        }
        return result
    }

    override var requiresConstruction: MutableList<Resource> = mutableListOf()
    override var requiresProduction: MutableList<Resource> = mutableListOf()
    override var offers: MutableList<Resource> = mutableListOf()

}

class Fletcher : Building() {
    override var productionCount: Int = 0
    override var constructionCount: Int = 0

    override var isProductionBuilding: Boolean = true

    override var requiresConstruction: MutableList<Resource> = mutableListOf(Wood, Wood)
    override var requiresProduction: MutableList<Resource> = mutableListOf(Wood)
    override var offers: MutableList<Resource> = mutableListOf()

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        if (!isConstructed()) return emptyList()
        val result = mutableListOf<GameState>()
        for (x in 0..9) {
            productionCount = productionCount!! + 1
            if (productionCount == 100) {
                result.add(GameState(coordinates, Operator.Set, Type.Storage, Arrow))
                productionCount = 0
            }
        }
        return result
    }

}