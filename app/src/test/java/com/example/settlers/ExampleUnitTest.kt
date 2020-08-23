package com.example.settlers

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MapManagerTestData : MapManager(cells = listOf(
    Cell(coordinates = Coordinates(0,0),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(1,0),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(2,0),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(0,1),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(1,1),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(2,1),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(0,2),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(1,2),type = GroundType.Desert, value = 0.0),
    Cell(coordinates = Coordinates(2,2),type = GroundType.Desert, value = 0.0)
    ))

class ExampleUnitTest {
    @Test
    fun request() {
        val start = Coordinates(0,0)
        val stop = Coordinates(7,7)
        val mapManager = MapManagerTestData()
        val transportManager = TransportManager(mapManager)
        transportManager.request(start, stop, Ressource.Wood)
        assertEquals(4, 2 + 2)
    }
}
