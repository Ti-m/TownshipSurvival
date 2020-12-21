package com.example.settlers

class GameStateCreator {

    private val T1 = Coordinates(2, 0)
    private val T2 = Coordinates(1, 1)
    private val T3 = Coordinates(2, 2)
    private val R2 = Coordinates(5, 1)
    //    private val R3 = Coordinates(7, 1)
    private val L1 = Coordinates(7, 1)
    private val L2 = Coordinates(6, 0)
    private val L3 = Coordinates(6, 2)

    fun L3_T3_unfinishedRoad(): List<GameState> {
        return listOf(
            GameState(T1, Operator.Set, Type.Building, Townhall()),
            GameState(T2, Operator.Set, Type.Building, Townhall()),
            GameState(T3, Operator.Set, Type.Building, Townhall()),
            GameState(R2, Operator.Set, Type.Building, Road()),
//            GameState(R3, Operator.Set, Type.Building, Road()),
            GameState(L1, Operator.Set, Type.Building, Lumberjack()),
            GameState(L2, Operator.Set, Type.Building, Lumberjack()),
            GameState(L3, Operator.Set, Type.Building, Lumberjack()),
        )
    }

    fun G1_L2_T3_unfinishedRoad(): List<GameState> {
        return listOf(
            GameState(T1, Operator.Set, Type.Building, Townhall()),
            GameState(T2, Operator.Set, Type.Building, Townhall()),
            GameState(T3, Operator.Set, Type.Building, Townhall()),
            GameState(R2, Operator.Set, Type.Building, Road()),
//            GameState(R3, Operator.Set, Type.Building, Road()),
            GameState(L1, Operator.Set, Type.Building, Lumberjack()),
            GameState(L2, Operator.Set, Type.Building, Tower()),
            GameState(L2, Operator.Set, Type.Production, Arrow),//TODO remove here again?
            GameState(L3, Operator.Set, Type.Building, Lumberjack()),
        )
    }

    fun createSpawner(coords: Coordinates): GameState {
        return GameState(coords, Operator.Set, Type.Building, Spawner())
    }

    fun createZombie(coords: Coordinates): GameState {
        return GameState(coords, Operator.Set, Type.MovingObject, Zombie)
    }

    fun createExplosion(coords: Coordinates): GameState {
        return GameState(coords, Operator.Set, Type.Animation, ExplosionAnimation())
    }

}