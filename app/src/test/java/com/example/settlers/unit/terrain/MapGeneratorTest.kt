package com.example.settlers.unit.terrain

import com.example.settlers.Cell
import com.example.settlers.Coordinates
import com.example.settlers.GroundType
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MapGeneratorTest {
    lateinit var terrainInterpolator: TerrainInterpolator

    lateinit var sut: MapGenerator

    class TestInterpolator : TerrainInterpolator() {
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
        terrainInterpolator = TestInterpolator()
        sut = MapGenerator(terrainInterpolator)
    }

    @Test
    fun createMap() {
        val map = sut.createMap(2)
        assertEquals(listOf(
            Cell(Coordinates(0,0), GroundType.Water),
            Cell(Coordinates(1,1), GroundType.Desert),//TODO Why is here desert and not water?
            Cell(Coordinates(2,0), GroundType.Grass),//TODO Why is here desert and not water?
            Cell(Coordinates(3,1), GroundType.Water)
        ),
            map
        )
    }
}