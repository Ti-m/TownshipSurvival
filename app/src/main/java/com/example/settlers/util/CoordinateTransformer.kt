package com.example.settlers.util

import com.example.settlers.Coordinates
import com.example.settlers.CubeCoordinates
import com.example.settlers.terrain.MapGeneratorCell

object CoordinateTransformer {

    //https://www.redblobgames.com/grids/hexagons/#coordinates-doubled
    //I use x for row, y for col
    fun offsetToDouble(input: Coordinates): Coordinates {
        if (input.y % 2 == 0) {
            return Coordinates(input.x * 2, input.y)
        }
        else {
            return Coordinates((input.x * 2) + 1, input.y)
        }
    }

}