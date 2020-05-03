package com.example.settlers

interface TerrainInterpolator {
    fun interpolate(terrain: Array<Array<Double>>, int: Int)
    fun isPowerOfTwo(number: Int): Boolean
}

class DefaultTerrainInterpolator : TerrainInterpolator {
    override fun interpolate(terrain: Array<Array<Double>>, int: Int) {

    }

    override fun isPowerOfTwo(number: Int): Boolean {
        return true
    }
}