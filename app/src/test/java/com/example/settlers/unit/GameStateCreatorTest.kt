package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class GameStateCreatorTest {

    private lateinit var sut: GameStateCreator
    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var gameStateManager: GameStateManager

    @Before
    fun setUp() {
        mapManager = MapManagerPreparedForTest()
        gameStateManager = GameStateManagerPreparedForTest(mapManager)
    }

    @Test
    fun testCreateSapwnerAtFarEdge() {
        val coords: Coordinates = mapManager.getSouthEastEdge()
        gameStateManager.applyState(GameStateCreator.createSpawner(coords))
        val building = mapManager.queryBuilding(coords)
        assertTrue(building is Spawner)
    }
}