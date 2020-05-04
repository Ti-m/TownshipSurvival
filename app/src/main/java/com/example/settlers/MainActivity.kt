package com.example.settlers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val scrollingLayout = ScrollingLayout(this)

        scrollingLayout.setBackgroundColor(Color.parseColor("#333333"))
        scrollingLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        val gw = GameWorld(this)
        gw.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        scrollingLayout.addView(gw)
        setContentView(scrollingLayout)
    }
}
enum class GroundType { Grass, Desert }
class Polygon(val a: Pair<Float, Float>, val b: Pair<Float, Float>, val c: Pair<Float, Float>)
class Element(val x: Int, val y: Int, var typeTop: GroundType, var typeBottom: GroundType )

class GameWorld(context: Context) : View(context) {

    val map = createMap(5)

    companion object {

        fun createMap(size: Int): List<Element> {
            val map = Array(size) {
                Array<Double?>(size) {
                    null
                }
            }
            map[0][0] = 0.0
            map[0][size-1] = 0.0
            map[size-1][0] = 0.0
            map[size-1][size-1] = 0.0
            val interpolator = TerrainInterpolator()
            interpolator.interpolate(map, size, 1.0, 1.0)
            return listOf(
                Element(x = 1, y = 1, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 2, y = 1, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 3, y = 1, typeTop = GroundType.Grass, typeBottom = GroundType.Desert),
                Element(x = 4, y = 1, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 5, y = 1, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 1, y = 2, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 2, y = 2, typeTop = GroundType.Desert, typeBottom = GroundType.Desert),
                Element(x = 3, y = 2, typeTop = GroundType.Grass, typeBottom = GroundType.Desert),
                Element(x = 4, y = 2, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 5, y = 2, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 1, y = 3, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 2, y = 3, typeTop = GroundType.Desert, typeBottom = GroundType.Grass),
                Element(x = 3, y = 3, typeTop = GroundType.Desert, typeBottom = GroundType.Desert),
                Element(x = 4, y = 3, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 5, y = 3, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 1, y = 4, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 2, y = 4, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 3, y = 4, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 4, y = 4, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 5, y = 4, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 1, y = 5, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 2, y = 5, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 3, y = 5, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 4, y = 5, typeTop = GroundType.Grass, typeBottom = GroundType.Grass),
                Element(x = 5, y = 5, typeTop = GroundType.Grass, typeBottom = GroundType.Grass)
            ).map {
                it.apply {
                    if (map[this.x-1][this.y-1]!! < 1.0) {
                        this.typeTop = GroundType.Grass
                        this.typeBottom = GroundType.Grass
                    } else {
                        this.typeTop = GroundType.Desert
                        this.typeBottom = GroundType.Desert
                    }
                }
            }
        }

        val flagPaint = Paint().apply {
            this.color = Color.LTGRAY
            this.style = Paint.Style.FILL
            this.textSize = 100.0f
        }
        val grassPaint = Paint().apply {
            this.color = Color.GREEN
            this.style = Paint.Style.FILL
        }
        val desertPaint = Paint().apply {
            this.color = Color.YELLOW
            this.style = Paint.Style.FILL
        }

        val distance = 100.0f

        fun drawGround(item: Element, canvas: Canvas, path: Path) {
            //even rows need distance/" offset

            val top = calcTop(item)
            val bottom = calcBottom(item)

            val colorTop = when(item.typeTop) {
                GroundType.Grass -> grassPaint
                GroundType.Desert -> desertPaint
            }
            drawPolygon(p = top, canvas = canvas, path = path, paint = colorTop)
            val colorBottom = when(item.typeBottom) {
                GroundType.Grass -> grassPaint
                GroundType.Desert -> desertPaint
            }
            drawPolygon(p = bottom, canvas = canvas, path = path, paint = colorBottom)
        }

        fun drawFlag(item: Element, canvas: Canvas) {
            val top = calcTop(item)
            drawFlag(top, canvas = canvas, paint = flagPaint)
        }


        private fun calcTop(item: Element): Polygon {
            if (item.y.rem(2) == 0) {
                return Polygon(
                    Pair(item.x * distance + 0.5f * distance, item.y * distance),
                    Pair(item.x * distance + 1.5f * distance, item.y * distance),
                    Pair(item.x * distance + 1.0f * distance, item.y * distance - distance)
                )
            } else {
                return Polygon(
                    Pair(item.x * distance, item.y * distance),
                    Pair(item.x * distance + distance, item.y * distance),
                    Pair(item.x * distance + distance * 0.5f, item.y * distance - distance)
                )
            }
        }

        private fun calcBottom(item: Element): Polygon {
            if (item.y.rem(2) == 0) {
                return Polygon(
                    Pair(item.x * distance + 0.5f * distance, item.y * distance),
                    Pair(item.x * distance + 1.5f * distance, item.y * distance),
                    Pair(item.x * distance + 1.0f * distance, item.y * distance + distance)
                )
            } else {
                return Polygon(
                    Pair(item.x * distance, item.y * distance),
                    Pair(item.x * distance + distance, item.y * distance),
                    Pair(item.x * distance + distance * 0.5f, item.y * distance + distance)
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

        private fun drawFlag(p: Pair<Float, Float>, canvas: Canvas, paint: Paint) {
            canvas.drawCircle(p.first, p.second, 15.0f, paint)
        }

        private fun drawFlag(p: Polygon, canvas: Canvas, paint: Paint) {
            canvas.drawCircle(p.a.first, p.b.second, 15.0f, paint)
        }
    }

//    val topleft = Pair(100.0f, 0.0f)
//    val topRight = Pair(300.0f, 0.0f)
//    val middleLeft = Pair(0.0f, 100.0f)
//    val middle = Pair(200.0f, 100.0f)
//    val middleRifght = Pair(400.0f, 100.0f)
//    val bottomLeft = Pair(100.0f, 200.0f)
//    val bottomRight = Pair(300.0f, 200.0f)
//    val flags = listOf(topleft, topRight, middleLeft, middle, middleRifght, bottomLeft, bottomRight)
//    val polygons = listOf(
//        Polygon(topleft,middle,middleLeft),
//        Polygon(topleft,topRight,middle),
//        Polygon(middle,middleRifght,topRight),
//        Polygon(middle,middleRifght,bottomRight),
//        Polygon(middle,bottomRight,bottomLeft),
//        Polygon(middle,bottomLeft,middleLeft)
//    )

    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        textSize = 60.0f
    }
    val polygonPaint = Paint().apply {
        this.color = Color.GRAY
        this.style = Paint.Style.FILL
    }

    val path = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        polygons.forEach {
//            drawPolygon(it, canvas!!, path, polygonPaint)
//        }
//        flags.map {
//            drawFlag(it, canvas!!, flagPaint)
//        }
        map.forEach {
            drawGround(it, canvas!!, path)
        }
        map.forEach {
            drawFlag(it, canvas!!)
        }

    }


}