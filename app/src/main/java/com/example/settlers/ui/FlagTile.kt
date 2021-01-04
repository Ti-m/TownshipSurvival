package com.example.settlers.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.settlers.*
import com.example.settlers.MainActivity.Companion.flagDistance

//Only used from code
@SuppressLint("ViewConstructor")
class GraphicalFlagTile(
    cell: Cell,
    modeController: ModeController,
    context: Context?,
    private val neighbourCalculator: HexagonNeighbourCalculator
) : FlagTile(cell, modeController, context) {

    //TODO can these stay here? or init only once?
    //private val image: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.hexagon_outline_32, null)
    private val roadNW: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.road_nw_32, null)
    private val roadN: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.road_n_32, null)
    private val roadNE: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.road_ne_32, null)
    private val roadS: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.road_s_32, null)
    private val roadSW: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.road_sw_32, null)
    private val roadSE: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.road_se_32, null)
    private val townhall: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.townhall_1_32, null)
    private val tower: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.tower_1_32, null)
    private val towerConstruction: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.tower_construction_1_32, null)
    private val lumberjack: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.lumberjack_1_32, null)
    private val lumberjackConstruction: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.lumberjack_construction_1_32, null)
    private val spawner: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.spawner_1_32, null)
    private val zombie: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.zombie_1_32, null)
    private val tree: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.tree_1_32, null)
    private val cactus: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.cactus_1_32, null)
    private val explosion_1: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.explosion_1_32, null)
    private val explosion_2: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.explosion_2_32, null)


//    override fun drawGround(canvas: Canvas) {
//        super.drawGround(canvas)
//        val bounds = canvas.clipBounds
//        image!!.bounds = bounds
//        //image!!.bounds = Rect(bounds.left+7, bounds.top+5, bounds.right-7, bounds.bottom-5)
//        image.draw(canvas)
//    }

    override fun drawRoad(canvas: Canvas) {
        //val neighbours = neighbourCalculator.getNeighboursOfCellDoubleCoords(cell.coordinates, allowAnyBuilding = true)//search for road Neighbours

        val neighbours = neighbourCalculator.getRoadConnections(cell.coordinates)
        neighbours.forEach {
            when (it) {
                RoadConnections.NorthWest -> {
                    roadNW!!.bounds = canvas.clipBounds
                    roadNW.draw(canvas)
                }
                RoadConnections.North -> {
                    roadN!!.bounds = canvas.clipBounds
                    roadN.draw(canvas)
                }
                RoadConnections.NorthEast -> {
                    roadNE!!.bounds = canvas.clipBounds
                    roadNE.draw(canvas)
                }
                RoadConnections.SouthEast -> {
                    roadSE!!.bounds = canvas.clipBounds
                    roadSE.draw(canvas)
                }
                RoadConnections.South -> {
                    roadS!!.bounds = canvas.clipBounds
                    roadS.draw(canvas)
                }
                RoadConnections.SouthWest -> {
                    roadSW!!.bounds = canvas.clipBounds
                    roadSW.draw(canvas)
                }
            }
        }

//        road!!.bounds = canvas.clipBounds
//        road.draw(canvas)
    }

    override fun drawTownhall(canvas: Canvas) {
        townhall!!.bounds = canvas.clipBounds
        townhall.draw(canvas)
    }

    override fun drawTower(canvas: Canvas) {
        if (cell.building!!.isConstructed()) {
            tower!!.bounds = canvas.clipBounds
            tower.draw(canvas)
        } else {
            towerConstruction!!.bounds = canvas.clipBounds
            towerConstruction.draw(canvas)
        }
    }

    override fun drawLumberjack(canvas: Canvas) {
        if (cell.building!!.isConstructed()) {
            lumberjack!!.bounds = canvas.clipBounds
            lumberjack.draw(canvas)
        } else {
            lumberjackConstruction!!.bounds = canvas.clipBounds
            lumberjackConstruction.draw(canvas)
        }
    }

    override fun drawSpawner(canvas: Canvas) {
        spawner!!.bounds = canvas.clipBounds
        spawner.draw(canvas)
    }

    override fun drawTree(canvas: Canvas) {
        tree!!.bounds = canvas.clipBounds
        tree.draw(canvas)
    }

    override fun drawCactus(canvas: Canvas) {
        cactus!!.bounds = canvas.clipBounds
        cactus.draw(canvas)
    }

    override fun drawZombie(canvas: Canvas) {
        zombie!!.bounds = canvas.clipBounds
        zombie.draw(canvas)
    }

    override fun drawAnimation(canvas: Canvas) {
        when (cell.animation!!.parts.firstOrNull()) {
            is ExplosionAnimationOne -> {
                explosion_1!!.bounds = canvas.clipBounds
                explosion_1.draw(canvas)
            }
            is ExplosionAnimationTwo -> {
                explosion_2!!.bounds = canvas.clipBounds
                explosion_2.draw(canvas)
            }
            is ExplosionAnimationThree -> {
                explosion_1!!.bounds = canvas.clipBounds
                explosion_1.draw(canvas)
            }
            is ShootAnimationOne -> {
                explosion_1!!.bounds = canvas.clipBounds //TODO Add the right animation
                explosion_1.draw(canvas)
            }
            is ShootAnimationTwo -> {
                explosion_2!!.bounds = canvas.clipBounds //TODO Add the right animation
                explosion_2.draw(canvas)
            }
            is ProjectileAnimationOne -> {
                explosion_1!!.bounds = canvas.clipBounds //TODO Add the right animation
                explosion_1.draw(canvas)
            }
            is ProjectileAnimationTwo -> {
                explosion_2!!.bounds = canvas.clipBounds //TODO Add the right animation
                explosion_2.draw(canvas)
            }
        }
    }
}

