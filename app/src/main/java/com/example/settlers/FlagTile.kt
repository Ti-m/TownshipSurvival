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

    val coords: Hexagon = Hexagon(a = flagDistance / 2)
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
                //selectedElement = getSelectedElement( event.x - translationX, event.y - translationY)
                performClick()

            }
        }
        return true
    }

    override fun performClick(): Boolean {
        isSelectedTile = true
        invalidate()
        return super.performClick()
    }
}

class Hexagon(val a: Float) {

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
        val base = Paint().apply {
            this.style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 1.0f
        }
        return when (type) {
            GroundType.Water -> base.apply {
                this.color = Color.BLUE
            }
            GroundType.Grass -> base.apply {
                this.color = Color.GREEN
            }
            GroundType.Desert -> base.apply {
                this.color = Color.YELLOW
            }
            GroundType.Mountain -> base.apply {
                this.color = Color.GRAY
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
            this.style = Paint.Style.FILL_AND_STROKE
            this.strokeWidth = 1.0f
            //this.textAlign = Paint.Align.CENTER
        }
    }
}