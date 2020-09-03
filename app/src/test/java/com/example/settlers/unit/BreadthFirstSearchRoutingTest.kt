package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.Route
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BreadthFirstSearchRoutingTest {

    lateinit var sut: BreadthFirstSearchRouting

    @Before
    fun setup() {
        sut = BreadthFirstSearchRouting(MapManagerTestData())
    }

    @Test
    fun calcRoute() {
        val from = Coordinates(1,1)
        val to = Coordinates(1,2)

        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                current = Coordinates(1,1),
                steps = mutableListOf(
                    Coordinates(1,2)
                )
            ),
            route
        )
    }

    @Test
    fun calcRoute2() {
        val from = Coordinates(1,1)
        val to = Coordinates(2,2)

        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                current = Coordinates(1,1),
                steps = mutableListOf(
                    Coordinates(2,1),
                    Coordinates(2,2)
                )
            ),
            route
        )
    }

    @Test
    fun calcRoute3() {
        val from = Coordinates(0,0)
        val to = Coordinates(2,2)

        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                current = Coordinates(0,0),
                steps = mutableListOf(
                    Coordinates(1,1),
                    Coordinates(2,1),
                    Coordinates(2,2)
                )
            ),
            route
        )
    }
}