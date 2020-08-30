package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BreadthFirstSearchRoutingTest {

    lateinit var sut: BreadthFirstSearchRouting

    @Before
    fun setup() {
        sut = BreadthFirstSearchRouting()
    }

    @Test
    fun calcRoute() {
        val from = Coordinates(0,0)
        val to = Coordinates(2,2)

        val route = sut.calcRoute(from, to)

        Assert.assertEquals(
            Route(
                listOf(
                    Coordinates(0,0),
                    Coordinates(0,1),
                    Coordinates(0,2),
                    Coordinates(1,2),
                    Coordinates(2,2)
                )
            ),
            route
        )
    }
}