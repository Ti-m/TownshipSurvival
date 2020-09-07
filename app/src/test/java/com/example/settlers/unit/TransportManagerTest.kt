package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Before
import org.junit.Test

class TransportManagerTest {

    lateinit var sut: TransportManager
    lateinit var logger: Logger

    @Before
    fun prepare() {
        val mapManager = MapManagerTestData()
        logger = DisabledLogger()
        sut = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)
    }

//    @Test
//    fun request() {
//        val coords: Coordinates = Coordinates(0,0)
//        val transportRequestNew = TransportRequestNew(coords, Wood)
//        sut.request(transportRequestNew)
//    }

    @Test
    fun tick() {
    }
}