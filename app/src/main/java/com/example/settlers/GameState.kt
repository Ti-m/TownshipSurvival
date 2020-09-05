package com.example.settlers

//Any is Resource or Building type currently
data class GameState(val coordinates: Coordinates, val operator: Operator, val type: Type, val data: Any)

enum class Operator { Set, Remove }
enum class Type { Resource, Offered, Building }
//abstract class GameObject
//abstract class BuildingType : GameObject()
//class Townhall : BuildingType()
//class Lumberjack : BuildingType()
//class Road : BuildingType()
//
//class Resource { Wood, Stone }