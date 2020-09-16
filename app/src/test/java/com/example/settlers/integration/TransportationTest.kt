package com.example.settlers.integration

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerPreparedForTest
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class TransportationTest {

    lateinit var logger: Logger

    @Before
    fun setup() {
        logger = DisabledLogger()
    }

    @Test
    fun request() {
        //TODO this seems to be some higherlvl class, which handles the different managers. Like GameManager??

        val provider = Coordinates(0,0)
        val destiantion = Coordinates(1,1)//This test only works for a single tile, because the it ticks only once
        //val transportRequest = TransportRequestNew(destination = destiantion, what = Wood)
        val mapManager = MapManagerPreparedForTest()

        val transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)

        val gameStateManager = GameStateManager(transportManager, mapManager, DisabledLogger())

        gameStateManager.applyStates(listOf(
            GameState(provider, Operator.Set, Type.Building, Townhall()),
            GameState(destiantion, Operator.Set, Type.Building, Lumberjack()),
//            GameState(provider, Operator.Set, Type.Storage, Wood),
//            GameState(destiantion, Operator.Set, Type.Required, Wood),
        ))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        //transportManager.request(transportRequest)
        //val newStates = transportManager.tick()//It has to tick more then once, to do a transport more far
        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = destiantion))
        assertEquals(listOf(Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        //TODO Another tick to convert. Do I really want to do this here? This test gets really messy. Block Cells?
        //val newStates2 = transportManager.tick()
        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood), mapManager.queryInStorage(at = destiantion))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = destiantion))
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
