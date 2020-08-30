package com.example.settlers.terrain

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.settlers.*
import com.example.settlers.ui.FlagTile

class MapGenerator(private val interpolator: TerrainInterpolator) {
    fun createMap(size: Int): List<Cell> {
        val map = Array(size) {
            Array<Double?>(size) {
                null
            }
        }
        map[0][0] = 1.0
        map[0][size-1] = 11.0
        map[size-1][0] = 21.0
        map[size-1][size-1] = 31.0
        interpolator.interpolate(map, size, 0.03, 0.0)
        if (map[(size/2-1)][size/2-1] == null) return listOf()
        var result = mutableListOf<Cell>()
        map.forEachIndexed { indexX, array ->
            array.forEachIndexed { indexY, item ->

                result.add(
                    Cell(
                    coordinates = Coordinates(x= indexX + 1, y = indexY + 1),
                    type = GroundType.Water,
                    value = item!!
                )
                )
            }
        }
        val max = result.maxBy { it.value }
        //val min = result.minBy { it.value }
        result = result.map {
            val tmp = it.value / max!!.value
            val type = when  { //if (item!! < 1.0) GroundType.Grass else GroundType.Desert
                tmp < 0.25 -> GroundType.Water
                tmp < 0.5 -> GroundType.Desert
                tmp < 0.75 -> GroundType.Grass
                tmp < 1.0 -> GroundType.Mountain
                else -> GroundType.Water
            }

            Cell(coordinates = it.coordinates, type = type, value = tmp)
        }.toMutableList()
        return result
    }

    fun createTiles(input: List<Cell>, transportManager: TransportManager, fragmentManager: FragmentManager, context: Context, mapManager: MapManager): List<FlagTile> {
        return input.map { FlagTile(it, input, transportManager, mapManager, fragmentManager, context) }
    }
}