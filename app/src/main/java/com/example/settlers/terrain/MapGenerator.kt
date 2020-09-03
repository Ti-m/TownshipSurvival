package com.example.settlers.terrain

import android.content.Context
import com.example.settlers.*
import com.example.settlers.ui.FlagTile
import com.example.settlers.util.CoordinateTransformer

data class MapGeneratorCell(
    var coordinates: Coordinates,
    var type: GroundType,
    val value: Double
)

class MapGenerator(private val interpolator: TerrainInterpolator) {
    fun createMap(size: Int): List<Cell> {
        val map = Array(size) {
            Array<Double?>(size) {
                0.0
            }
        }
        map[0][0] = 1.0
        map[0][size-1] = 11.0
        map[size-1][0] = 21.0
        map[size-1][size-1] = 31.0

        interpolator.interpolate(map, size, 0.03, 0.0)

        if (map[(size/2-1)][size/2-1] == null) return listOf()
        var result = mutableListOf<MapGeneratorCell>()
        map.forEachIndexed { indexX, array ->
            array.forEachIndexed { indexY, item ->

                result.add(
                    MapGeneratorCell(
//                    coordinates = Coordinates(x= indexX + 1, y = indexY + 1),
                    coordinates = CoordinateTransformer.offsetToDouble(Coordinates(x= indexX, y = indexY)),
                    type = GroundType.Water,
                    value = item!!
                    )
                )
            }
        }
        val max = result.maxByOrNull { it.value }
        //val min = result.minBy { it.value }
        val cellresult = result.map {
            val tmp = it.value / max!!.value
            val type = when  { //if (item!! < 1.0) GroundType.Grass else GroundType.Desert
                tmp < 0.25 -> GroundType.Water
                tmp < 0.5 -> GroundType.Desert
                tmp < 0.75 -> GroundType.Grass
                tmp < 1.0 -> GroundType.Mountain
                else -> GroundType.Water
            }

            Cell(coordinates = it.coordinates, type = type)
        }
        return cellresult.toMutableList()
    }

    fun createTiles(input: List<Cell>, buildDialogHandler: BuildDialogHandler, context: Context): List<FlagTile> {
        return input.map { FlagTile(it, buildDialogHandler, context) }
    }
}