package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapManagerTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun queryResourcesOffered() {
        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Set, Type.Storage, Wood)))
        val result = d.mapManager.queryInStorage(d.coords)

        assertEquals(listOf(Wood), result)
    }

    @Test
    fun getCellsWhichRequireStuff() {
        d.gameStateManager.applyStates(
            listOf(
                GameState(d.coords, Operator.Set, Type.Required, Wood),
            )
        )
        assertEquals(
            mapOf(Pair(d.coords, Cell(d.coords, GroundType.Desert, requires = mutableListOf(Wood)))),
            d.mapManager.getCellsWhichRequireStuff()
        )
    }

    @Test
    fun getCellsWithBuildings() {
        val c2 = Coordinates(1,1)
        val c3 = Coordinates(0,2)
        d.gameStateManager.applyStates(
            listOf(
                GameState(d.coords, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Building, Townhall()),
                GameState(c3, Operator.Set, Type.Building, Road()),
            )
        )
        val cell1 = d.mapManager.findSpecificCell(d.coords)!!
        val cell2 = d.mapManager.findSpecificCell(c2)!!
        val cell3 = d.mapManager.findSpecificCell(c3)!!

        val result = d.mapManager.getCellsWithBuildings()

        assertEquals(mapOf(
            Pair(d.coords, cell1),
            Pair(c2, cell2),
            Pair(c3, cell3)
        ), result)
    }

    @Test
    fun getCellsWhichShallRunAProduction() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(c1),
            GameStateCreator.createFletcher(c2),
            GameStateCreator.removeWoodFromRequired(c1),
            GameStateCreator.removeWoodFromRequired(c1),
            GameStateCreator.removeWoodFromRequired(c2),
            GameStateCreator.removeWoodFromRequired(c2),
            GameStateCreator.addWoodToProduction(c1),
        ))
        val cell1 = d.mapManager.findSpecificCell(c1)!!
        cell1.building!!.setConstructionFinished()

        val result = d.mapManager.getCellsWhichShallRunAProduction()

        assertEquals(mapOf(
            Pair(c1, cell1)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction nothing finished`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        d.gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Production, Wood)
            )
        )
        val cell1 = d.mapManager.findSpecificCell(c1)!!
        val cell2 = d.mapManager.findSpecificCell(c2)!!

        val result = d.mapManager.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1),
            Pair(c2, cell2)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction Test Building Material availability`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        d.gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Production, Wood)//One is missing
            )
        )
        val cell1 = d.mapManager.findSpecificCell(c1)!!

        val result = d.mapManager.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction one already finished`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        d.gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
            )
        )
        val cell1 = d.mapManager.findSpecificCell(c1)!!
        val cell2 = d.mapManager.findSpecificCell(c2)!!
        cell2.building!!.setConstructionFinished()

        val result = d.mapManager.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1),
            //Pair(c2, cell2) //c2 is done
        ), result)
    }

    @Test
    fun `getCellsWithMovingObjects two cells`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        d.gameStateManager.applyStates(
            listOf(
                GameStateCreator.createZombie(c1),
                GameStateCreator.createZombie(c2)
            )
        )
        val cell1 = d.mapManager.findSpecificCell(c1)!!
        val cell2 = d.mapManager.findSpecificCell(c2)!!

        val result = d.mapManager.getCellsWithMovingObjects()

        assertEquals(mapOf(
            Pair(c1, cell1),
            Pair(c2, cell2)
        ), result)
    }



    @Test
    fun `getSouthEastEdge one already finished`() {
        val coords: Coordinates = d.mapManager.getSouthEastEdge()
        assertEquals(Coordinates(7,3), coords)
    }

    //This is redundant? See GameStateManagerTest.refreshProductionRequirements
    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - trivial `() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            //Finished manually, so clear required.
            GameState(d.coords, Operator.Remove, Type.Required, Wood),
            GameState(d.coords, Operator.Remove, Type.Required, Wood)
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check
        assertEquals(1, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - There is stuff in required, therefore, dont request `() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords)
            //The initial requirements are not removed manually
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check
        assertEquals(0, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - There is stuff in production, therefore, dont request `() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            //Finished manually, so clear required.
            GameState(d.coords, Operator.Remove, Type.Required, Wood),
            GameState(d.coords, Operator.Remove, Type.Required, Wood),
            //Add to production manually
            GameStateCreator.addWoodToProduction(d.coords)
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check
        assertEquals(0, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - Tower - is not a production building, but requests arrows`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(d.coords),
            //Finished manually, so clear required.
            GameState(d.coords, Operator.Remove, Type.Required, Wood),
            GameState(d.coords, Operator.Remove, Type.Required, Stone),
            GameState(d.coords, Operator.Remove, Type.Required, Stone),
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check - will request arrows
        assertEquals(1, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichShallRunAProduction() - production, because all materials are available`() {
        //Init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            GameStateCreator.addWoodToProduction(d.coords)
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(1, d.mapManager.getCellsWhichShallRunAProduction().size)
    }

    @Test
    fun `getCellsWhichShallContinueAProduction() - production, because production is already running`() {
        //Init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords)
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()
        d.mapManager.queryBuilding(d.coords)!!.productionCount = 50 //set by hand

        //Check
        assertEquals(1, d.mapManager.getCellsWhichShallContinueAProduction().size)
    }

    @Test
    fun `getCellsWhichShallRunAProduction() - don't run production if already 3 items are in storage`() {
        //Init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            GameStateCreator.addWoodToProduction(d.coords),//Material is available
            GameStateCreator.addArrowToStorage(d.coords),//Already 3 are produced
            GameStateCreator.addArrowToStorage(d.coords),//Already 3 are produced
            GameStateCreator.addArrowToStorage(d.coords),//Already 3 are produced
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, d.mapManager.getCellsWhichShallRunAProduction().size)
    }

    @Test
    fun `getCellsWhichShallRunAProductionWithConsumingOutsideResources() - don't run production if already 3 items are in storage`() {
        //Init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLumberjack(d.coords),
            GameStateCreator.addWoodToStorage(d.coords),//Already 3 are produced
            GameStateCreator.addWoodToStorage(d.coords),//Already 3 are produced
            GameStateCreator.addWoodToStorage(d.coords),//Already 3 are produced
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, d.mapManager.getCellsWhichShallRunAProductionWithConsumingOutsideResources().size)
    }

    @Test
    fun `getCellsWhichShallRunAProductionWithProducingOutsideResources() - find a forester`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createForester(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(1, d.mapManager.getCellsWhichShallRunAProductionWithProducingOutsideResources().size)
    }

    @Test
    fun `getCellsWhichShallRunAProductionWithProducingOutsideResources() - find a lumberjack - so no building found`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, d.mapManager.getCellsWhichShallRunAProductionWithProducingOutsideResources().size)
    }

}