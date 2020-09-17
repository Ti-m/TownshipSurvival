package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapManagerTest {

    private lateinit var sut: MapManagerPreparedForTest
    private lateinit var gameStateManager: GameStateManager
    private lateinit var transportManager: TransportManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        sut = MapManagerPreparedForTest()
        transportManager = TransportManagerPreparedForTest(sut)
        gameStateManager = GameStateManagerPreparedForTest(transportManager, sut)
        coords = Coordinates(0,0)
    }

    @Test
    fun queryResourcesOffered() {
        gameStateManager.applyStates(listOf(GameState(coords, Operator.Set, Type.Storage, Wood)))
        val result = sut.queryInStorage(coords)

        assertEquals(listOf(Wood), result)
    }

    @Test
    fun getCellsWhichRequireStuff() {
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
            )
        )
        assertEquals(
            mapOf(Pair(coords, Cell(coords, GroundType.Desert, requires = mutableListOf(Wood)))),
            sut.getCellsWhichRequireStuff()
        )
    }

    @Test
    fun matchTransportToStorage() {
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Transport, Wood),
            )
        )
        //The specific coordinates are irrelevant here
        val result = sut.convertTransportToStorage(sut.findSpecificCell(coords)!!)

        assertEquals(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Remove, Type.Transport, Wood)
        ), result)

        gameStateManager.applyStates(result)
        assertEquals(listOf(Wood), sut.queryInStorage(coords))
    }

    @Test
    fun convertStorageToProduction() {
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Storage, Wood),
            )
        )
        //The specific coordinates are irrelevant here
        val result = sut.convertStorageToProduction(sut.findSpecificCell(coords)!!)

        assertEquals(listOf(
            GameState(coords, Operator.Set, Type.Production, Wood),
            GameState(coords, Operator.Remove, Type.Storage, Wood),
            GameState(coords, Operator.Remove, Type.Required, Wood),
        ), result)

        gameStateManager.applyStates(result)

        assertEquals(listOf(Wood), sut.queryInProduction(coords))
    }


}