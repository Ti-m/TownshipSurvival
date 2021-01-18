package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapManagerTest {

    private lateinit var sut: MapManagerPreparedForTest
    private lateinit var gameStateManager: GameStateManager
    private lateinit var transportManager: TransportManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        sut = MapManagerPreparedForTest()
        transportManager = TransportManagerPreparedForTest(sut)
        gameStateManager = GameStateManagerPreparedForTest(transportManager, sut)
        coords = Coordinates(0,0)
    }

    @Test
    fun queryResourcesOffered() {
        gameStateManager.applyStates(listOf(GameState(coords, Operator.Set, Type.Storage, Wood)))
        val result = sut.queryInStorage(coords)

        assertEquals(listOf(Wood), result)
    }

    @Test
    fun getCellsWhichRequireStuff() {
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
            )
        )
        assertEquals(
            mapOf(Pair(coords, Cell(coords, GroundType.Desert, requires = mutableListOf(Wood)))),
            sut.getCellsWhichRequireStuff()
        )
    }

    @Test
    fun getCellsWithBuildings() {
        val c2 = Coordinates(1,1)
        val c3 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Building, Townhall()),
                GameState(c3, Operator.Set, Type.Building, Road()),
            )
        )
        val cell1 = sut.findSpecificCell(coords)!!
        val cell2 = sut.findSpecificCell(c2)!!
        val cell3 = sut.findSpecificCell(c3)!!

        val result = sut.getCellsWithBuildings()

        assertEquals(mapOf(
            Pair(coords, cell1),
            Pair(c2, cell2),
            Pair(c3, cell3)
        ), result)
    }

    @Test
    fun getCellsWhichShallRunAProduction() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(c1),
            GameStateCreator.createFletcher(c2),
            GameStateCreator.removeWoodFromRequired(c1),
            GameStateCreator.removeWoodFromRequired(c1),
            GameStateCreator.removeWoodFromRequired(c2),
            GameStateCreator.removeWoodFromRequired(c2),
            GameStateCreator.addWoodToProduction(c1),
        ))
        val cell1 = sut.findSpecificCell(c1)!!
        cell1.building!!.setConstructionFinished()

        val result = sut.getCellsWhichShallRunAProduction()

        assertEquals(mapOf(
            Pair(c1, cell1)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction nothing finished`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Production, Wood)
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!
        val cell2 = sut.findSpecificCell(c2)!!

        val result = sut.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1),
            Pair(c2, cell2)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction Test Building Material availability`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Production, Wood)//One is missing
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!

        val result = sut.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction one already finished`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!
        val cell2 = sut.findSpecificCell(c2)!!
        cell2.building!!.setConstructionFinished()

        val result = sut.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1),
            //Pair(c2, cell2) //c2 is done
        ), result)
    }

    @Test
    fun `getCellsWithMovingObjects two cells`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameStateCreator.createZombie(c1),
                GameStateCreator.createZombie(c2)
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!
        val cell2 = sut.findSpecificCell(c2)!!

        val result = sut.getCellsWithMovingObjects()

        assertEquals(mapOf(
            Pair(c1, cell1),
            Pair(c2, cell2)
        ), result)
    }



    @Test
    fun `getSouthEastEdge one already finished`() {
        val coords: Coordinates = sut.getSouthEastEdge()
        assertEquals(Coordinates(7,3), coords)
    }

    //This is redundant? See GameStateManagerTest.refreshProductionRequirements
    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - trivial `() {
        //init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(coords),
            //Finished manually, so clear required.
            GameState(coords, Operator.Remove, Type.Required, Wood),
            GameState(coords, Operator.Remove, Type.Required, Wood)
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()

        //check
        assertEquals(1, sut.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - There is stuff in required, therefore, dont request `() {
        //init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(coords)
            //The initial requirements are not removed manually
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()

        //check
        assertEquals(0, sut.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - There is stuff in production, therefore, dont request `() {
        //init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(coords),
            //Finished manually, so clear required.
            GameState(coords, Operator.Remove, Type.Required, Wood),
            GameState(coords, Operator.Remove, Type.Required, Wood),
            //Add to production manually
            GameStateCreator.addWoodToProduction(coords)
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()

        //check
        assertEquals(0, sut.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - Tower - is not a production building, but requests arrows`() {
        //init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(coords),
            //Finished manually, so clear required.
            GameState(coords, Operator.Remove, Type.Required, Wood),
            GameState(coords, Operator.Remove, Type.Required, Stone),
            GameState(coords, Operator.Remove, Type.Required, Stone),
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()

        //check - will request arrows
        assertEquals(1, sut.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichShallRunAProduction() - production, because all materials are available`() {
        //Init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(coords),
            GameStateCreator.addWoodToProduction(coords)
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()

        //Check
        assertEquals(1, sut.getCellsWhichShallRunAProduction().size)
    }

    @Test
    fun `getCellsWhichShallRunAProduction() - production, because production is already running`() {
        //Init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(coords)
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()
        sut.queryBuilding(coords)!!.productionCount = 50 //set by hand

        //Check
        assertEquals(1, sut.getCellsWhichShallRunAProduction().size)
    }

    @Test
    fun `getCellsWhichShallRunAProduction() - don't run production if already 3 items are in storage`() {
        //Init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(coords),
            GameStateCreator.addWoodToProduction(coords),//Material is available
            GameStateCreator.addArrowToStorage(coords),//Already 3 are produced
            GameStateCreator.addArrowToStorage(coords),//Already 3 are produced
            GameStateCreator.addArrowToStorage(coords),//Already 3 are produced
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, sut.getCellsWhichShallRunAProduction().size)
    }

    @Test
    fun `getCellsWhichShallRunAProductionWithConsumingOutsideResources() - don't run production if already 3 items are in storage`() {
        //Init
        gameStateManager.applyStates(listOf(
            GameStateCreator.createLumberjack(coords),
            GameStateCreator.addWoodToStorage(coords),//Already 3 are produced
            GameStateCreator.addWoodToStorage(coords),//Already 3 are produced
            GameStateCreator.addWoodToStorage(coords),//Already 3 are produced
        ))
        sut.queryBuilding(coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, sut.getCellsWhichShallRunAProductionWithConsumingOutsideResources().size)
    }

}