//Only used from code
@SuppressLint("ViewConstructor")
open class FlagTile(
    val cell: Cell,
    private val modeController: ModeController,
    context: Context?
) : View(context) {

    companion object {
        private const val TAG = "FlagTile"
    }

    val coords: Hexagon = Hexagon(w = flagDistance / 2)
    private val flagPaint = ColorHelper.getFlagPaint()
    private val selectedPaint = ColorHelper.getBuildingProgressPaint()
    private val groundPaint = ColorHelper.getGroundPaint(cell.type)
    private val textPaint = ColorHelper.getTextPaint()
    private val buildingPaint = ColorHelper.getBuildingPaint()
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        //Log.i(TAG, "onDraw chords ${cell.coordinates} res1 ${cell.transport}")
        super.onDraw(canvas!!)
        drawGround(canvas)
        drawFlag(canvas)
        drawBuilding(canvas)
        drawText(canvas)
    }

    protected open fun drawGround(canvas: Canvas) {
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

        if (cell.hasResources) {
            if (cell.type == GroundType.Grass) {
                drawTree(canvas)
            } else if (cell.type == GroundType.Desert) {
                drawCactus(canvas)
            }
        }

        if (cell.movingObject != null) {
            drawZombie(canvas)
        }

        if (cell.animation != null) {
            drawAnimation(canvas)
        }

    }

    open fun drawRoad(canvas: Canvas) {
        val letter = "R"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawTownhall(canvas: Canvas) {
        val letter = "T"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawLumberjack(canvas: Canvas) {
        val letter = "L"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawTower(canvas: Canvas) {
        val letter = "T"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawFletcher(canvas: Canvas) {
        val letter = "F"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawSpawner(canvas: Canvas) {
        //overwrite
    }

    open fun drawTree(canvas: Canvas) {
        //overwrite
    }

    open fun drawCactus(canvas: Canvas) {
        //overwrite
    }

    open fun drawAnimation(canvas: Canvas) {
        //overwrite
    }

    open fun drawZombie(canvas: Canvas) {
        val letter = "Z"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    private fun drawBuilding(canvas: Canvas) {
        cell.building?.let {
            when (it) {
                is Townhall -> drawTownhall(canvas)
                is Lumberjack -> drawLumberjack(canvas)
                is Road -> drawRoad(canvas)
                is Tower -> drawTower(canvas)
                is Fletcher -> drawFletcher(canvas)
                is Spawner -> drawSpawner(canvas)
                else -> throw NotImplementedError()
            }

            //Show progress
            if (!it.isConstructed()) {
                val progress = it.constructionCount.toString()[0].toString()
                canvas.drawText(
                    progress,
                    coords.center.first + coords.w / 1.75f,
                    coords.center.second + textPaint.textSize * 0.3f,
                    selectedPaint
                )
            } else if (it.productionCount != 0) {
                val progress = it.productionCount.toString()[0].toString()
                canvas.drawText(
                    progress,
                    coords.center.first + coords.w / 1.75f,
                    coords.center.second + textPaint.textSize * 0.3f,
                    textPaint
                )
            }
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
                coordinates = cell.coordinates,
                message = "$content\n$buildingContent"
            )
            dialog.show((context as MainActivity).supportFragmentManager, TAG)
        }

        return super.performClick()
    }
}