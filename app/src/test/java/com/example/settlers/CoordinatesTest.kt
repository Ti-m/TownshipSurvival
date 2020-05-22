package com.example.settlers

import org.junit.Assert
import org.junit.Test

class CoordinatesTest {
    @Test
    fun layoutMap() {
        val a = Coordinates(1,1)
        val b = Coordinates(1,1)
        Assert.assertEquals(a, b)
    }
}