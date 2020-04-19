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

class Polygon(val a: Pair<Float, Float>, val b: Pair<Float, Float>, val c: Pair<Float, Float>)

class GameWorld(context: Context) : View(context) {

    companion object {
        fun drawPolygon(p: Polygon, canvas: Canvas, path: Path, paint: Paint) {
            path.reset()
            path.moveTo(p.a.first, p.a.second)
            path.lineTo(p.b.first, p.b.second)
            path.lineTo(p.c.first, p.c.second)
            canvas.drawPath(path, paint)
        }

        fun drawFlag(p: Pair<Float, Float>, canvas: Canvas, paint: Paint) {
            canvas.drawCircle(p.first, p.second, 15.0f, paint)
        }
    }

    val topleft = Pair(100.0f, 0.0f)
    val topRight = Pair(300.0f, 0.0f)
    val middleLeft = Pair(0.0f, 100.0f)
    val middle = Pair(200.0f, 100.0f)
    val middleRifght = Pair(400.0f, 100.0f)
    val bottomLeft = Pair(100.0f, 200.0f)
    val bottomRight = Pair(300.0f, 200.0f)
    val flags = listOf(topleft, topRight, middleLeft, middle, middleRifght, bottomLeft, bottomRight)
    val polygons = listOf(
        Polygon(topleft,middle,middleLeft),
        Polygon(topleft,topRight,middle),
        Polygon(middle,middleRifght,topRight),
        Polygon(middle,middleRifght,bottomRight),
        Polygon(middle,bottomRight,bottomLeft),
        Polygon(middle,bottomLeft,middleLeft)
    )

    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        textSize = 60.0f
    }
    val polygonPaint = Paint().apply {
        this.color = Color.GRAY
        this.style = Paint.Style.FILL
    }
    val flagPaint = Paint().apply {
        this.color = Color.YELLOW
        this.style = Paint.Style.FILL
        this.textSize = 100.0f
    }

    val path = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        polygons.forEach {
            drawPolygon(it, canvas!!, path, polygonPaint)
        }
        flags.map {
            drawFlag(it, canvas!!, flagPaint)
        }

    }


}