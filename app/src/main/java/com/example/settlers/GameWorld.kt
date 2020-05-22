package com.example.settlers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.settlers.MainActivity.Companion.flagDistance
import com.otaliastudios.zoom.ZoomLayout
import kotlin.math.round
import kotlin.math.sqrt

enum class GroundType { Water, Grass, Desert, Mountain }
enum class BuildingType { Townhall, Lumberjack, Road }

class Element(
    val x: Int,
    val y: Int,
    var type: GroundType,
    var building: BuildingType? = null,
    var carrier: Boolean = false,
    var worker: Worker? = null,
    var ressource1: Ressource? = null,
    var ressource2: Ressource? = null,
    val value: Double//Used in map generation, remove?
)

class Ressource
class Worker



class GameWorld(private val tileGridSize: Int, private val fragmentManager: FragmentManager, context: Context?) : ViewGroup(context) {
    companion object {
        val TAG = "GameWorld"
    }

    val map = createMap(tileGridSize)
    val tiles = createTiles(map)

    init {
        tiles.forEach {
            this.addView(it)
        }
    }

    private fun createMap(size: Int): List<Element> {
        val map = Array(size) {
            Array<Double?>(size) {
                null
            }
        }
        map[0][0] = 1.0
        map[0][size-1] = 11.0
        map[size-1][0] = 21.0
        map[size-1][size-1] = 31.0
        val interpolator = TerrainInterpolator()
        interpolator.interpolate(map, size, 0.03, 0.0)
        if (map[(size/2-1)][size/2-1] == null) return listOf()
        var result = mutableListOf<Element>()
        map.forEachIndexed { indexX, array ->
            array.forEachIndexed { indexY, item ->

                result.add(Element(x= indexX + 1, y = indexY + 1, type = GroundType.Water, value = item!!))
            }
        }
        val max = result.maxBy { it.value }
        val min = result.minBy { it.value }
        result = result.map {
            val tmp = it.value / max!!.value
            val type = when  { //if (item!! < 1.0) GroundType.Grass else GroundType.Desert
                tmp < 0.25 -> GroundType.Water
                tmp < 0.5 -> GroundType.Desert
                tmp < 0.75 -> GroundType.Grass
                tmp < 1.0 -> GroundType.Mountain
                else -> GroundType.Water
            }

            Element(x = it.x, y = it.y, type = type, value = tmp)
        }.toMutableList()
        return result
    }

    private fun createTiles(input: List<Element>): List<FlagTile> {
        return input.map { FlagTile(it, fragmentManager, context) }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.i(TAG,"onInterceptTouchEvent")
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG,"onTouchEvent")
        return false
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        tiles.forEach {
            val a = it.coords.a
            val r = it.coords.r
            if (it.element.x.rem(2) == 0) {
                it.layout(
                    (it.element.x * 1.5 * a).toInt(),
                    (it.element.y * 2 * r).toInt(),
                    (it.element.x * 1.5 * a + 2 * a).toInt(),
                    (it.element.y * 2 * r + 2 * r).toInt()
                )
            } else {
                it.layout(
                    (it.element.x * 1.5 * a).toInt(),
                    (it.element.y * 2 * r + r).toInt(),
                    (it.element.x * 1.5 * a + 2 * a).toInt(),
                    (it.element.y * 2 * r + 3 * r).toInt()
                )
            }
        }
    }

    private val overlayBackgroundPaint = Paint().apply {
        this.color = Color.WHITE
        this.style = Paint.Style.FILL_AND_STROKE
    }

//    private fun getSelectedElement(x: Float, y: Float) : Element?  {
//        //val row = (y - 0.5 * flagDistance) / flagDistance
//        val row = round(y / flagDistance).toInt()
//        //var column = round(x / flagDistance).toInt()
//        val column = if (row.rem(2).toInt() == 0) {
//            round((x  - flagDistance / 2) / flagDistance).toInt()
//        } else {
//            round(x / flagDistance).toInt()
//        }
//        //val column = (x / textSize) - 1
//       // var column = round(x / flagDistance).toInt()
////        if (column.rem(2).toInt() == 0) {
////            column = round((x  - flagDistance / 2) / flagDistance).toInt()
////        }
//
//        //val column = (x - 0.5 * flagDistance) / flagDistance
//        Log.e("getSelected", "$row $column")
//        return try {
//            map.first {
//                it.y == column && it.x == row
//            }
//        } catch (e: NoSuchElementException) {
//            null
//        }
//    }

//    private fun drawSelectedBox(canvas: Canvas?) {
//        var add = 0.0f
//        if (selectedElement!!.x.rem(2).toInt() == 0) {
//            add = flagDistance/2
//        }
//        canvas!!.drawRect(
//            selectedElement!!.y * flagDistance + add,
//            selectedElement!!.x * flagDistance,
//            selectedElement!!.y * flagDistance + add + 100.0f,
//            selectedElement!!.x * flagDistance + 100.0f,
//            framePaint
//        )
////        canvas.drawRect(
////            selectedCoordinate!!.y * textSize + 2 * textSize + 10,
////            selectedCoordinate!!.x * textSize + textSize + 10,
////            selectedCoordinate!!.y * textSize  + 2 * textSize + 2 * textSize - 10,
////            selectedCoordinate!!.x * textSize  + textSize + 2 * textSize - 10,
////            overlayBackgroundPaint
////        )
//        ////canvas!!.drawText(it.text.toString(),left + textSize * it.coords.y,top + textSize * it.coords.x, localPaint)
//    }
}

class ColorTools {
    companion object {
        fun getIntFromColor(red: Int, green: Int, blue: Int): Int {
            var red = red
            var green = green
            var blue = blue
            red = red shl 16 and 0x00FF0000 //Shift red 16-bits and mask out other stuff
            green = green shl 8 and 0x0000FF00 //Shift Green 8-bits and mask out other stuff
            blue = blue and 0x000000FF //Mask out anything not blue.
            return -0x1000000 or red or green or blue //0xFF000000 for 100% Alpha. Bitwise OR everything together.
        }
    }
}