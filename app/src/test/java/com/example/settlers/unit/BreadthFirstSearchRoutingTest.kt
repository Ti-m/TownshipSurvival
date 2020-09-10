package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.Route
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BreadthFirstSearchRoutingTest {

    private lateinit var mapManager: MapManager
    private lateinit var transportManager: TransportManager
    private lateinit var logger: Logger
    private lateinit var gameStateManager: GameStateManager
    private lateinit var sut: BreadthFirstSearchRouting

    @Before
    fun setup() {
        mapManager = MapManagerTestData()
        logger = DisabledLogger()
        sut = BreadthFirstSearchRouting(mapManager)
        transportManager = TransportManager(mapManager, sut, logger)
        gameStateManager = GameStateManager(transportManager, mapManager, logger)
    }

    @Test
    fun calcRoute() {
        val from = Coordinates(1,1)
        val to = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                current = Coordinates(1,1),
                steps = mutableListOf(
                    Coordinates(2,2)
                )
            ),
            route
        )
    }

    @Test
    fun calcRoute2() {
        val from = Coordinates(1,1)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                current = Coordinates(1,1),
                steps = mutableListOf(
                    Coordinates(2,2),
                    Coordinates(4,2)
                )
            ),
            route
        )
    }

    @Test
    fun calcRoute3() {
        val from = Coordinates(0,0)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))


        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                current = Coordinates(0,0),
                steps = mutableListOf(
                    Coordinates(1,1),
                    Coordinates(2,2),
                    Coordinates(4,2)
                )
            ),
            route
        )
    }

    @Test
    fun calcRoute_RoadMissing() {
        val from = Coordinates(0,0)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            //Here is a Road missing GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))


        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                current = Coordinates(0,0),
                steps = mutableListOf(
                    Coordinates(1,1),
                    Coordinates(2,2),
                    Coordinates(4,2)
                )
            ),
            route
        )
    }
}