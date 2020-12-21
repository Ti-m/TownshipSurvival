package com.example.settlers

import kotlin.math.abs
import kotlin.math.max

object DoubleCoordsDistance {
    //Calculate coordinate distance for double coords, "double heigth": https://www.redblobgames.com/grids/hexagons/#distances-doubled
    fun distance(a: Coordinates, b: Coordinates): Int {
        val dx = abs(a.y - b.y)
        val dy = abs(a.x - b.x)
        return dx + max(0, (dy - dx) / 2)
    }
}