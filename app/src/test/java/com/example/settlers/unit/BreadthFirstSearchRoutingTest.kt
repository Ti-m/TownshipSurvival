package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.Route
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BreadthFirstSearchRoutingTest {

    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var transportManager: TransportManager
    private lateinit var gameStateManager: GameStateManager
    private lateinit var sut: BreadthFirstSearchRouting

    @Before
    fun setup() {
        mapManager = MapManagerPreparedForTest()
        sut = BreadthFirstSearchRouting(mapManager, HexagonNeighbourCalculator(mapManager))
        transportManager = TransportManagerPreparedForTest(mapManager, sut)
        gameStateManager = GameStateManagerPreparedForTest(transportManager, mapManager)
    }

    @Test
    fun `calcRoute to neighbour`() {
        val from = Coordinates(1,1)
        val to = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = sut.calcRoute(from, to)

        assertEquals(
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
    fun `calcRoute two steps`() {
        val from = Coordinates(1,1)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = sut.calcRoute(from, to)

        assertEquals(
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
    fun `calcRoute 3 steps`() {
        val from = Coordinates(0,0)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))


        val route = sut.calcRoute(from, to)

        assertEquals(
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
    fun `calcRoute road missing, so no route`() {
        val from = Coordinates(0,0)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            //Here is a Road missing GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))


        val route = sut.calcRoute(from, to)

        assertNull(route)
    }

    @Test
    fun `Don't allow routes through buildings`() {
        val from = Coordinates(1,1)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Lumberjack()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = sut.calcRoute(from, to)

        assertNull(route)
    }

    @Test
    fun `Route around buildings`() {
        val from = Coordinates(0,0)
        val to = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Lumberjack()),
            GameState(Coordinates(2,0), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(3,1), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = sut.calcRoute(from, to)

        assertEquals(
            Route(
                current = Coordinates(0,0),
                steps = mutableListOf(
                    Coordinates(2,0),
                    Coordinates(3,1),
                    Coordinates(2,2)
                )
            ),
            route
        )
    }

    @Test
    fun `calcRouteFirstStep 2 steps`() {
        val from = Coordinates(1,1)
        val to = Coordinates(4,2)
        gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val next = sut.calcRouteFirstStep(from, to)

        assertEquals(Coordinates(2,2), next)
    }

    @Test
    fun `findNextItemWithAccessInStorage 1 available 2 away`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(available, Operator.Set, Type.Storage, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.resetTouched()

        val foundAt = sut.findNextItemWithAccessInStorage(requester, Wood)

        assertEquals(available, foundAt)
    }

    @Test
    fun `findNextItemWithAccessInStorage 2 available at 2 different tiles`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(available, Operator.Set, Type.Storage, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Storage, Wood),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.resetTouched()

        val foundAt = sut.findNextItemWithAccessInStorage(requester, Wood)

        assertEquals(Coordinates(1,1), foundAt)
    }

    @Test
    fun `findNextItemWithAccessInTransport nothing available`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))

        val foundAt = sut.findNextItemWithAccessInTransport(requester, Wood)

        assertNull(foundAt)
    }

    @Test
    fun `findNextItemWithAccessInTransport 2 available at 2 different tiles`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(available, Operator.Set, Type.Transport, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Transport, Wood),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.resetTouched()

        val foundAt = sut.findNextItemWithAccessInTransport(requester, Wood)

        assertEquals(Coordinates(1,1), foundAt)
    }
}