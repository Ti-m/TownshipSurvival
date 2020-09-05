package com.example.settlers.unit

import com.example.settlers.Coordinates
import com.example.settlers.util.CoordinateTransformer
import org.junit.Assert.assertEquals
import org.junit.Test

class CoordinateTransformerTest {

    //https://www.redblobgames.com/grids/hexagons/#coordinates-doubled
    //I use x for row, y for col
    @Test
    fun offsetToDoubleCoords_1() {
        val inputOne = Coordinates(0,0)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(0,0)

        assertEquals(expectedOne, resultOne)
    }
    @Test
    fun offsetToDoubleCoords_2() {
        val inputOne = Coordinates(1,0)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(2,0)

        assertEquals(expectedOne, resultOne)
    }

    @Test
    fun offsetToDoubleCoords_3() {
        val inputOne = Coordinates(0,1)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(1,1)

        assertEquals(expectedOne, resultOne)
    }

    @Test
    fun offsetToDoubleCoords_4() {
        val inputOne = Coordinates(1,1)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(3,1)

        assertEquals(expectedOne, resultOne)
    }

    @Test
    fun offsetToDoubleCoords_5() {
        val inputOne = Coordinates(0,2)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(0,2)

        assertEquals(expectedOne, resultOne)
    }

    @Test
    fun offsetToDoubleCoords_6() {
        val inputOne = Coordinates(1,2)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(2,2)

        assertEquals(expectedOne, resultOne)
    }

    @Test
    fun offsetToDoubleCoords_7() {
        val inputOne = Coordinates(0,3)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(1,3)

        assertEquals(expectedOne, resultOne)
    }

    @Test
    fun offsetToDoubleCoords_8() {
        val inputOne = Coordinates(1,3)
        val resultOne = CoordinateTransformer.offsetToDouble(inputOne)
        val expectedOne = Coordinates(3,3)

        assertEquals(expectedOne, resultOne)
    }

}