package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HexagonNeighbourCalculatorTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun `getNeighbourOfCell ignore obstacles`() {
        val middle = Coordinates(3,1)
        val neighbours: List<Coordinates> = d.neighbourCalculator.getNeighboursOfCellDoubleCoords(
            coords = middle,
            ignoreObstacles = true
        )
        assertEquals(
            listOf(
                Coordinates(4, 2),
                Coordinates(2, 2),
                Coordinates(1, 1),
                Coordinates(2, 0),
                Coordinates(4, 0),
                Coordinates(5, 1)
            ), neighbours
        )
    }

    @Test
    fun `getNeighbourOfCell ignore touched`() {
        val middle = Coordinates(3,1)
        d.mapManager.findSpecificCell(Coordinates(2,2))!!.touched = true
        val neighbours: List<Coordinates> = d.neighbourCalculator.getNeighboursOfCellDoubleCoords(
            coords = middle,
            ignoreObstacles = true
        )
        assertEquals(
            listOf(
                Coordinates(4, 2),
                //Coordinates(2, 2), is touched
                Coordinates(1, 1),
                Coordinates(2, 0),
                Coordinates(4, 0),
                Coordinates(5, 1)
            ), neighbours
        )
    }

    @Test
    fun `getNeighbourOfCell with obstacles`() {
        val middle = Coordinates(3,1)
        val destination = Coordinates(4,2)
        d.gameStateManager.applyStates(listOf(GameState(destination, Operator.Set, Type.Building, Road())))
        val neighbours: List<Coordinates> = d.neighbourCalculator.getNeighboursOfCellDoubleCoords(middle, destination, false)
        assertEquals(
            listOf(
                Coordinates(4, 2)
            ), neighbours
        )
    }

    @Test
    fun `getNeighbourOfCell ignore tiles of grid, index smaller then zero`() {
        val middle = Coordinates(0,0)
        val neighbours: List<Coordinates> = d.neighbourCalculator.getNeighboursOfCellDoubleCoords(
            coords = middle,
            ignoreObstacles = true
        )
        assertEquals(
            listOf(
                Coordinates(1, 1),
                Coordinates(2, 0)
            ), neighbours
        )
    }

    @Test
    fun `getNeighbourOfCell ignore tiles of grid, index to high`() {
        val middle = Coordinates(7,3)
        val neighbours: List<Coordinates> = d.neighbourCalculator.getNeighboursOfCellDoubleCoords(
            coords = middle,
            ignoreObstacles = true
        )
        assertEquals(
            listOf(
                Coordinates(5, 3),
                Coordinates(6, 2),
            ), neighbours
        )
    }

    @Test
    fun `getNeighbourOfCell allow any building`() {
        val middle = Coordinates(0,0)
        d.gameStateManager.applyStates(listOf(GameState(Coordinates(1,1), Operator.Set, Type.Building, Lumberjack())))
        val neighbours: List<Coordinates> = d.neighbourCalculator.getNeighboursOfCellDoubleCoords(
            coords = middle,
            ignoreObstacles = false,
            allowAnyBuilding = true
        )
        assertEquals(
            listOf(
                Coordinates(1, 1)
            ), neighbours
        )
    }

    @Test
    fun `getNeighbourOfCell cells which have a road connection trivial Test`() {
        val middle = Coordinates(3,1)
        val actual = d.neighbourCalculator.getRoadConnections(middle)
        assertEquals(emptyList<RoadConnections>(), actual)
    }

    @Test
    fun `getNeighbourOfCell cells which have a road connection #1`() {
        val middle = Coordinates(3,1)
        d.gameStateManager.applyStates(listOf(
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Lumberjack()),
            GameState(Coordinates(2,0), Operator.Set, Type.Building, Road())
        ))
        val actual = d.neighbourCalculator.getRoadConnections(middle)
        assertEquals(
            listOf(
                RoadConnections.North,
                RoadConnections.NorthWest
            ), actual
        )
    }

    @Test
    fun `getNeighbourOfCell cells which have a road connection #2`() {
        val middle = Coordinates(3,1)
        d.gameStateManager.applyStates(listOf(
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Lumberjack()),
            GameState(Coordinates(4,2), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(5,1), Operator.Set, Type.Building, Tower()),
            GameState(Coordinates(4,0), Operator.Set, Type.Building, Townhall()),
        ))
        val actual = d.neighbourCalculator.getRoadConnections(middle)
        assertEquals(
            listOf(
                RoadConnections.SouthEast,
                RoadConnections.NorthEast,
                RoadConnections.SouthWest,
                RoadConnections.South
            ), actual
        )
    }

}