package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class TransportManagerTest {

    private lateinit var mapManager: MapManager
    private lateinit var sut: TransportManager
    private lateinit var logger: Logger
    private lateinit var gameStateManager: GameStateManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        mapManager = MapManagerTestData()
        logger = DisabledLogger()
        sut = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)
        gameStateManager = GameStateManager(sut, mapManager, logger)
        coords = Coordinates(0,0)
    }

    @Test
    fun `whereIsResourceInTransportAt from same tile`() {
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Building, Road())
        ))
        //The coordinates are irrelevant here
        val result = sut.whereIsResourceinTransportAt(TransportRequest(coords, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsResourceInTransportAt from one tile over`() {
        val dest = Coordinates(2,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        //The coordinates are irrelevant here
        val result = sut.whereIsResourceinTransportAt(TransportRequest(dest, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsResourceInStoragedAt from same tile`() {
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Building, Road())
        ))
        //The coordinates are irrelevant here
        val result = sut.whereIsResourceInStoragedAt(TransportRequest(coords, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsResourceInStorageAt from one tile over`() {
        val dest = Coordinates(2,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        //The coordinates are irrelevant here
        val result = sut.whereIsResourceInStoragedAt(TransportRequest(dest, Wood))

        assertEquals(coords, result)
    }
}