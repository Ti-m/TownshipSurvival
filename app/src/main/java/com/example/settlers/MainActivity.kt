package com.example.settlers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.settlers.MainActivity.Companion.flagDiameter
import com.example.settlers.MainActivity.Companion.flagDistance


class MainActivity : AppCompatActivity() {

    companion object {

        val flagDistance = 33.0f //33.0f
        val flagDiameter = flagDistance / 7
        val tileGridSize = 33
        val gameBoardBorder = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val zoomingLayout = ZoomingLayout(this)
        zoomingLayout.setBackgroundColor(Color.parseColor("#333333"))
        zoomingLayout.layoutParams = ViewGroup.LayoutParams(gameBoardBorder + tileGridSize * flagDistance.toInt(), gameBoardBorder + tileGridSize * flagDistance.toInt())
        val gw2 = GameWorld(context = this, tileGridSize = tileGridSize)
        gw2.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        zoomingLayout.addView(gw2)
        setContentView(zoomingLayout)
    }
}

enum class GroundType { Water, Grass, Desert, Mountain }
class Polygon(val a: Pair<Float, Float>, val b: Pair<Float, Float>, val c: Pair<Float, Float>)
class Element(val x: Int, val y: Int, var typeTop: GroundType, var typeBottom: GroundType, val value: Double )

class GameWorld(context: Context, tileGridSize: Int) : View(context) {

    val map = createMap(tileGridSize)

    companion object {

        val TAG = "GameWorld"

        fun createMap(size: Int): List<Element> {
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

        val flagPaint = Paint().apply {
            this.color = Color.LTGRAY
            this.style = Paint.Style.FILL
            this.textSize = 100.0f
        }
        val waterPaint = Paint().apply {
            this.color = Color.BLUE
            this.style = Paint.Style.FILL
        }
        val grassPaint = Paint().apply {
            this.color = Color.GREEN
            this.style = Paint.Style.FILL
        }
        val desertPaint = Paint().apply {
            this.color = Color.YELLOW
            this.style = Paint.Style.FILL
        }
        val mountainPaint = Paint().apply {
            this.color = Color.GRAY
            this.style = Paint.Style.FILL
        }


        fun drawGround(item: Element, canvas: Canvas, path: Path) {
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

        fun drawFlag(item: Element, canvas: Canvas) {
            val top = calcTop(item)
            drawFlag(top, canvas = canvas, paint = flagPaint)
        }


        private fun calcTop(item: Element): Polygon {
            if (item.y.rem(2) == 0) {
                return Polygon(
                    Pair(item.x * flagDistance + 0.5f * flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + 1.5f * flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + 1.0f * flagDistance, item.y * flagDistance - flagDistance)
                )
            } else {
                return Polygon(
                    Pair(item.x * flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + flagDistance * 0.5f, item.y * flagDistance - flagDistance)
                )
            }
        }

        private fun calcBottom(item: Element): Polygon {
            if (item.y.rem(2) == 0) {
                return Polygon(
                    Pair(item.x * flagDistance + 0.5f * flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + 1.5f * flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + 1.0f * flagDistance, item.y * flagDistance + flagDistance)
                )
            } else {
                return Polygon(
                    Pair(item.x * flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + flagDistance, item.y * flagDistance),
                    Pair(item.x * flagDistance + flagDistance * 0.5f, item.y * flagDistance + flagDistance)
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
            canvas.drawCircle(p.a.first, p.b.second, flagDiameter, paint)
        }
    }

    val path = Path()

    override fun onDraw(canvas: Canvas?) {
        Log.i(TAG, "onDraw")
        super.onDraw(canvas)

        map.forEach {
            drawGround(it, canvas!!, path)
        }
        map.forEach {
            drawFlag(it, canvas!!)
        }
    }
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