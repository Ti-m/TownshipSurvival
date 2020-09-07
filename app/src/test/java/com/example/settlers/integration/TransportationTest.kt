package com.example.settlers.integration

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
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
        val mapManager = MapManagerTestData()
        val gameStateManager = GameStateManager(mapManager, DisabledLogger())

        gameStateManager.applyStates(listOf(
            GameState(provider, Operator.Set, Type.Offered, Wood),
            GameState(destiantion, Operator.Set, Type.Requires, Wood),
        ))
        assertEquals(listOf(Wood), mapManager.queryResourcesOffered(at = provider))

        val transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)

        //transportManager.request(transportRequest)
        val newStates = transportManager.tick()//It has to tick more then once, to do a transport more far
        gameStateManager.applyStates(newStates)

        assertEquals(listOf(Wood), mapManager.queryResources(at = destiantion))
        assertEquals(emptyList<Resource>(), mapManager.queryResourcesOffered(at = provider))
    }

    @Test
    fun completeTransport() {

        //Convert an item from the resource list to the storage list
        //This makes the item unavailable for further transports
        //Special case for warehouse. This building makes storage items available for transport
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
