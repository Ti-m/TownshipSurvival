package com.example.settlers

//Any is Resource or Building type currently
data class GameState(val coordinates: Coordinates, val operator: Operator, val type: Type, val data: GameObject)

enum class Operator { Set, Remove }
//Always move Transport -> Storage -> Production
enum class Type {
    Transport, // In transport
    Storage, // available for transport
    Production, // Not available for transport
    Required,
    Building
}

abstract class GameObject

abstract class Resource : GameObject()
object Wood : Resource()
object Stone : Resource()

abstract class Building : GameObject(){
    //This is a blueprint for the items needed to construct a building
    abstract var requires: MutableList<Resource>
    //This is a blueprint for the items produced in the moment construction of the building happens and be put into storage
    abstract var offers: MutableList<Resource>
//    abstract var requested: MutableList<Resource>
}
class Townhall : Building() {
    override var requires: MutableList<Resource> = mutableListOf()//mutableListOf(Ressource.Wood, Ressource.Wood, Ressource.Stone, Ressource.Stone, Ressource.Stone)//TODO set the real cost, atm its for free
    override var offers: MutableList<Resource> = mutableListOf(Wood, Wood, Wood, Stone, Stone, Stone)
//    override var requested: MutableList<Resource> = mutableListOf()
}

class Lumberjack : Building() {
    override var requires: MutableList<Resource> = mutableListOf(Wood, Wood)
    override var offers: MutableList<Resource> = mutableListOf()
//    override var requested: MutableList<Resource> = mutableListOf()
}

class Road : Building() {
    override var requires: MutableList<Resource> = mutableListOf()
    override var offers: MutableList<Resource> = mutableListOf()
//    override var requested: MutableList<Resource> = mutableListOf()
}
