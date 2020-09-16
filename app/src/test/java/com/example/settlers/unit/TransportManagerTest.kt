package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class TransportManagerTest {

    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var sut: TransportManager
    private lateinit var logger: Logger
    private lateinit var gameStateManager: GameStateManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        logger = DisabledLogger()
        sut = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager, HexagonNeighbourCalculator(mapManager)), logger)
        gameStateManager = GameStateManager(sut, mapManager, logger)
        coords = Coordinates(0,0)
    }

    @Test
    fun `whereIsNextResourceInTransportWithAccess from same tile`() {
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Building, Road())
        ))
        mapManager.resetTouched()

        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInTransportWithAccess(TransportRequest(coords, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsNextResourceInTransportWithAccess from one tile over`() {
        val dest = Coordinates(2,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        mapManager.resetTouched()

        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInTransportWithAccess(TransportRequest(dest, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsNextResourceInStorageWithAccess from same tile`() {
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Building, Road())
        ))
        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInStorageWithAccess(TransportRequest(coords, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsNextResourceInStorageWithAccess from one tile over`() {
        val dest = Coordinates(2,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInStorageWithAccess(TransportRequest(dest, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `moveResources step #1`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameState(Coordinates(x=0, y=0), Operator.Remove, Type.Storage, Wood),
            GameState(Coordinates(x=1, y=1), Operator.Set, Type.Transport, Wood)
        ), result)
    }

    @Test
    fun `moveResources step #2`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(Coordinates(1,1), Operator.Set, Type.Transport, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.resetTouched()

        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameState(Coordinates(x=1, y=1), Operator.Remove, Type.Transport, Wood),
            GameState(Coordinates(x=2, y=2), Operator.Set, Type.Transport, Wood)
        ), result)
    }

    @Test
    fun `moveResources step #3`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(dest, Operator.Set, Type.Transport, Wood),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf<GameState>(), result)
    }

    @Test
    fun `moveResources already touched`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.findSpecificCell(Coordinates(0,0))!!.touched = true
        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf<GameState>(
        ), result)
    }

}