package com.example.settlers

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

enum class GroundType { Water, Grass, Desert, Mountain }
enum class BuildingType { Townhall, Lumberjack, Road }

class Cell(
    var coordinates: Coordinates,
    var type: GroundType,
    var building: BuildingType? = null,
    var carrier: Boolean = false,
    var worker: Worker? = null,
    var ressource1: Ressource? = null,
    var ressource2: Ressource? = null,
    val value: Double//Used in map generation, remove?
)

class Coordinates(val x: Int, val y: Int)

class Ressource
class Worker

class GameWorld(private val cells: List<Cell>, private val fragmentManager: FragmentManager, context: Context?) : ViewGroup(context) {
    companion object {
        val TAG = "GameWorld"
    }

    val tiles = createTiles(cells)

    init {
        tiles.forEach {
            this.addView(it)
        }
    }

    private fun createTiles(input: List<Cell>): List<FlagTile> {
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
            if (it.cell.coordinates.x.rem(2) == 0) {
                it.layout(
                    (it.cell.coordinates.x * 1.5 * a).toInt(),
                    (it.cell.coordinates.y * 2 * r).toInt(),
                    (it.cell.coordinates.x * 1.5 * a + 2 * a).toInt(),
                    (it.cell.coordinates.y * 2 * r + 2 * r).toInt()
                )
            } else {
                it.layout(
                    (it.cell.coordinates.x * 1.5 * a).toInt(),
                    (it.cell.coordinates.y * 2 * r + r).toInt(),
                    (it.cell.coordinates.x * 1.5 * a + 2 * a).toInt(),
                    (it.cell.coordinates.y * 2 * r + 3 * r).toInt()
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