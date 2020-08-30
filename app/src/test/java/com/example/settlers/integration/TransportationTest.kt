package com.example.settlers.integration

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import org.junit.Test

import org.junit.Assert.*

class TransportationTest {
    @Test
    fun request() {
        //TODO this seems to be some higherlvl class, which handles the different managers. Like GameManager??

        //TODO missing: The resource at the provider are not set
        val provider = Coordinates(0,0)
        val destiantion = Coordinates(1,0)//TODO move somewhere else
        val transportRequest = TransportRequestNew(destination = destiantion, what = Resource.Wood)
        val mapManager = MapManagerTestData()

        mapManager.applyStates(listOf(GameState(provider, Command.SetResourceOffered, Resource.Wood)))
        assertEquals(listOf(Resource.Wood), mapManager.queryResourcesOffered(at = provider))

        val transportManager = TransportManagerNew(mapManager, BreadthFirstSearchRouting(mapManager))

        transportManager.request(transportRequest)
        val newStates = transportManager.tick()
        mapManager.applyStates(newStates)

        assertEquals(Resource.Wood, mapManager.queryResource1(at = destiantion))
        assertEquals(emptyList<Resource>(), mapManager.queryResourcesOffered(at = provider))
    }
}
