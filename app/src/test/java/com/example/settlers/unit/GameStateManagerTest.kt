package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GameStateManagerTest {

    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var transportManager: TransportManager
    private lateinit var sut: GameStateManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        transportManager = TransportManagerPreparedForTest(mapManager)
        sut = GameStateManagerPreparedForTest(transportManager, mapManager)
        coords = Coordinates(0,0)
    }

    @Test
    fun applyStates_SetRemoveResourceOffered() {
        //Set
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Storage, Wood)))
        Assert.assertEquals(listOf(Wood), mapManager.queryInStorage(coords))
        //Remove
        sut.applyStates(listOf(GameState(coords, Operator.Remove, Type.Storage, Wood)))
        Assert.assertEquals(listOf<Resource>(), mapManager.queryInStorage(coords))
    }

    @Test
    fun applyStates_SetRemoveResource() {
        //Set
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Transport, Wood),
                GameState(coords, Operator.Set, Type.Transport, Wood)
            )
        )
        Assert.assertEquals(listOf(Wood, Wood), mapManager.queryInTransport(coords))
        //Remove
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Remove, Type.Transport, Wood),
                GameState(coords, Operator.Remove, Type.Transport, Wood)
            )
        )
        Assert.assertEquals(emptyList<Resource>(), mapManager.queryInTransport(coords))
    }

    @Test
    fun applyStates_SetTownhall() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Townhall())))
        Assert.assertTrue(mapManager.queryBuilding(coords) is Townhall)

        Assert.assertEquals(
            listOf(Wood, Wood, Wood, Stone, Stone, Stone),
            mapManager.queryInStorage(coords)
        )
    }

    @Test
    fun applyStates_SetLumberjack() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Lumberjack())))
        Assert.assertTrue(mapManager.queryBuilding(coords) is Lumberjack)
    }

    @Test
    fun applyStates_SetRoad() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Road())))
        Assert.assertTrue(mapManager.queryBuilding(coords) is Road)
    }

    @Test
    fun `create spawner when first townhall is created`() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Townhall())))
        Assert.assertTrue(mapManager.queryBuilding(coords) is Townhall)
        val southEastEdge = mapManager.getSouthEastEdge()
        Assert.assertTrue(mapManager.queryBuilding(southEastEdge) is Spawner)

    }
}