package com.example.settlers.unit

import com.example.settlers.Coordinates
import com.example.settlers.Resource
import com.example.settlers.TransportManagerNew
import com.example.settlers.TransportRequestNew
import com.example.settlers.testdoubles.MapManagerTestData
import org.junit.Test

class TransportManagerTest {

    lateinit var sut: TransportManagerNew

    @Test
    fun prepare() {
        sut = TransportManagerNew(MapManagerTestData())
    }

    fun request() {
        val coords: Coordinates = Coordinates(0,0)
        val transportRequestNew = TransportRequestNew(coords, Resource.Wood)
        sut.request(transportRequestNew)
    }
}