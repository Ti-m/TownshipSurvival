package com.example.settlers

abstract class Building : GameObject() {

    //TODO shall these counters also be part of the GameStateObjects? Or is it ok, that the
    // buildings handle stuff on their own?
    //raises from 0 to 100
    abstract var productionCount: Int?
    abstract var constructionCount: Int?

    abstract fun produce(coordinates: Coordinates): Collection<GameState>
    fun construct() {
        if (constructionCount == null) return
        for (x in 0..9) {
            if (constructionCount!! < 100) {
                constructionCount = constructionCount!! + 1
            }
        }
    }

    fun setConstructionFinished() { constructionCount = 100 }

    //This is a blueprint for the items needed to construct a building
    abstract var requires: MutableList<Resource>
    //This is a blueprint for the items produced in the moment construction of the building happens and be put into storage
    abstract var offers: MutableList<Resource>
//    abstract var requested: MutableList<Resource>

    fun isConstructed() : Boolean {
        return constructionCount == 100
    }
}

class Townhall : Building() {

    //no build time
    override var constructionCount: Int? = null
    //no production
    override var productionCount: Int? = null

    override var requires: MutableList<Resource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override var offers: MutableList<Resource> = mutableListOf(Wood, Wood, Wood, Stone, Stone, Stone)

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        return emptyList()
    }
}

class Lumberjack : Building() {

    override var constructionCount: Int? = 0
    override var productionCount: Int? = 0

    override var requires: MutableList<Resource> = mutableListOf(Wood, Wood)
    override var offers: MutableList<Resource> = mutableListOf()

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        if (!isConstructed()) return emptyList()
        val result = mutableListOf<GameState>()
        for (x in 0..9) {
            productionCount = productionCount!! + 1
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
    override var constructionCount: Int? = null
    //no production
    override var productionCount: Int? = null

    override var requires: MutableList<Resource> = mutableListOf()
    override var offers: MutableList<Resource> = mutableListOf()

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        return emptyList()
    }
}

class Tower : Building() {
    override var constructionCount: Int? = 0
    //no production //TODO can i use this for shooting "progress"?
    override var productionCount: Int? = null

    override var requires: MutableList<Resource> = mutableListOf(Wood, Stone, Stone)
    override var offers: MutableList<Resource> = mutableListOf()

    override fun produce(coordinates: Coordinates): Collection<GameState> {
        return emptyList()
    }
}
