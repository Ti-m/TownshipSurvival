package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.unit.terrain.MapGeneratorTest
import com.example.settlers.util.TestDoubleRandom
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MapSaverTest {
    private lateinit var map: MutableMap<Coordinates, Cell>
    private lateinit var sut: MapSaver

    @Before
    fun setup() {
        map = mutableMapOf( //4x4 grid
            //TODO Building is not comparable yet, so this will not be equal after deserialization:
            //Pair(Coordinates(2,0), Cell(coordinates = Coordinates(2,0), type = GroundType.Desert, building = Townhall())),
            Pair(Coordinates(0,0), Cell(coordinates = Coordinates(0,0), type = GroundType.Desert)),
            Pair(Coordinates(2,0), Cell(coordinates = Coordinates(2,0), type = GroundType.Desert)),
            Pair(Coordinates(4,0), Cell(coordinates = Coordinates(4,0),type = GroundType.Desert)),
            Pair(Coordinates(6,0), Cell(coordinates = Coordinates(6,0),type = GroundType.Desert)),
            Pair(Coordinates(1,1), Cell(coordinates = Coordinates(1,1),type = GroundType.Desert)),
            Pair(Coordinates(3,1), Cell(coordinates = Coordinates(3,1),type = GroundType.Desert)),
            Pair(Coordinates(5,1), Cell(coordinates = Coordinates(5,1),type = GroundType.Desert)),
            Pair(Coordinates(7,1), Cell(coordinates = Coordinates(7,1),type = GroundType.Desert)),
            Pair(Coordinates(0,2), Cell(coordinates = Coordinates(0,2),type = GroundType.Desert)),
            Pair(Coordinates(2,2), Cell(coordinates = Coordinates(2,2),type = GroundType.Desert)),
            Pair(Coordinates(4,2), Cell(coordinates = Coordinates(4,2),type = GroundType.Desert)),
            Pair(Coordinates(6,2), Cell(coordinates = Coordinates(6,2),type = GroundType.Desert)),
            Pair(Coordinates(1,3), Cell(coordinates = Coordinates(1,3),type = GroundType.Desert)),
            Pair(Coordinates(3,3), Cell(coordinates = Coordinates(3,3),type = GroundType.Desert)),
            Pair(Coordinates(5,3), Cell(coordinates = Coordinates(5,3),type = GroundType.Desert)),
            Pair(Coordinates(7,3), Cell(coordinates = Coordinates(7,3),type = GroundType.Desert)),
        )

        val randomGenerator = TestDoubleRandom()
        val terrainInterpolator = MapGeneratorTest.TestInterpolator(randomGenerator)
        val mapGen = MapGenerator(terrainInterpolator, randomGenerator)

        sut = MapSaver(map, mapGen)
    }

    @Test
    fun `check serialize, unserialize`() {
        val tmp = map.toMap()//copy
        sut.save()
        sut.load()

        Assert.assertEquals(
            tmp, map
        )
    }
}