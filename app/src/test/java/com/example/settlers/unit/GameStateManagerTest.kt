package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.*
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
        assertEquals(listOf(Wood), mapManager.queryInStorage(coords))
        //Remove
        sut.applyStates(listOf(GameState(coords, Operator.Remove, Type.Storage, Wood)))
        assertEquals(listOf<Resource>(), mapManager.queryInStorage(coords))
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
        assertEquals(listOf(Wood, Wood), mapManager.queryInTransport(coords))
        //Remove
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Remove, Type.Transport, Wood),
                GameState(coords, Operator.Remove, Type.Transport, Wood)
            )
        )
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(coords))
    }

    @Test
    fun applyStates_SetTownhall() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Townhall())))
        assertTrue(mapManager.queryBuilding(coords) is Townhall)

        assertEquals(
            listOf(Wood, Wood, Wood, Stone, Stone, Stone),
            mapManager.queryInStorage(coords)
        )
    }

    @Test
    fun applyStates_SetLumberjack() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Lumberjack())))
        assertTrue(mapManager.queryBuilding(coords) is Lumberjack)
    }

    @Test
    fun applyStates_SetRoad() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Road())))
        assertTrue(mapManager.queryBuilding(coords) is Road)
    }

    @Test
    fun `Test Zombie movement - Find targets and move there`() {
        val destination = Coordinates(0,0)
        val wrongTarget = Coordinates(7,3)
        val spawner = Coordinates(2,2)
        sut.applyStates(listOf(
            GameState(wrongTarget, Operator.Set, Type.Building, Tower()),
            GameState(destination, Operator.Set, Type.Building, Townhall()),
            GameState(spawner, Operator.Set, Type.MovingObject, Zombie)
        ))
        mapManager.resetTouched()
        sut.tick()

        assertEquals(Zombie, mapManager.findSpecificCell(Coordinates(1, 1))!!.movingObject)
        assertEquals(null, mapManager.findSpecificCell(Coordinates(2, 2))!!.movingObject)

        //another step
        mapManager.resetTouched()
        sut.tick()

        assertEquals(Zombie, mapManager.findSpecificCell(Coordinates(0, 0))!!.movingObject)
        assertEquals(null, mapManager.findSpecificCell(Coordinates(1, 1))!!.movingObject)
        assertEquals(null, mapManager.findSpecificCell(Coordinates(2, 2))!!.movingObject)
    }

    @Test
    fun `Removing a building removes requested items`() {
        val cell = mapManager.findSpecificCell(Coordinates(0, 0))!!
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Transport, Wood))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Production, Wood))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Storage, Wood))
        assertEquals(2, cell.requires.count())
        sut.applyState(GameState(Coordinates(0,0), Operator.Remove, Type.Building, null))
        assertEquals(0, cell.production.count())
        assertEquals(0, cell.storage.count())
        assertEquals(0, cell.transport.count())
        assertNull(cell.building)
    }

    @Test
    fun `Replacing a building updates requested items`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        assertEquals(2, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(0, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        assertEquals(0, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(6, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
    }

    @Test
    fun `Replacing a building does not delete the storage`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        assertEquals(0, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(6, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        assertEquals(2, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(6, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
    }

    @Test
    fun `A Building gets destroyed, if a zombie steps into its cell`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.MovingObject, Zombie))
        sut.tick()
        assertNull(mapManager.findSpecificCell(Coordinates(0, 0))!!.building)
    }
}