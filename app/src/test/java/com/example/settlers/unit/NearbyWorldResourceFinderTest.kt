package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.util.TestDoubleRandom
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NearbyWorldResourceFinderTest {
    private lateinit var d: BasicTestDependencies

    @Before
    fun setup() {
        d = BasicTestDependencies()
    }

    @Test
    fun `find WorldResource, only one available`() {
        val first = Coordinates(1,1)
        d.gameStateManager.applyStates(listOf(
            GameState(first, Operator.Set, Type.WorldResource, Tree),
        ))

        val foundAt = d.nearbyWorldResourceFinder.find(start = d.coords, range = 3, worldResource = Tree)

        Assert.assertEquals(first, foundAt)
    }

    @Test
    fun `find WorldResource, two available`() {
        val first = Coordinates(1,1)
        val second = Coordinates(2,0)
        d.gameStateManager.applyStates(listOf(
            GameState(first, Operator.Set, Type.WorldResource, Tree),
            GameState(second, Operator.Set, Type.WorldResource, Tree),
        ))

        val foundAt = d.nearbyWorldResourceFinder.find(start = d.coords, range = 3, worldResource = Tree)

        Assert.assertEquals(first, foundAt)
    }

    @Test
    fun `find WorldResource, two available with random`() {
        val mapManager = MapManagerPreparedForTest()
        d = BasicTestDependencies(
            mapManager = mapManager,
            neighbourCalculator = ShuffledNeighbourCalculator(TestDoubleRandom(), mapManager)
        )

        val first = Coordinates(1,1)
        val second = Coordinates(2,0)
        d.gameStateManager.applyStates(listOf(
            GameState(first, Operator.Set, Type.WorldResource, Tree),
            GameState(second, Operator.Set, Type.WorldResource, Tree),
        ))

        val foundAt = d.nearbyWorldResourceFinder.find(start = d.coords, range = 3, worldResource = Tree)

        Assert.assertEquals(second, foundAt)
    }
}