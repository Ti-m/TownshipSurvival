package com.example.settlers

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager

open class MapManager (private val cells: List<Cell>) {
    fun getCellBy(coordinates: Coordinates): Cell {
        return cells[0]
    }
}

class GameWorld(private val tiles: List<FlagTile>, private val transport: MutableList<Transport>, private val fragmentManager: FragmentManager, context: Context?) : ViewGroup(context) {
    companion object {
        val TAG = "GameWorld"
    }


    init {
        tiles.forEach {
            this.addView(it)
        }
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