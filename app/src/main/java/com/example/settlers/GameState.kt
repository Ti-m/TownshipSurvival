package com.example.settlers

//Any is Resource or Building type currently
data class GameState(val coordinates: Coordinates, val operator: Operator, val type: Type, val data: GameObject?)

enum class Operator { Set, Remove }
//Always move Transport -> Storage -> Production
enum class Type {
    Transport, // In transport
    Storage, // available for transport
    Production, // Not available for transport
    Required,
    Building,
    MovingObject, //Enemeys i.e.
    Animation,
    Damage, //To Moving Objects, Buildings
    WorldResource //Tree, Stone, Ore...
}

abstract class GameObject

abstract class Resource : GameObject()
object Wood : Resource()
object Stone : Resource()

abstract class WorldResource : GameObject()
object Tree : WorldResource()
object Rock : WorldResource()

abstract class Ammunition : Resource() {
    abstract val damage: Int
}
object Arrow : Ammunition(){
    override val damage = 1
}

abstract class MovingObject : GameObject() {
    abstract val health: Int
    abstract val damage: Int
}
object Zombie : MovingObject() {
    override val health = 1
    override val damage = 1
}

class Damage(val value: Int): GameObject()