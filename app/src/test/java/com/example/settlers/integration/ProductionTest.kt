package com.example.settlers.integration

import com.example.settlers.BasicTestDependencies
import com.example.settlers.Coordinates
import com.example.settlers.GameStateCreator
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProductionTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun `runProduction - no double production, if something is set into storage`() {
        val cTown = Coordinates(0,0)
        val cRoad1 = Coordinates(2,0)
        val cRoad2 = Coordinates(4,0)
        val cRoad3 = Coordinates(5,1)
        val cLumber = Coordinates(1,1)
        val cFletcher1 = Coordinates(4,2)
        val tree1 = Coordinates(3,1)
        val tree2 = Coordinates(7,1)
        val tree3 = Coordinates(0,2)

        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTownhall(cTown),
            GameStateCreator.createRoad(cRoad1),
            GameStateCreator.createRoad(cRoad2),
            GameStateCreator.createRoad(cRoad3),
            GameStateCreator.createLumberjack(cLumber),
            GameStateCreator.createFletcher(cFletcher1),
            GameStateCreator.createTree(tree1),
            GameStateCreator.createTree(tree2),
            GameStateCreator.createTree(tree3),
        ))
        for (x in 1 .. 14) {
            //d.gameStateManager.runProduction(cell)
            d.gameStateManager.tick()
        }
        //Lumberjack not finished yet
        Assert.assertEquals(90, d.mapManager.queryBuilding(cLumber)!!.constructionCount)
        d.gameStateManager.tick()
        //Now it's finished
        val lumberjack = d.mapManager.queryBuilding(cLumber)!!
        Assert.assertTrue(lumberjack.isConstructed())
        lumberjack.workerLivesAt = Coordinates(0,0)//Set a dummy worker, anything except null

        for (x in 1 .. 13) {
            //d.gameStateManager.runProduction(cell)
            d.gameStateManager.tick()
        }
        //Fletcher not finished yet
        Assert.assertEquals(90, d.mapManager.queryBuilding(cFletcher1)!!.constructionCount)
        d.gameStateManager.tick()
        //Now it's finished
        Assert.assertTrue(d.mapManager.queryBuilding(cFletcher1)!!.isConstructed())

        d.mapManager.queryBuilding(cFletcher1)!!.workerLivesAt = Coordinates(0,0)//Set a dummy worker, anything except null

        for (x in 1 .. 7) {
            //d.gameStateManager.runProduction(cell)
            d.gameStateManager.tick()
        }
        //Construction reached 10 now
        Assert.assertEquals(10, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(20, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(30, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(40, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(50, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(60, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(70, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(80, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(90, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(0, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
        d.gameStateManager.tick()
        Assert.assertEquals(10, d.mapManager.queryBuilding(cFletcher1)!!.productionCount)
    }
}