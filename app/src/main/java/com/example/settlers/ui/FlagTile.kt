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
    private val modeController: ModeController,
    context: Context?
) : View(context) {

    companion object {
        private val TAG = "FlagTile"
    }

    val coords: Hexagon = Hexagon(w = flagDistance / 2)
    private val flagPaint = ColorHelper.getFlagPaint()
    private val selectedPaint = ColorHelper.getSelectedPaint()
    private val groundPaint = ColorHelper.getGroundPaint(cell.type)
    private val textPaint = ColorHelper.getTextPaint()
    private val buildingPaint = ColorHelper.getBuildingPaint()
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        //Log.i(TAG, "onDraw chords ${cell.coordinates} res1 ${cell.transport}")
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

        cell.transport.forEachIndexed { index, resource ->
            val letter = when (resource) {
                is Wood -> "w"
                is Stone -> "s"
                else -> throw NotImplementedError()
            }
            if (index == 0) {
                canvas.drawText(
                    letter,
                    coords.center.first - textPaint.textSize / 2,
                    coords.center.second - textPaint.textSize * 0.75f,
                    textPaint
                )
            } else {
                canvas.drawText(
                    letter,
                    coords.center.first + textPaint.textSize / 2,
                    coords.center.second - textPaint.textSize * 0.75f,
                    textPaint
                )
            }
        }

        cell.building?.let {
            val letter = when (it) {
                is Townhall ->"T"
                is Lumberjack -> "L"
                is Road -> "R"
                is Tower -> "G"
                else -> throw NotImplementedError()
            }
            canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)

            //Show progress
            val progress = it.constructionCount.toString()[0].toString()
            canvas.drawText(
                progress,
                coords.center.first + coords.w / 1.75f,
                coords.center.second + textPaint.textSize * 0.3f,
                textPaint
            )
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
        if (modeController.mode == DialogMode.Build) {
            val dialog = BuildDialog.newInstance(cell.coordinates)
            dialog.show((context as MainActivity).supportFragmentManager, TAG)
        } else {
            //TODO use mapManger API here when extracting the method
            val content = """
                Storage: ${cell.storage.joinToString { it.javaClass.simpleName }}
                Production: ${cell.production.joinToString { it.javaClass.simpleName }}
                Requires: ${cell.requires.joinToString { it.javaClass.simpleName }}                
            """.trimIndent()
            val buildingContent = if (cell.building != null) {
                val progress = if (cell.building!!.isConstructed()) {
                    cell.building!!.productionCount
                } else {
                    cell.building!!.constructionCount
                }
                """
                    Building:
                    Progress : $progress
                """.trimIndent()
            } else {
                null
            }

            val dialog = InspectDialog.newInstance(
                title = "Inspect :: (x=${x}, y=${y})",
                message = "$content\n$buildingContent"
            )
            dialog.show((context as MainActivity).supportFragmentManager, TAG)
        }

        return super.performClick()
    }
}