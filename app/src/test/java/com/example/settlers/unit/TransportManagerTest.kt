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

    @Before
    fun prepare() {
        mapManager = MapManagerTestData()
        logger = DisabledLogger()
        sut = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)
        gameStateManager = GameStateManager(sut, mapManager, logger)
    }

    @Test
    fun tick_convertTransportToStorage() {
        val coords = Coordinates(0,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Required, Wood)
        ))
        gameStateManager.applyStates(sut.convertTransportToStorage())
        assertEquals(listOf(Wood), mapManager.queryInStorage(coords))
    }

    @Test
    fun tick_convertStorageToProduction() {
        val coords = Coordinates(0,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Required, Wood)
        ))
        gameStateManager.applyStates(sut.convertStorageToProduction())
        assertEquals(listOf(Wood), mapManager.queryInProduction(coords))
    }

    @Test
    fun tick_convertTransportToProduction_bothSet() {
        val coords = Coordinates(0,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Required, Wood)
        ))
        gameStateManager.applyStates(sut.convertStorageToProduction())
        assertEquals(listOf(Wood), mapManager.queryInProduction(coords))
    }
}