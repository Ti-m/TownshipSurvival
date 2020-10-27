package com.example.settlers.unit

import com.example.settlers.Coordinates
import com.example.settlers.GameStateCreator
import com.example.settlers.MapManagerPreparedForTest
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class GameStateCreatorTest {

    private lateinit var sut: GameStateCreator
    private lateinit var mapManager: MapManagerPreparedForTest

    @Before
    fun setUp() {
        mapManager = MapManagerPreparedForTest()
        sut = GameStateCreator()
    }

    @Test
    fun testCreateSapwnerAtFarEdge() {
        val coords: Coordinates = mapManager.getSouthEastEdge()
//        sut.createSpawner(coords)
//        val building = mapManager.queryBuilding(coords)
//        assertTrue(building is Spawner)
    }
}