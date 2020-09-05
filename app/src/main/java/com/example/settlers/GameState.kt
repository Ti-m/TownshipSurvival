package com.example.settlers

//Any is Resource or Building type currently
data class GameState(val coordinates: Coordinates, val operator: Operator, val type: Type, val data: GameObject)

enum class Operator { Set, Remove }
enum class Type { Resource, Offered, Building }

abstract class GameObject

abstract class Resource : GameObject()
object Wood : Resource()
object Stone : Resource()

abstract class Building : GameObject(){
    abstract var requires: MutableList<Resource>
    abstract var offers: MutableList<Resource>
    abstract var requested: MutableList<Resource>
}
class Townhall : Building() {
    override var requires: MutableList<Resource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override var offers: MutableList<Resource> = mutableListOf(Wood, Wood, Wood, Stone, Stone, Stone)
    override var requested: MutableList<Resource> = mutableListOf()
}

class Lumberjack : Building() {
    override var requires: MutableList<Resource> = mutableListOf(Wood, Wood)
    override var offers: MutableList<Resource> = mutableListOf()
    override var requested: MutableList<Resource> = mutableListOf()
}

class Road : Building() {
    override var requires: MutableList<Resource> = mutableListOf()
    override var offers: MutableList<Resource> = mutableListOf()
    override var requested: MutableList<Resource> = mutableListOf()
}
