package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class GameStateCreatorTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun setUp() {
        d = BasicTestDependencies()
    }

    @Test
    fun testCreateSapwnerAtFarEdge() {
        val coords: Coordinates = d.mapManager.getSouthEastEdge()
        d.gameStateManager.applyState(GameStateCreator.createSpawner(coords))
        val building = d.mapManager.queryBuilding(coords)
        assertTrue(building is Spawner)
    }
}