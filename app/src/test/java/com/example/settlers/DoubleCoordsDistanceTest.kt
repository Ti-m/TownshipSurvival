package com.example.settlers

import org.junit.Test

import org.junit.Assert.*

class DoubleCoordsDistanceTest {

    @Test
    fun distance() {
        assertEquals(0, DoubleCoordsDistance.distance(Coordinates(0,0), Coordinates(0,0)))
        assertEquals(1, DoubleCoordsDistance.distance(Coordinates(0,0), Coordinates(2,0)))
        assertEquals(2, DoubleCoordsDistance.distance(Coordinates(0,0), Coordinates(4,0)))
        assertEquals(3, DoubleCoordsDistance.distance(Coordinates(0,0), Coordinates(6,0)))

        assertEquals(1, DoubleCoordsDistance.distance(Coordinates(0,0), Coordinates(1,1)))
        assertEquals(2, DoubleCoordsDistance.distance(Coordinates(0,0), Coordinates(2,2)))
        assertEquals(3, DoubleCoordsDistance.distance(Coordinates(0,0), Coordinates(3,3)))

        assertEquals(0, DoubleCoordsDistance.distance(Coordinates(6,2), Coordinates(6,2)))
        assertEquals(1, DoubleCoordsDistance.distance(Coordinates(6,2), Coordinates(5,1)))
        assertEquals(2, DoubleCoordsDistance.distance(Coordinates(6,2), Coordinates(3,1)))
        assertEquals(3, DoubleCoordsDistance.distance(Coordinates(6,2), Coordinates(2,0)))
    }
}