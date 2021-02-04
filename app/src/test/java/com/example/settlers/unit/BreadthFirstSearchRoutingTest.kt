package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.Route
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BreadthFirstSearchRoutingTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun setup() {
        d = BasicTestDependencies()
    }

    @Test
    fun `calcRoute to neighbour`() {
        val from = Coordinates(1,1)
        val to = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = d.routing.calcRoute(from, to)

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
        d.gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = d.routing.calcRoute(from, to)

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
        d.gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))


        val route = d.routing.calcRoute(from, to)

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
        d.gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            //Here is a Road missing GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))


        val route = d.routing.calcRoute(from, to)

        assertNull(route)
    }

    @Test
    fun `Don't allow routes through buildings`() {
        val from = Coordinates(1,1)
        val to = Coordinates(4,2)
        d.gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Lumberjack()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = d.routing.calcRoute(from, to)

        assertNull(route)
    }

    @Test
    fun `Route around buildings`() {
        val from = Coordinates(0,0)
        val to = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Lumberjack()),
            GameState(Coordinates(2,0), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(3,1), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val route = d.routing.calcRoute(from, to)

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
        d.gameStateManager.applyStates(listOf(
            GameState(from, Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
            GameState(to, Operator.Set, Type.Building, Lumberjack())
        ))

        val next = d.routing.calcRouteFirstStep(from, to)

        assertEquals(Coordinates(2,2), next)
    }
}