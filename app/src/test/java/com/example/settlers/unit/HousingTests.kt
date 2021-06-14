package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

//TODO Revisit this tests. Are they still relevant? Or are they obsolete now?
class HousingTests {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    //TODO move to MapManagerTest?
    @Test
    fun `getHousingDemand one lvl 1`() {

        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Set, Type.Building, Lumberjack())))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        val result = d.mapManager.getHousingDemand()

        assertEquals(HousingDemand(lvl1 = 1, lvl2 = 0, lvl3 = 0, lvl4 = 0), result)
    }

    //TODO move to MapManagerTest?
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

        assertEquals(HousingDemand(lvl1 = 1, lvl2 = 1, lvl3 = 0, lvl4 = 0), result)
    }

    //TODO move to MapManagerTest?
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
        val coord10 = Coordinates(2,2)
        val coord11 = Coordinates(4,2)
        val coord12 = Coordinates(6,2)
        val coord13 = Coordinates(1,3)
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
            GameState(coord10, Operator.Set, Type.Building, HouseLevel1()),
            GameState(coord11, Operator.Set, Type.Building, HouseLevel2()),
            GameState(coord12, Operator.Set, Type.Building, HouseLevel3()),
            GameState(coord13, Operator.Set, Type.Building, Fisherman()),
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
        d.mapManager.queryBuilding(coord10)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord11)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord12)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord13)!!.setConstructionFinished()

        val result = d.mapManager.getHousingDemand()

        assertEquals(HousingDemand(lvl1 = 4, lvl2 = 3, lvl3 = 0, lvl4 = 0), result)
    }

    //TODO move to MapManagerTest?
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

        val finished = d.mapManager.getFinishedBuildingsOfTypeCount<Lumberjack>()
        val unfinished = d.mapManager.getUnfinishedBuildingsOfTypeCount<Lumberjack>()

        assertEquals(2, finished)
        assertEquals(2, unfinished)
    }

    //TODO move to MapManagerTest?
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

        val finished = d.mapManager.getFinishedBuildingsOfTypeCount<Lumbermill>()
        val unfinished = d.mapManager.getUnfinishedBuildingsOfTypeCount<Lumbermill>()

        assertEquals(2, finished)
        assertEquals(2, unfinished)
    }

    @Test
    fun `queryHouseLuxuryDemand of the specific house types - trivial`() {
        val coord1 = Coordinates(0,0)
        val nr1 = d.mapManager.queryHouseLuxuryDemand(coord1)
        assertEquals(listOf<Resource>(), nr1)
    }

    @Test
    fun `queryHouseLuxuryDemand - house is not finished`() {
        val coord1 = Coordinates(0,0)
        d.gameStateManager.applyStates(listOf(
            GameState(coord1, Operator.Set, Type.Building, HouseLevel1()),
        ))

        val nr1 = d.mapManager.queryHouseLuxuryDemand(coord1)

        assertEquals(listOf<Resource>(), nr1)
    }

    @Test
    fun `queryHouseLuxuryDemand of the specific house types`() {
        val coord1 = Coordinates(0,0)
        val coord2 = Coordinates(2,0)
        val coord3 = Coordinates(4,0)
        d.gameStateManager.applyStates(listOf(
            GameState(coord1, Operator.Set, Type.Building, HouseLevel1()),
            GameState(coord2, Operator.Set, Type.Building, HouseLevel2()),
            GameState(coord3, Operator.Set, Type.Building, HouseLevel3()),
        ))
        d.mapManager.queryBuilding(coord1)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord2)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord3)!!.setConstructionFinished()

        val nr1 = d.mapManager.queryHouseLuxuryDemand(coord1)
        val nr2 = d.mapManager.queryHouseLuxuryDemand(coord2)
        val nr3 = d.mapManager.queryHouseLuxuryDemand(coord3)

        assertEquals(listOf(Fish), nr1)
        assertEquals(listOf(Fish), nr2)
        assertEquals(listOf(Fish), nr3)
    }

    @Test
    fun `count housing luxury demand for all buildings - trivial`() {
        val result = d.mapManager.getCompleteLuxuryDemand()

        assertEquals(listOf<Resource>(), result)
    }

    @Test
    fun `count housing luxury demand for all buildings`() {
        val coord1 = Coordinates(0,0)
        val coord2 = Coordinates(2,0)
        val coord3 = Coordinates(4,0)
        d.gameStateManager.applyStates(listOf(
            GameState(coord1, Operator.Set, Type.Building, HouseLevel1()),
            GameState(coord2, Operator.Set, Type.Building, HouseLevel2()),
            GameState(coord3, Operator.Set, Type.Building, HouseLevel3()),
        ))
        d.mapManager.queryBuilding(coord1)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord2)!!.setConstructionFinished()
        d.mapManager.queryBuilding(coord3)!!.setConstructionFinished()

        val result = d.mapManager.getCompleteLuxuryDemand()

        assertEquals(listOf(Fish, Fish, Fish), result)
    }

    @Test
    fun `getBuildingsWithUnfulfilledHousing - trivial`() {
        val result = d.mapManager.getBuildingsWithUnfulfilledHousing()

        assertEquals(emptyList<Building>(), result)
    }

    @Test
    fun `getBuildingsWithUnfulfilledHousing - 2 set and 2 unset`() {
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
        val building1 = d.mapManager.queryBuilding(coord1)!!
        val building2 = d.mapManager.queryBuilding(coord2)!!
        val building3 = d.mapManager.queryBuilding(coord3)!!
        val building4 = d.mapManager.queryBuilding(coord4)!!

        building1.setConstructionFinished()
        building2.setConstructionFinished()
        building3.setConstructionFinished()
        building4.setConstructionFinished()

        building1.workerLivesAt = coord1
        building2.workerLivesAt = coord2

        val result = d.mapManager.getBuildingsWithUnfulfilledHousing()

        assertEquals(listOf(building3, building4), result)
    }

    @Test
    fun `HousingDemand - getStringForInspectDialog`() {
        val housingDemand = HousingDemand(1,2,3,4)
        assertEquals("""
            lvl1 = 1
            lvl2 = 2
            lvl3 = 3
            lvl4 = 4
        """.trimIndent(), housingDemand.getStringForInspectDialog())
    }
}