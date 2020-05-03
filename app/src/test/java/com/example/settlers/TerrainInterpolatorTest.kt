package com.example.settlers

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TerrainInterpolatorTest {

    private lateinit var actions: String
    private lateinit var dummy: Array<Array<Double>>
    private lateinit var interpolator: TerrainInterpolator

    @Before
    fun setUp() {
        actions = ""
        dummy = arrayOf(arrayOf(1.0))
        interpolator = TerrainInterpolatorSpy()
    }

    @Test
    fun terminalCondition_sizeOne() {
        interpolator.interpolate(dummy, 1)
        assertEquals(actions, "")
    }

    @Test
    fun sizeMustBePowerOfTwoPlus1() {
        interpolator.interpolate(dummy, 2)
    }

    @Test
    fun Check_isPowerOfTwo() {
        interpolator.interpolate(dummy, 2)
        assertEquals(interpolator.isPowerOfTwo(2), true)
    }
}

class TerrainInterpolatorSpy : TerrainInterpolator  {
    override fun interpolate(terrain: Array<Array<Double>>, int: Int) {

    }

    override fun isPowerOfTwo(number: Int): Boolean {
       return true
    }

}