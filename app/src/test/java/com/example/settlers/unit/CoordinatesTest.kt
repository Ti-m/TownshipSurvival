package com.example.settlers.unit

import com.example.settlers.Coordinates
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