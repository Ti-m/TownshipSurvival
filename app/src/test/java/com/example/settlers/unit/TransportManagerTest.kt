package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import org.junit.Test

class TransportManagerTest {

    lateinit var sut: TransportManagerNew

    @Test
    fun prepare() {
        val mapManager = MapManagerTestData()
        sut = TransportManagerNew(mapManager, BreadthFirstSearchRouting(mapManager))
    }

    fun request() {
        val coords: Coordinates = Coordinates(0,0)
        val transportRequestNew = TransportRequestNew(coords, Resource.Wood)
        sut.request(transportRequestNew)
    }
}