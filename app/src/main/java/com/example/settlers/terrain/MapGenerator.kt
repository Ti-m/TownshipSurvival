package com.example.settlers.terrain

import android.content.Context
import com.example.settlers.*
import com.example.settlers.ui.FlagTile
import com.example.settlers.ui.GraphicalFlagTile
import com.example.settlers.util.CoordinateTransformer
import kotlin.random.Random

data class MapGeneratorCell(
    var coordinates: Coordinates,
    var type: GroundType,
    val value: Double
)

class MapGenerator(private val interpolator: TerrainInterpolator, private val randomGenerator: Random) {
    fun createMap(size: Int): MutableMap<Coordinates, Cell> {
        val mapArray = Array(size) {
            Array<Double?>(size) {
                0.0
            }
        }
        mapArray[0][0] = 1.0
        mapArray[0][size-1] = 11.0
        mapArray[size-1][0] = 21.0
        mapArray[size-1][size-1] = 31.0

        interpolator.interpolate(mapArray, size, 0.03, 0.0)

        if (mapArray[(size/2-1)][size/2-1] == null) return mutableMapOf()
        val result = mutableMapOf<Coordinates, MapGeneratorCell>()
        mapArray.forEachIndexed { indexX, array ->
            array.forEachIndexed { indexY, item ->
                val coords = CoordinateTransformer.offsetToDouble(Coordinates(x= indexX, y = indexY))
                result[coords] = MapGeneratorCell(
//                    coordinates = Coordinates(x= indexX + 1, y = indexY + 1),
                    coordinates = coords,
                    type = GroundType.Water,
                    value = item!!
                )
            }
        }
        val max = result.maxByOrNull { it.value.value }
        //val min = result.minBy { it.value }
        val cellresult = result.mapValues {
            val tmp = it.value.value / max!!.value.value
            val type = when  { //if (item!! < 1.0) GroundType.Grass else GroundType.Desert
                tmp < 0.25 -> GroundType.Water
                tmp < 0.5 -> GroundType.Desert
                tmp < 0.75 -> GroundType.Grass
                tmp < 1.0 -> GroundType.Mountain
                else -> GroundType.Water
            }

            val hasResource = randomGenerator.nextInt(0, 100)
            val supportsTrees = type == GroundType.Grass || type == GroundType.Desert
            val supportsRocks = type == GroundType.Grass || type == GroundType.Desert || type == GroundType.Mountain
            val worldResource = if (type == GroundType.Water) {
                FishShoal //TODO atm every piece of water is a FishShoal. Reduce to something resonable
            } else if (hasResource < 25 && supportsTrees) {
                Tree
            } else if (hasResource > 85 && supportsRocks) {
                Rock
            } else {
                null
            }

            Cell(coordinates = it.value.coordinates, type = type, worldResource = worldResource).apply {
                this.textureVariant = randomGenerator.nextInt(0,2)
            }
        }
        return cellresult.toMutableMap()
    }

    fun createTiles(
        context: Context,
        input: Map<Coordinates, Cell>,
        modeController: ModeController,
        neighbourCalculator: HexagonNeighbourCalculator,
        isLowDpi: Boolean,
        assignedOverlayController: OverlayController,
        clickedOverlayController: OverlayController,
    ): Map<Coordinates, FlagTile> {
        return input.mapValues { GraphicalFlagTile(
            context,
            it.value,
            modeController,
            neighbourCalculator,
            isLowDpi,
            assignedOverlayController,
            clickedOverlayController,
        )}
    }
}