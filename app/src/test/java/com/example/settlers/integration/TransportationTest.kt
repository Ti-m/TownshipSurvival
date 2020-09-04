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
        val transportRequest = TransportRequestNew(destination = destiantion, what = Resource.Wood)
        val mapManager = MapManagerTestData()

        mapManager.applyStates(listOf(GameState(provider, Command.SetResourceOffered, Resource.Wood)))
        assertEquals(listOf(Resource.Wood), mapManager.queryResourcesOffered(at = provider))

        val transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)

        transportManager.request(transportRequest)
        val newStates = transportManager.tick()//It has to tick more then once, to do a transport more far
        mapManager.applyStates(newStates)

        assertEquals(Resource.Wood, mapManager.queryResource1(at = destiantion))
        assertEquals(emptyList<Resource>(), mapManager.queryResourcesOffered(at = provider))
    }
}
