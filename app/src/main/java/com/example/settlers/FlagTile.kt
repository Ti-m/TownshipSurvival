package com.example.settlers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.settlers.MainActivity.Companion.flagDistance
import kotlin.math.sqrt

class FlagTile(val element: Element, context: Context?) : View(context) {

    private val coords: Hexagon = Hexagon(a = flagDistance / 2)
    private val flagPaint = ColorHelper.getFlagPaint()
    private val selectedPaint = ColorHelper.getSelectedPaint()
    private val groundPaint = ColorHelper.getGroundPaint(element.type)
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        Log.i("FlagTile", "onDraw")
        super.onDraw(canvas!!)
        drawGround(canvas)
        drawFlag(canvas)
    }

    private fun drawGround(canvas: Canvas) {
        //even rows need distance/" offset

//        val top = calcTop(item)
//        val bottom = calcBottom(item)
//
//        val colorTop = when(item.typeTop) {
//            GroundType.Grass -> grassPaint
//            GroundType.Desert -> desertPaint
//            GroundType.Water -> waterPaint
//            GroundType.Mountain -> mountainPaint
//        }

//            val paint = Paint().apply {
//                this.color = ColorTools.getIntFromColor((item.value * 255).toInt(),(item.value * 255).toInt(),0)
//                this.style = Paint.Style.FILL
//            }
//            drawPolygon(p = top, canvas = canvas, path = path, paint = paint)
//        drawPolygon(p = top, canvas = canvas, path = path, paint = colorTop)
//        val colorBottom = when(item.typeBottom) {
//            GroundType.Grass -> grassPaint
//            GroundType.Desert -> desertPaint
//            GroundType.Water -> waterPaint
//            GroundType.Mountain -> mountainPaint
//        }
////            drawPolygon(p = bottom, canvas = canvas, path = path, paint = paint)
//        drawPolygon(p = bottom, canvas = canvas, path = path, paint = colorBottom)
        path.reset()
        path.moveTo(coords.p1.first, coords.p1.second)
        path.lineTo(coords.p2.first, coords.p2.second)
        path.lineTo(coords.p4.first, coords.p4.second)
        path.lineTo(coords.p6.first, coords.p6.second)
        path.lineTo(coords.p5.first, coords.p5.second)
        path.lineTo(coords.p3.first, coords.p3.second)
        path.lineTo(coords.p1.first, coords.p1.second)

        if (isSelectedTile) {
            canvas.drawPath(path, selectedPaint)
        } else {
            canvas.drawPath(path, groundPaint)
        }
    }

    private fun drawFlag(canvas: Canvas) {
        //val top = calcTop(item)
        //drawFlag(top, canvas = canvas, paint = flagPaint)
        canvas.drawCircle(flagDistance / 2, flagDistance / 2, MainActivity.flagDiameter, flagPaint)
    }

    private var isSelectedTile = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i("FlagTile", "onTouchEvent")
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                isSelectedTile = false
            }
            MotionEvent.ACTION_UP -> {
                Log.d("foo-gameworld", "x is $x")
                Log.d("foo-gameworld", "translationX is $translationX")
                Log.d("foo-gameworld", "left is $left")
                Log.d("foo-gameworld", "event.x is ${event.x}")
                Log.d("foo-gameworld", "scaleX is $scaleX")
                //selectedElement = getSelectedElement( event.x - translationX, event.y - translationY)
                performClick()

            }
        }
        //Log.e("onTouchEvent", "fired ${event.action} ${selectedElement?.x} ${selectedElement?.y}")
        //return super.onTouchEvent(event)
        return true // return true in parentview
    }

    override fun performClick(): Boolean {
        isSelectedTile = true
        invalidate()
        return super.performClick()
    }
}

class Hexagon(private val a: Float) {

    //a is the outer radius

    /*
    * ....p1..p2...
    * .p3...c...p4.
    * ....p5..p6..
    * */

    val p1: Pair<Float, Float>
    val p2: Pair<Float, Float>
    val p3: Pair<Float, Float>
    val p4: Pair<Float, Float>
    val p5: Pair<Float, Float>
    val p6: Pair<Float, Float>

    companion object {
        private val sqrt3 = sqrt(3.0f)
    }
    val r = sqrt3 * a / 2

    init {

        val center = Pair(a,r)
        //calculated from center which is 0,0
        p3 = Pair(center.first - a  , center.second)
        p4 = Pair(center.first + a  , center.second)
        p1 = Pair(center.first - a/2, center.second - r)
        p2 = Pair(center.first + a/2, center.second - r)
        p5 = Pair(center.first - a/2, center.second + r)
        p6 = Pair(center.first + a/2, center.second + r)
    }
}

object ColorHelper {
    fun getGroundPaint(type: GroundType): Paint {
        return when (type) {
            GroundType.Water -> Paint().apply {
                this.color = Color.BLUE
                this.style = Paint.Style.FILL
            }
            GroundType.Grass -> Paint().apply {
                this.color = Color.GREEN
                this.style = Paint.Style.FILL
            }
            GroundType.Desert -> Paint().apply {
                this.color = Color.YELLOW
                this.style = Paint.Style.FILL
            }
            GroundType.Mountain -> Paint().apply {
                this.color = Color.GRAY
                this.style = Paint.Style.FILL
            }
        }
    }

    fun getFlagPaint(): Paint {
        return Paint().apply {
            this.color = Color.LTGRAY
            this.style = Paint.Style.FILL
            this.textSize = 100.0f
        }
    }

    fun getSelectedPaint() : Paint {
        return Paint().apply {
            this.color = Color.RED
            this.style = Paint.Style.STROKE
            this.strokeWidth = 3.0f
            this.textAlign = Paint.Align.CENTER
        }
    }
}