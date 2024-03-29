package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.util.TestDoubleRandom
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EmptyCellFinderTest {
    private lateinit var d: BasicTestDependencies

    @Before
    fun setup() {
        d = BasicTestDependencies()
    }

    @Test
    fun `find empty cell for WorldResource, only starting coords are blocked`() {
        val forester = Coordinates(1,1)
        d.gameStateManager.applyStates(listOf(
            //This is simply for blocking the spot in which the  forester would be.
            //Otherwise the algorithm will find the starting point.
            //Any obstacle will do here
            GameState(forester, Operator.Set, Type.Building, Forester()),
        ))

        val foundAt = d.emptyCellFinder.find(start = forester, range = 3)

        assertEquals(Coordinates(2,2), foundAt)
    }

    @Test
    fun `find empty cell for WorldResource, starting coords and first match are blocked`() {
        val forester = Coordinates(1,1)
        d.gameStateManager.applyStates(listOf(
            //This is simply for blocking the spot in which the  forester would be.
            //Otherwise the algorithm will find the starting point.
            //Any obstacle will do here
            GameState(forester, Operator.Set, Type.Building, Forester()),
            //Block the first viable target too
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Road()),
        ))

        val foundAt = d.emptyCellFinder.find(start = forester, range = 3)

        assertEquals(Coordinates(0,2), foundAt)
    }

    @Test
    fun `find empty cell for WorldResource, add a random seed`() {
        val mapManager = MapManagerPreparedForTest()
        d = BasicTestDependencies(
            mapManager = mapManager,
            neighbourCalculator = ShuffledNeighbourCalculator(TestDoubleRandom(), mapManager)
        )
        val forester = Coordinates(1,1)
        d.gameStateManager.applyStates(listOf(
            //This is simply for blocking the spot in which the  forester would be.
            //Otherwise the algorithm will find the starting point.
            //Any obstacle will do here
            GameState(forester, Operator.Set, Type.Building, Forester()),
        ))

        val foundAt = d.emptyCellFinder.find(start = forester, range = 3)

        assertEquals(Coordinates(0,0), foundAt)
    }

    @Test
    fun `find empty cell for WorldResource, ignore water`() {
        val forester = Coordinates(0,0)
        val target = Coordinates(3,1)
        d = BasicTestDependencies(
            mapManager = MapManagerPreparedForTest(
                cells = mapOf(
                    Pair(forester, Cell(coordinates = forester,type = GroundType.Desert)),
                    Pair(Coordinates(2,0), Cell(coordinates = Coordinates(2,0),type = GroundType.Water)),
                    Pair(Coordinates(1,1), Cell(coordinates = Coordinates(1,1),type = GroundType.Water)),
                    Pair(target, Cell(coordinates = target,type = GroundType.Desert)),
                ),
                mapSize = 2
            )
        )
        d.gameStateManager.applyStates(listOf(
            //This is simply for blocking the spot in which the  forester would be.
            //Otherwise the algorithm will find the starting point.
            //Any obstacle will do here
            GameState(forester, Operator.Set, Type.Building, Forester()),
        ))

        val foundAt = d.emptyCellFinder.find(start = forester, range = 3)
        //Find the only empty cell without water
        assertEquals(target, foundAt)
    }
}