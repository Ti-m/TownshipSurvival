package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BuildingTest {

    private lateinit var dummyCoordinates: Coordinates

    @Before
    fun prepare() {
        dummyCoordinates = Coordinates(0,0)
    }

    @Test
    fun produce() {
        val sut = Lumberjack()
        sut.produce(dummyCoordinates)
        assertEquals(0, sut.productionCount)
        //First needs to finish construction
        sut.setConstructionFinished()

        sut.produce(dummyCoordinates)
        assertEquals(10, sut.productionCount)
        sut.produce(dummyCoordinates)
        assertEquals(20, sut.productionCount)
        for (x in 0 .. 6) {
            sut.produce(dummyCoordinates)
        }
        assertEquals(90, sut.productionCount)
        val states = sut.produce(dummyCoordinates)
        assertEquals(0, sut.productionCount)
        assertEquals(listOf(GameState(dummyCoordinates, Operator.Set, Type.Storage, Wood)), states)
    }

    @Test
    fun `construct - track progress`() {
        val sut = Lumberjack()
        sut.construct()
        assertEquals(10, sut.constructionCount)
        sut.construct()
        assertEquals(20, sut.constructionCount)
        for (x in 0 .. 6) {
            sut.construct()
        }
        assertEquals(90, sut.constructionCount)
        assertEquals(false, sut.isConstructed())
        sut.construct()
        assertEquals(100, sut.constructionCount)
        assertEquals(true, sut.isConstructed())
        sut.construct()
        assertEquals(100, sut.constructionCount) //Still 100
    }

    @Test
    fun `Spawner produce Zombie`() {
        val sut = Spawner()
        sut.produce(dummyCoordinates)
        assertEquals(1, sut.productionCount)
        sut.produce(dummyCoordinates)
        assertEquals(2, sut.productionCount)
        for (x in 0 .. 96)  sut.produce(dummyCoordinates)
        assertEquals(99, sut.productionCount)
        val states = sut.produce(dummyCoordinates)
        assertEquals(0, sut.productionCount)
        assertEquals(listOf(GameState(dummyCoordinates, Operator.Set, Type.MovingObject, Zombie)), states)
    }
}