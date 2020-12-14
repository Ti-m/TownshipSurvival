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
    Animation
}

abstract class GameObject

abstract class Resource : GameObject()
object Wood : Resource()
object Stone : Resource()

abstract class MovingObject : GameObject()
object Zombie : MovingObject() {
    val health = 1
    val dmg = 1
}