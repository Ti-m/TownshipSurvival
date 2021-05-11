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
    fun `getCellsWhichRequireStuffWhichIsNotInStorage - regular case`() {
        val dest = Coordinates(2,2)
        val identicalLumberjack = Lumberjack()
        d.gameStateManager.applyStates(listOf(
            GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, identicalLumberjack)
        ))

        val actual = d.mapManager.getCellsWhichRequireStuffWhichIsNotInStorage()
        assertEquals(
            mapOf(Pair(dest, Cell(dest, GroundType.Desert, building = identicalLumberjack, requires = mutableListOf(Lumber, Lumber)))),
            actual
        )
    }

    @Test
    fun `getCellsWhichRequireStuffWhichIsNotInStorage - delivery is stopped`() {
        val dest = Coordinates(2,2)
        val identicalLumberjack = Lumberjack()
        d.gameStateManager.applyStates(listOf(
            GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, identicalLumberjack)
        ))
        d.mapManager.queryBuilding(dest)!!.stopDelivery = true

        val actual = d.mapManager.getCellsWhichRequireStuffWhichIsNotInStorage()
        assertEquals(
            mapOf<Coordinates, Cell>(),//mapOf(Pair(dest, Cell(dest, GroundType.Desert, building = identicalLumberjack, requires = mutableListOf(Wood, Wood)))),
            actual
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
    fun `getCellsWhichShallRunAProduction - production is blocked`() {
        val c1 = Coordinates(1,1)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(c1),
            GameStateCreator.addWoodToProduction(c1),
        ))
        val cell1 = d.mapManager.findSpecificCell(c1)!!
        cell1.building!!.setConstructionFinished()
        cell1.building!!.isProductionBlocked = true

        val result = d.mapManager.getCellsWhichShallRunAProduction()

        assertEquals(emptyMap<Coordinates, Cell>(), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction nothing finished`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        d.gameStateManager.applyStates(
            listOf(
                GameStateCreator.createLumberjack(c1),
                GameStateCreator.addLumberToProduction(c1),
                GameStateCreator.addLumberToProduction(c1),
                GameStateCreator.createLumberjack(c2),
                GameStateCreator.addLumberToProduction(c2),
                GameStateCreator.addLumberToProduction(c2),
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
                GameStateCreator.createLumberjack(c1),
                GameStateCreator.addLumberToProduction(c1),
                GameStateCreator.addLumberToProduction(c1),
                GameStateCreator.createLumberjack(c2),
                GameStateCreator.addLumberToProduction(c2),//One is missing
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
                GameStateCreator.createLumberjack(c1),
                GameStateCreator.addLumberToProduction(c1),
                GameStateCreator.addLumberToProduction(c1),
                GameStateCreator.createLumberjack(c2),
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

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - trivial `() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            //Finished manually, so clear required.
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check
        assertEquals(1, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - Tower - is not a production building, but requests arrows`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(d.coords),
            //Finished manually, so clear required.
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check - will request arrows
        assertEquals(1, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - Tower - requests arrows as soon as one is missing`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(d.coords),
            //Finished manually, so clear required.
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            //One arrow is already inside
            GameStateCreator.addArrowToProduction(d.coords),
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check - will request arrows
        assertEquals(1, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
    }

    @Test
    fun `getCellsWhichNeedToUpdateProductionRequirements() - Tower - don't requests more arrows - it's full`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(d.coords),
            //Finished manually, so clear required.
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            //One arrow is already inside
            GameStateCreator.addArrowToProduction(d.coords),
            GameStateCreator.addArrowToProduction(d.coords),
            GameStateCreator.addArrowToProduction(d.coords),
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //check - will request arrows
        assertEquals(0, d.mapManager.getCellsWhichNeedToUpdateProductionRequirements().size)
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

    //TODO TMP
    @Test
    fun `getCellsWithHouses() - one is found`() {
        //Init
//        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
//val building = HouseLevel1()
//        //Check
//        assertEquals(
//            mapOf(Pair(d.coords, Cell(coordinates = d.coords, type = GroundType.Desert, building = building))),
//            d.mapManager.filterHouses(mapOf(Pair(d.coords, Cell(coordinates = d.coords, type = GroundType.Desert, building = building))), type =  HouseLevel1())
//        ) Hier muss ich ein konkretes Haus reinstecken. z.b. HouseLevel1. Ich kann nicht ein abstraktes House wie "House" benutzen...
    }
//    Bedeutet das, dass die generische Funktion so nicht funktioniert?
//    * Also nochmal in die Doku schauen https://kotlinlang.org/docs/generics.html#declaration-site-variance
//    * Dann möglicherweise eine nicht generische Variante bauen.


    @Test
    fun `getCellsWithFinishedHouses() - one is found`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(1, d.mapManager.getCellsWithFinishedHouses().count())
    }

    @Test
    fun `getCellsWithHousesWithoutARunningProductionAndNoMaterialsAvailable() - one is found`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(1, d.mapManager.getCellsWithHousesWithoutARunningProductionAndNoMaterialsAvailable().size)
    }

    @Test
    fun `getCellsWithHousesWithoutARunningProductionAndNoMaterialsAvailable() - zero are found, because the single house has products available`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()
        d.gameStateManager.applyState(GameStateCreator.addFishToProduction(d.coords))

        //Check
        assertEquals(0, d.mapManager.getCellsWithHousesWithoutARunningProductionAndNoMaterialsAvailable().size)
    }

    @Test
    fun `getCellsWithHousesWithoutARunningProductionAndMaterialsAvailable() - one is found`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()
        d.gameStateManager.applyState(GameStateCreator.addFishToProduction(d.coords))

        //Check
        assertEquals(1, d.mapManager.getCellsWithHousesWithoutARunningProductionAndMaterialsAvailable().size)
    }

    @Test
    fun `getCellsWithHousesWithoutARunningProductionAndMaterialsAvailable() - zero are found, because the single house has no products available`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, d.mapManager.getCellsWithHousesWithoutARunningProductionAndMaterialsAvailable().size)
    }

    //TODO
    @Test
    fun `addHouseAssignments() - trivial test - no house`() {
        //Init
        val coordsLumberjack = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(coordsLumberjack))
        val lumberjack = d.mapManager.findSpecificCell(coordsLumberjack)!!
        lumberjack.building!!.setConstructionFinished()

        //Do
        val states = d.mapManager.addHouseAssignments(lumberjack)

        //Check
        assertEquals(listOf<GameState>(), states)
    }

    @Test
    fun `addHouseAssignments() - add assignment of house in the house and in the Lumberjack`() {
        //Init
        val coordsHouse = Coordinates(1,1)
        val coordsLumberjack = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(coordsHouse))
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(coordsLumberjack))
        val house = d.mapManager.findSpecificCell(coordsHouse)!!
        val lumberjack = d.mapManager.findSpecificCell(coordsLumberjack)!!
        house.building!!.setConstructionFinished()
        lumberjack.building!!.setConstructionFinished()

        //Do
        val states = d.mapManager.addHouseAssignments(lumberjack)

        //Check
        assertEquals(
            listOf(
                //assign to the production building
                GameState(coordsLumberjack, Operator.Set, Type.ProductionAssignment, Assignment(coordsHouse)),
                //assign to the house
                GameState(coordsHouse, Operator.Set, Type.HouseAssignment, Assignment(coordsLumberjack)),
            ),
            states
        )
    }

    @Test
    fun `removeHouseAssignments() - nothing to remove, no workers set`() {
        //Init
        val coordsHouse = Coordinates(1,1)
        val coordsLumberjack = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(coordsHouse))
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(coordsLumberjack))
        val house = d.mapManager.findSpecificCell(coordsHouse)!!
        val lumberjack = d.mapManager.findSpecificCell(coordsLumberjack)!!
        house.building!!.setConstructionFinished()
        lumberjack.building!!.setConstructionFinished()

        //Do - remove them again
        val newStates = d.mapManager.removeHouseAssignments(lumberjack)

        //Check
        assertEquals(listOf<GameState>(), newStates)
    }

    @Test
    fun `removeHouseAssignments() - nothing to remove, not even a house set`() {
        //Init
        val coordsLumberjack = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(coordsLumberjack))
        val lumberjack = d.mapManager.findSpecificCell(coordsLumberjack)!!
        lumberjack.building!!.setConstructionFinished()

        //Do - remove them again
        val newStates = d.mapManager.removeHouseAssignments(lumberjack)

        //Check
        assertEquals(listOf<GameState>(), newStates)
    }

    @Test
    fun `removeHouseAssignments() - remove assignment of house in the house and in the Lumberjack`() {
        //Init
        val coordsHouse = Coordinates(1,1)
        val coordsLumberjack = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(coordsHouse))
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(coordsLumberjack))
        val house = d.mapManager.findSpecificCell(coordsHouse)!!
        val lumberjack = d.mapManager.findSpecificCell(coordsLumberjack)!!
        house.building!!.setConstructionFinished()
        lumberjack.building!!.setConstructionFinished()
        //setup housing
        val states = d.mapManager.addHouseAssignments(lumberjack)
        d.gameStateManager.applyStates(states)

        //Do - remove them again
        val newStates = d.mapManager.removeHouseAssignments(lumberjack)

        //Check
        assertEquals(
            listOf(
                //assign to the production building
                GameState(coordsLumberjack, Operator.Remove, Type.ProductionAssignment, Assignment(coordsHouse)),
                //assign to the house
                GameState(coordsHouse, Operator.Remove, Type.HouseAssignment, Assignment(coordsLumberjack)),
            ),
            newStates
        )
    }
}