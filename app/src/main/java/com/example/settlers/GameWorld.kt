package com.example.settlers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.settlers.MainActivity.Companion.flagDistance
import kotlin.math.round

enum class GroundType { Water, Grass, Desert, Mountain }
class Polygon(val a: Pair<Float, Float>, val b: Pair<Float, Float>, val c: Pair<Float, Float>)
class Element(val x: Int, val y: Int, var typeTop: GroundType, var typeBottom: GroundType, val value: Double )

class GameWorld(context: Context, private val parent: ZoomingLayout, private val tileGridSize: Int) : View(context) {
    companion object {
        val TAG = "GameWorld"
    }

    init {
        parent.addTouchListener(TAG, ::onTouchEvent)
    }

    val map = createMap(tileGridSize)

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

                result.add(Element(x= indexX + 1, y = indexY + 1, typeBottom =  GroundType.Water, typeTop =  GroundType.Water, value = item!!))
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

            Element(x = it.x, y = it.y, typeTop = type, typeBottom = type, value = tmp)
        }.toMutableList()
        return result
    }

    private val flagPaint = Paint().apply {
        this.color = Color.LTGRAY
        this.style = Paint.Style.FILL
        this.textSize = 100.0f
    }
    private val waterPaint = Paint().apply {
        this.color = Color.BLUE
        this.style = Paint.Style.FILL
    }
    private val grassPaint = Paint().apply {
        this.color = Color.GREEN
        this.style = Paint.Style.FILL
    }
    private val desertPaint = Paint().apply {
        this.color = Color.YELLOW
        this.style = Paint.Style.FILL
    }
    private val mountainPaint = Paint().apply {
        this.color = Color.GRAY
        this.style = Paint.Style.FILL
    }

    private fun drawGround(item: Element, canvas: Canvas, path: Path) {
        //even rows need distance/" offset

        val top = calcTop(item)
        val bottom = calcBottom(item)

        val colorTop = when(item.typeTop) {
            GroundType.Grass -> grassPaint
            GroundType.Desert -> desertPaint
            GroundType.Water -> waterPaint
            GroundType.Mountain -> mountainPaint
        }

//            val paint = Paint().apply {
//                this.color = ColorTools.getIntFromColor((item.value * 255).toInt(),(item.value * 255).toInt(),0)
//                this.style = Paint.Style.FILL
//            }
//            drawPolygon(p = top, canvas = canvas, path = path, paint = paint)
        drawPolygon(p = top, canvas = canvas, path = path, paint = colorTop)
        val colorBottom = when(item.typeBottom) {
            GroundType.Grass -> grassPaint
            GroundType.Desert -> desertPaint
            GroundType.Water -> waterPaint
            GroundType.Mountain -> mountainPaint
        }
//            drawPolygon(p = bottom, canvas = canvas, path = path, paint = paint)
        drawPolygon(p = bottom, canvas = canvas, path = path, paint = colorBottom)
    }

    private fun drawFlag(item: Element, canvas: Canvas) {
        val top = calcTop(item)
        drawFlag(top, canvas = canvas, paint = flagPaint)
    }

    private fun calcTop(item: Element): Polygon {
        if (item.y.rem(2) == 0) {
            return Polygon(
                Pair(item.x * MainActivity.flagDistance + 0.5f * MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + 1.5f * MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + 1.0f * MainActivity.flagDistance, item.y * MainActivity.flagDistance - MainActivity.flagDistance)
            )
        } else {
            return Polygon(
                Pair(item.x * MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + MainActivity.flagDistance * 0.5f, item.y * MainActivity.flagDistance - MainActivity.flagDistance)
            )
        }
    }

    private fun calcBottom(item: Element): Polygon {
        if (item.y.rem(2) == 0) {
            return Polygon(
                Pair(item.x * MainActivity.flagDistance + 0.5f * MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + 1.5f * MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + 1.0f * MainActivity.flagDistance, item.y * MainActivity.flagDistance + MainActivity.flagDistance)
            )
        } else {
            return Polygon(
                Pair(item.x * MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + MainActivity.flagDistance, item.y * MainActivity.flagDistance),
                Pair(item.x * MainActivity.flagDistance + MainActivity.flagDistance * 0.5f, item.y * MainActivity.flagDistance + MainActivity.flagDistance)
            )
        }
    }

    private fun drawPolygon(p: Polygon, canvas: Canvas, path: Path, paint: Paint) {
        path.reset()
        path.moveTo(p.a.first, p.a.second)
        path.lineTo(p.b.first, p.b.second)
        path.lineTo(p.c.first, p.c.second)
        canvas.drawPath(path, paint)
    }

    private fun drawFlag(p: Polygon, canvas: Canvas, paint: Paint) {
        canvas.drawCircle(p.a.first, p.b.second, MainActivity.flagDiameter, paint)
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        Log.i(TAG, "onDraw")
        super.onDraw(canvas)

        map.forEach {
            drawGround(it, canvas!!, path)
        }
        map.forEach {
            drawFlag(it, canvas!!)
        }

        if (selectedElement != null) {
            drawSelectedBox(canvas)
        }
    }

    private var selectedElement: Element? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedElement = null
            }
            MotionEvent.ACTION_UP -> {
                Log.d("foo-gameworld", "x is $x")
                Log.d("foo-gameworld", "translationX is $translationX")
                Log.d("foo-gameworld", "left is $left")
                Log.d("foo-gameworld", "event.x is ${event.x}")
                Log.d("foo-gameworld", "scaleX is $scaleX")
                selectedElement = getSelectedElement( event.x / scaleX - translationX / scaleX, event.y / scaleY - translationY / scaleY) //scalex benutzen um beim zoom die richtig position zu ermitteln?
                //selectedElement = getSelectedElement( event.x - translationX, event.y - translationY)
                performClick()

            }
        }
        //Log.e("onTouchEvent", "fired ${event.action} ${selectedElement?.x} ${selectedElement?.y}")
        //return super.onTouchEvent(event)
        return false // return true in parentview
    }

    override fun performClick(): Boolean {
        this.invalidate()
        return super.performClick()
    }

    private val framePaint = Paint().apply {
        this.color = Color.RED
        this.style = Paint.Style.STROKE
        this.strokeWidth = 3.0f
        this.textAlign = Paint.Align.CENTER
    }
    private val overlayBackgroundPaint = Paint().apply {
        this.color = Color.WHITE
        this.style = Paint.Style.FILL_AND_STROKE
    }

    private fun getSelectedElement(x: Float, y: Float) : Element?  {
        //val row = (y - 0.5 * flagDistance) / flagDistance
        val row = round(y / flagDistance).toInt()
        //var column = round(x / flagDistance).toInt()
        val column = if (row.rem(2).toInt() == 0) {
            round((x  - flagDistance / 2) / flagDistance).toInt()
        } else {
            round(x / flagDistance).toInt()
        }
        //val column = (x / textSize) - 1
       // var column = round(x / flagDistance).toInt()
//        if (column.rem(2).toInt() == 0) {
//            column = round((x  - flagDistance / 2) / flagDistance).toInt()
//        }

        //val column = (x - 0.5 * flagDistance) / flagDistance
        Log.e("getSelected", "$row $column")
        return try {
            map.first {
                it.y == column && it.x == row
            }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun drawSelectedBox(canvas: Canvas?) {
        var add = 0.0f
        if (selectedElement!!.x.rem(2).toInt() == 0) {
            add = flagDistance/2
        }
        canvas!!.drawRect(
            selectedElement!!.y * flagDistance + add,
            selectedElement!!.x * flagDistance,
            selectedElement!!.y * flagDistance + add + 100.0f,
            selectedElement!!.x * flagDistance + 100.0f,
            framePaint
        )
//        canvas.drawRect(
//            selectedCoordinate!!.y * textSize + 2 * textSize + 10,
//            selectedCoordinate!!.x * textSize + textSize + 10,
//            selectedCoordinate!!.y * textSize  + 2 * textSize + 2 * textSize - 10,
//            selectedCoordinate!!.x * textSize  + textSize + 2 * textSize - 10,
//            overlayBackgroundPaint
//        )
        ////canvas!!.drawText(it.text.toString(),left + textSize * it.coords.y,top + textSize * it.coords.x, localPaint)
    }
}
class Coordinates(var x: Int, var y: Int)

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