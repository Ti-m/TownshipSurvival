package com.example.settlers.unit.terrain

import com.example.settlers.Cell
import com.example.settlers.Coordinates
import com.example.settlers.GroundType
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import com.example.settlers.util.TestDoubleRandom
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class MapGeneratorTest {
    lateinit var terrainInterpolator: TerrainInterpolator
    lateinit var randomGenerator: Random

    lateinit var sut: MapGenerator

    class TestInterpolator(randomGenerator: Random) : TerrainInterpolator(randomGenerator) {
        override fun interpolate(
            terrain: Array<Array<Double?>>,
            size: Int,
            randomAmplitude: Double,
            offset: Double
        ) {
            //do nothing
        }
    }

    @Before
    fun prepare() {
        randomGenerator = TestDoubleRandom()
        terrainInterpolator = TestInterpolator(randomGenerator)
        sut = MapGenerator(terrainInterpolator, randomGenerator)
    }

    @Test
    fun createMap() {
        val map = sut.createMap(2)
        assertEquals(mapOf(
            Pair(Coordinates(0,0), Cell(Coordinates(0,0), GroundType.Water)),
            Pair(Coordinates(1,1), Cell(Coordinates(1,1), GroundType.Desert)),//TODO Why is here desert and not water?
            Pair(Coordinates(2,0), Cell(Coordinates(2,0), GroundType.Grass)),//TODO Why is here desert and not water?
            Pair(Coordinates(3,1), Cell(Coordinates(3,1), GroundType.Water))
        ),
            map
        )
    }
}