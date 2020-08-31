package com.example.settlers.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.settlers.*
import com.example.settlers.MainActivity.Companion.flagDistance

class FlagTile(
    val cell: Cell,
    private val handler: BuildDialogHandler,
    context: Context?
) : View(context) {

    companion object {
        private val TAG = "FlagTile"
    }

    val coords: Hexagon = Hexagon(a = flagDistance / 2)
    private val flagPaint = ColorHelper.getFlagPaint()
    private val selectedPaint = ColorHelper.getSelectedPaint()
    private val groundPaint = ColorHelper.getGroundPaint(cell.type)
    private val textPaint = ColorHelper.getTextPaint()
    private val buildingPaint = ColorHelper.getBuildingPaint()
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        Log.i(TAG, "onDraw chords ${cell.coordinates} res1 ${cell.resource1}")
        super.onDraw(canvas!!)
        drawGround(canvas)
        drawFlag(canvas)
        drawText(canvas)
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

//        if (isSelectedTile) {
//            canvas.drawPath(path, selectedPaint)
//        } else {
//            canvas.drawPath(path, groundPaint)
//        }
         if (cell.building != null) {
             canvas.drawPath(path, buildingPaint)
         } else {
            canvas.drawPath(path, groundPaint)
        }


    }

    private fun drawText(canvas: Canvas) {

        cell.resource1?.let {
            val letter = when (it) {
                Resource.Wood -> "w"
                Resource.Stone -> "s"
            }
            canvas.drawText(letter, coords.center.first - textPaint.textSize / 2, coords.center.second - textPaint.textSize * 0.75f, textPaint)
        }

        cell.resource2?.let {
            val letter = when (it) {
                Resource.Wood -> "w"
                Resource.Stone -> "s"
            }
            canvas.drawText(letter, coords.center.first + textPaint.textSize / 2, coords.center.second - textPaint.textSize * 0.75f, textPaint)
        }

        cell.building?.type?.let {
            val letter = when (it) {
                BuildingType.Townhall ->"T"
                BuildingType.Lumberjack -> "L"
                BuildingType.Road -> "R"
            }
            canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
        }

//        if  (cell.carrier) {
//            canvas.drawText("c", coords.center.first - textPaint.textSize / 2, coords.center.second + textPaint.textSize * 1.2f, textPaint)
//        }

        cell.worker?.let {
            val letter = when (it) {
                Worker.Construction -> "C"
            }
            canvas.drawText(letter, coords.center.first + textPaint.textSize / 2, coords.center.second + textPaint.textSize * 1.2f, textPaint)
        }
    }

    private fun drawFlag(canvas: Canvas) {
        //val top = calcTop(item)
        //drawFlag(top, canvas = canvas, paint = flagPaint)
        canvas.drawCircle(coords.center.first, coords.center.second,
            MainActivity.flagDiameter, flagPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i(TAG, "onTouchEvent")
        super.onTouchEvent(event)
        when (event!!.action) {
            MotionEvent.ACTION_UP -> {
                //selectedElement = getSelectedElement( event.x - translationX, event.y - translationY)
                performClick()

            }
        }
        return true
    }

    override fun performClick(): Boolean {

        val dialog = BuildDialog(
            items = BuildingType.values().map { it.name },
            handler = { dialog, which ->
                handler.cell = this.cell
                handler.onClick(dialog, which)
            }
        )
        dialog.show((context as MainActivity).supportFragmentManager, TAG)
        //invalidate()
        return super.performClick()
    }
}