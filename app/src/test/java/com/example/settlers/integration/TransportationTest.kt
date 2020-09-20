package com.example.settlers.integration

import com.example.settlers.*
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.util.DisabledLogger
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class TransportationTest {

    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var transportManager: TransportManager
    private lateinit var gameStateManager: GameStateManager

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        transportManager = TransportManagerPreparedForTest(mapManager)
        gameStateManager = GameStateManagerPreparedForTest(transportManager, mapManager)
    }

    @Test
    fun `tansport of a single item`() {
        //TODO this seems to be some higherlvl class, which handles the different managers. Like GameManager??

        val provider = Coordinates(0,0)
        val destiantion = Coordinates(1,1)//This test only works for a single tile, because the it ticks only once

        gameStateManager.applyStates(listOf(
            GameState(provider, Operator.Set, Type.Building, Townhall()),
            GameState(destiantion, Operator.Set, Type.Building, Lumberjack()),
        ))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = destiantion))
        assertEquals(listOf(Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        //TODO Another tick to convert. Do I really want to do this here? This test gets really messy. Block Cells?
        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood), mapManager.queryInStorage(at = destiantion))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = destiantion))
    }

    @Test
    fun `transport of 3 items - full disclosure`() {
        gameStateManager.applyStates(GameStateCreator().L3_T3_unfinishedRoad())
        gameStateManager.applyState(GameState(Coordinates(3,1), Operator.Set, Type.Building, Road()))//Finish road


        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = Coordinates(2,0)))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = Coordinates(1,1)))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = Coordinates(2,2)))

        gameStateManager.tick()
        assertEquals(listOf(Wood, Wood), mapManager.queryInTransport(at = Coordinates(3,1)))

        //Don't show these each round, until actually something goes to production
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryInTransport(at = Coordinates(5,1)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(listOf(Wood, Wood), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood, Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood, Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryRequires(at = Coordinates(6,2)))

    }

    @Test
    fun completeTransport() {

        //Convert an item from the resource list to the storage list
        //TODO convertResourceToStorage()

        //Convert an item from the storage list to the production list
        //This makes the item unavailable for further transports
        //This removes the item from the request list
        //TODO convertStorageToProduction()

        //An item from the production list gets converted to a new item (Production in a building)
        //The new item goes into storage list
        // Later: This can take some steps. This Ongoing production needs to be stored
        //TODO runProduction()

    }
}
