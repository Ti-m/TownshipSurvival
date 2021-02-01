package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MainActivityHelperTest {
    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var transportManager: TransportManager
    private lateinit var gameStateManager: GameStateManager

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        val neighbourCalculator = HexagonNeighbourCalculator(mapManager)
        val emptyCellFinder = EmptyCellFinder(mapManager, neighbourCalculator)
        val nearbyWorldResourceFinder = NearbyWorldResourceFinder(mapManager, neighbourCalculator)
        transportManager = TransportManager(
            mapManager,
            BreadthFirstSearchRouting(mapManager, neighbourCalculator),
            emptyCellFinder,
            nearbyWorldResourceFinder
        )
        gameStateManager = GameStateManager(transportManager, mapManager)
    }

    @Test
    fun `Set initial spawner`() {
        MainActivityHelper.setInitialSpawner(gameStateManager, mapManager)
        val southEastEdge = mapManager.getSouthEastEdge()
        Assert.assertTrue(mapManager.queryBuilding(southEastEdge) is Spawner)
    }
}