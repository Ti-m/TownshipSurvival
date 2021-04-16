package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HousingTests {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun `getHousingDemand one lvl 1`() {

        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Set, Type.Building, Lumberjack())))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        val result = d.mapManager.getHousingDemand()

        Assert.assertEquals(HousingDemand(lvl1 = 1, lvl2 = 0, lvl3 = 0), result)
    }

    @Test
    fun `getHousingDemand one lvl 1 and one lvl 2`() {

        val coord1 = Coordinates(0,0)
        val coord2 = Coordinates(2,0)
        d.gameStateManager.applyStates(listOf(
            GameState(coord1, Operator.Set, Type.Building, Lumberjack()),
            GameState(coord2, Operator.Set, Type.Building, Lumbermill())
        ))
        d.mapManager.queryBuilding(coord1)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord2)!!.setConstructionFinished()

        val result = d.mapManager.getHousingDemand()

        Assert.assertEquals(HousingDemand(lvl1 = 1, lvl2 = 1, lvl3 = 0), result)
    }

    @Test
    fun `getHousingDemand all`() {

        val coord1 = Coordinates(0,0)
        val coord2 = Coordinates(2,0)
        val coord3 = Coordinates(4,0)
        val coord4 = Coordinates(6,0)
        val coord5 = Coordinates(1,1)
        val coord6 = Coordinates(3,1)
        val coord7 = Coordinates(5,1)
        val coord8 = Coordinates(7,1)
        val coord9 = Coordinates(0,2)
        d.gameStateManager.applyStates(listOf(
            GameState(coord1, Operator.Set, Type.Building, Lumberjack()),
            GameState(coord2, Operator.Set, Type.Building, Lumbermill()),
            GameState(coord3, Operator.Set, Type.Building, Stonemason()),
            GameState(coord4, Operator.Set, Type.Building, Forester()),
            GameState(coord5, Operator.Set, Type.Building, Fletcher()),
            GameState(coord6, Operator.Set, Type.Building, Tower()),
            GameState(coord7, Operator.Set, Type.Building, Pyramid()),
            GameState(coord8, Operator.Set, Type.Building, Road()),
            GameState(coord9, Operator.Set, Type.Building, Townhall()),
        ))
        d.mapManager.queryBuilding(coord1)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord2)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord3)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord4)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord5)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord6)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord7)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord8)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord9)!!.setConstructionFinished()

        val result = d.mapManager.getHousingDemand()

        Assert.assertEquals(HousingDemand(lvl1 = 3, lvl2 = 3, lvl3 = 0), result)
    }

    @Test
    fun `count lumberjacks, 2 finished and 2 unfinised`() {
        val coord1 = Coordinates(0,0)
        val coord2 = Coordinates(2,0)
        val coord3 = Coordinates(4,0)
        val coord4 = Coordinates(6,0)
        d.gameStateManager.applyStates(listOf(
            GameState(coord1, Operator.Set, Type.Building, Lumberjack()),
            GameState(coord2, Operator.Set, Type.Building, Lumberjack()),
            GameState(coord3, Operator.Set, Type.Building, Lumberjack()),
            GameState(coord4, Operator.Set, Type.Building, Lumberjack()),
        ))
        d.mapManager.queryBuilding(coord1)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord2)!!.setConstructionFinished()

        val finished = d.mapManager.getFinishedBuildingsOfTypeCount(Lumberjack())
        val unfinished = d.mapManager.getUnfinishedBuildingsOfTypeCount(Lumberjack())

        Assert.assertEquals(2, finished)
        Assert.assertEquals(2, unfinished)
    }

    @Test
    fun `count lumbermills, 2 finished and 2 unfinised`() {
        val coord1 = Coordinates(0,0)
        val coord2 = Coordinates(2,0)
        val coord3 = Coordinates(4,0)
        val coord4 = Coordinates(6,0)
        d.gameStateManager.applyStates(listOf(
            GameState(coord1, Operator.Set, Type.Building, Lumbermill()),
            GameState(coord2, Operator.Set, Type.Building, Lumbermill()),
            GameState(coord3, Operator.Set, Type.Building, Lumbermill()),
            GameState(coord4, Operator.Set, Type.Building, Lumbermill()),
        ))
        d.mapManager.queryBuilding(coord1)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord2)!!.setConstructionFinished()

        val finished = d.mapManager.getFinishedBuildingsOfTypeCount(Lumbermill())
        val unfinished = d.mapManager.getUnfinishedBuildingsOfTypeCount(Lumbermill())

        Assert.assertEquals(2, finished)
        Assert.assertEquals(2, unfinished)
    }
}