package com.example.settlers.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.settlers.*
import com.example.settlers.MainActivity.Companion.flagDistance

//Only used from code
@SuppressLint("ViewConstructor")
class GraphicalFlagTile(
    context: Context,
    cell: Cell,
    modeController: ModeController,
    private val neighbourCalculator: HexagonNeighbourCalculator,
) : FlagTile(context, cell, modeController) {

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
    private val lumbermill: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.lumbermill_1_64, null)
    private val lumbermillConstruction: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.lumbermill_construction_1_64, null)
    private val stonemason: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.stonemason_1_64, null)
    private val stonemasonConstruction: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.stonemason_construction_1_64, null)
    private val spawner: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.spawner_1_32, null)
    private val zombie: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.zombie_1_32, null)
    private val tree1: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.tree_1_32, null)
    private val tree2: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.tree_2_32, null)
    private val palm: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.palm_1_32, null)
    private val explosion_1: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.explosion_1_32, null)
    private val explosion_2: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.explosion_2_32, null)
    private val lumber: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.lumber_1_8, null)
    private val stone: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.stone_1_8, null)
    private val wood: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.wood_1_8, null)
    private val arrow: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.arrow_1_8, null)
    private val rock: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.rock_1_32, null)
    private val grass: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.grass_1_64, null)
    private val desert: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.desert_1_64, null)
    private val water: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.water_1_64, null)
    private val mountain: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.mountain_1_64, null)

    override fun drawRoad(canvas: Canvas) {

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
    }

    override fun drawGrassTexture(canvas: Canvas) {
        draw32(canvas, grass!!)
    }

    override fun drawDesertTexture(canvas: Canvas) {
        draw32(canvas, desert!!)
    }

    override fun drawWaterTexture(canvas: Canvas) {
        draw32(canvas, water!!)
    }

    override fun drawMountainTexture(canvas: Canvas) {
        draw32(canvas, mountain!!)
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

    override fun drawLumbermill(canvas: Canvas) {
        if (cell.building!!.isConstructed()) {
            draw32(canvas, lumbermill!!)
        } else {
            draw32(canvas, lumbermillConstruction!!)
        }
    }

    override fun drawStoneMason(canvas: Canvas) {
        if (cell.building!!.isConstructed()) {
            draw32(canvas, stonemason!!)
        } else {
            draw32(canvas, stonemasonConstruction!!)
        }
    }

    override fun drawSpawner(canvas: Canvas) {
        spawner!!.bounds = canvas.clipBounds
        spawner.draw(canvas)
    }

    override fun drawTree(canvas: Canvas) {
        if (cell.textureVariant == 0) {
            draw8(canvas, tree1!!, 0)
        } else {
            draw8(canvas, tree2!!, 0)
        }
    }

    override fun drawPalm(canvas: Canvas) {
        draw8(canvas, palm!!, 0)
    }

    override fun drawRock(canvas: Canvas) {
        draw8(canvas, rock!!, 0)
    }

    override fun drawZombie(canvas: Canvas) {
        zombie!!.bounds = canvas.clipBounds
        zombie.draw(canvas)
    }

    override fun drawWood(canvas: Canvas, index: Int) {
        draw8(canvas, wood!!, index)
    }

    override fun drawLumber(canvas: Canvas, index: Int) {
        draw8(canvas, lumber!!, index)
    }

    override fun drawStone(canvas: Canvas, index: Int) {
        draw8(canvas, stone!!, index)
    }

    override fun drawArrow(canvas: Canvas, index: Int) {
        draw8(canvas, arrow!!, index)
    }

    private fun draw8(canvas: Canvas, drawable: Drawable, index: Int) {
        val size = 8
        val offsetRight = 16
        val offsetLeft = 6
        if (index == 0) {
            drawable.bounds = Rect(offsetLeft + 0,0,offsetLeft + size, size)
            drawable.draw(canvas)
        } else {
            drawable.bounds = Rect(offsetRight + 0,0,offsetRight + size, size)
            drawable.draw(canvas)
        }
    }

    private fun draw32(canvas: Canvas, drawable: Drawable) {
        val size = 32
//        val offsetLeft = 6
        drawable.bounds = Rect(0,0, size, size)
        drawable.draw(canvas)
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
    context: Context,
    val cell: Cell,
    private val modeController: ModeController
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
    private val stoppedPaint = ColorHelper.getStoppedPaint()
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        //Log.i(TAG, "onDraw chords ${cell.coordinates} res1 ${cell.transport}")
        super.onDraw(canvas!!)
        drawGround(canvas)
        if (cell.worldResource != null) {
            drawWorldResource(canvas)
        }
        if (cell.movingObject != null) {
            drawZombie(canvas)
        }
        if (cell.animation != null) {
            drawAnimation(canvas)
        }
        drawFlag(canvas)
        drawBuilding(canvas)
        drawResource(canvas)
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
            if (cell.building!!.stopDelivery) {
                canvas.drawPath(path, stoppedPaint)
            } else {
                canvas.drawPath(path, buildingPaint)
            }
        } else {
            canvas.drawPath(path, groundPaint)
            drawGroundTexture(canvas)
        }


    }

    private fun drawWorldResource(canvas: Canvas) {
        if (cell.worldResource is Tree) {
            if (cell.type == GroundType.Desert) {
                drawPalm(canvas)
            } else {
                drawTree(canvas)
            }
        } else if (cell.worldResource is Rock) {
            drawRock(canvas)
        }
    }

    private fun drawGroundTexture(canvas: Canvas) {
        when (cell.type) {
            GroundType.Water -> drawWaterTexture(canvas)
            GroundType.Grass -> drawGrassTexture(canvas)
            GroundType.Desert -> drawDesertTexture(canvas)
            GroundType.Mountain -> drawMountainTexture(canvas)
        }
    }

    open fun drawGrassTexture(canvas: Canvas) {
        //overwrite
    }

    open fun drawDesertTexture(canvas: Canvas) {
        //overwrite
    }

    open fun drawWaterTexture(canvas: Canvas) {
        //overwrite
    }

    open fun drawMountainTexture(canvas: Canvas) {
        //overwrite
    }

    open fun drawRoad(canvas: Canvas) {
        val letter = "R"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawPyramid(canvas: Canvas) {
        val letter = "P"
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

    open fun drawForester(canvas: Canvas) {
        val letter = "B"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawLumbermill(canvas: Canvas) {
        val letter = "M"
        canvas.drawText(letter, coords.center.first, coords.center.second + textPaint.textSize * 0.3f, textPaint)
    }

    open fun drawStoneMason(canvas: Canvas) {
        val letter = "S"
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

    open fun drawPalm(canvas: Canvas) {
        //overwrite
    }

    open fun drawRock(canvas: Canvas) {
        val letter = "r"
        //+5 to move the letter out of the pole
        canvas.drawText(letter, coords.center.first + 5, coords.center.second + textPaint.textSize * 0.3f, textPaint)
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
                is Forester -> drawForester(canvas)
                is Lumbermill -> drawLumbermill(canvas)
                is Stonemason -> drawStoneMason(canvas)
                is Road -> drawRoad(canvas)
                is Pyramid -> drawPyramid(canvas)
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

    private fun drawResource(canvas: Canvas) {

        cell.transport.forEachIndexed { index, resource ->
            when (resource) {
                is Wood -> drawWood(canvas, index)
                is Lumber -> drawLumber(canvas, index)
                is Stone -> drawStone(canvas, index)
                is Arrow -> drawArrow(canvas, index)
                else -> throw NotImplementedError()
            }
        }

//        if  (cell.carrier) {
//            canvas.drawText("c", coords.center.first - textPaint.textSize / 2, coords.center.second + textPaint.textSize * 1.2f, textPaint)
//        }

//        cell.worker?.let {
//            val letter = when (it) {
//                Worker.Construction -> "C"
//            }
//            canvas.drawText(letter, coords.center.first + textPaint.textSize / 2, coords.center.second + textPaint.textSize * 1.2f, textPaint)
//        }
    }

    open fun drawWood(canvas: Canvas, index: Int) {
        val letter = "w"
        drawText(canvas, index, letter)
    }

    open fun drawLumber(canvas: Canvas, index: Int) {
        val letter = "l"
        drawText(canvas, index, letter)
    }

    open fun drawStone(canvas: Canvas, index: Int) {
        val letter = "s"
        drawText(canvas, index, letter)
    }

    open fun drawArrow(canvas: Canvas, index: Int) {
        val letter = "a"
        drawText(canvas, index, letter)
    }

    private fun drawText(canvas: Canvas, index: Int, letter: String) {
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

            val deliveryState = when (cell.building?.stopDelivery) {
                null -> StopDeliveryState.NoBuilding
                true -> StopDeliveryState.Stopped
                false -> StopDeliveryState.Normal
            }
            val dialog = InspectDialog.newInstance(
                coordinates = cell.coordinates,
                message = "$content\n$buildingContent",
                stopDelivery = deliveryState
            )
            dialog.show((context as MainActivity).supportFragmentManager, TAG)
        }

        return super.performClick()
    }
}