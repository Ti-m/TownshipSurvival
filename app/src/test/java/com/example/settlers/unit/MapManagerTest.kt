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
    fun `getCellsWhichShallRunAProduction - one of two is found, because only one has material`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(c1),
            GameStateCreator.createFletcher(c2),
            GameStateCreator.addWoodToProduction(c1),
        ))
        val cell1 = d.mapManager.findSpecificCell(c1)!!
        val cell2 = d.mapManager.findSpecificCell(c2)!!
        cell1.building!!.setConstructionFinished()
        cell2.building!!.setConstructionFinished()
        cell1.building!!.workerLivesAt = Coordinates(0,0) //Anything except null
        cell2.building!!.workerLivesAt = Coordinates(0,0) //Anything except null

        val result = d.mapManager.getCellsWhichShallRunAProduction()

        assertEquals(mapOf(
            Pair(c1, cell1)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAProduction - none, because no worker`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            GameStateCreator.addWoodToProduction(d.coords),
        ))
        val fletcher = d.mapManager.findSpecificCell(d.coords)!!
        fletcher.building!!.setConstructionFinished()

        val result = d.mapManager.getCellsWhichShallRunAProduction()

        assertEquals(emptyMap<Coordinates, Cell>(), result)
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
    fun `getCellsWhichShallRunAProduction - spawner shall produce even without a worker`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createSpawner(d.coords),
        ))
        val spawner = d.mapManager.queryBuilding(d.coords)

        val result = d.mapManager.getCellsWhichShallRunAProduction()

        assertEquals(mapOf(Pair(Coordinates(0,0), Cell(Coordinates(0,0), GroundType.Desert, building = spawner))), result)
    }

    @Test
    fun `getCellsWhichShallRunAProduction - houses produce only if a worker is assigned - no worker`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLvl1House(d.coords),
            GameStateCreator.addFishToProduction(d.coords),
        ))
        val house = d.mapManager.queryHouse(d.coords)!!
        house.setConstructionFinished()

        val result = d.mapManager.getCellsWhichShallRunAProduction()

        assertEquals(emptyMap<Coordinates, Cell>(), result)
    }

    @Test
    fun `getCellsWhichShallRunAProduction - houses produce only if a worker is assigned - worker is assigned`() {
        val coordsHouse = Coordinates(1,1)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLvl1House(coordsHouse),
            GameStateCreator.addFishToProduction(coordsHouse),
        ))
        val house = d.mapManager.queryHouse(coordsHouse)!!
        house.setConstructionFinished()
        house.currentlyAssignedProductionBuildings.add(Coordinates(0,0))//Add pseudo worker

        val result = d.mapManager.getCellsWhichShallRunAProduction()

        assertEquals(1, result.count())
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
    fun `getCellsWhichShallContinueAProduction() - production, because production is already running`() {
        //Init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords)
        ))
        val fletcher = d.mapManager.queryBuilding(d.coords)!!
        fletcher.setConstructionFinished()
        fletcher.workerLivesAt = Coordinates(1,1)//Anything, just not null
        fletcher.productionCount = 50 //set by hand

        //Check
        assertEquals(1, d.mapManager.getCellsWhichShallContinueAProduction().size)
    }

    @Test
    fun `getCellsWhichShallContinueAProduction() - none, because no worker`() {
        //Init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords)
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()
        d.mapManager.queryBuilding(d.coords)!!.productionCount = 50 //set by hand

        //Check
        assertEquals(0, d.mapManager.getCellsWhichShallContinueAProduction().size)
    }

    @Test
    fun `getCellsWhichShallContinueAProduction() - house does not continue production without housing a worker`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLvl1House(d.coords),
        ))
        val house = d.mapManager.queryHouse(d.coords)!!
        house.setConstructionFinished()

        val result = d.mapManager.getCellsWhichShallContinueAProduction()

        assertEquals(0, result.count())
    }

    @Test
    fun `getCellsWhichShallContinueAProduction() - house continues production because it houses a worker`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLvl1House(d.coords),
        ))
        val house = d.mapManager.queryHouse(d.coords)!!
        house.setConstructionFinished()
        house.productionCount = 50
        house.currentlyAssignedProductionBuildings.add(Coordinates(0,0))//Add pseudo worker

        val result = d.mapManager.getCellsWhichShallContinueAProduction()

        assertEquals(1, result.count())
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
    fun `getCellsWhichShallRunAProductionWithProducingOutsideResources() - 0, because no worker`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createForester(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, d.mapManager.getCellsWhichShallRunAProductionWithProducingOutsideResources().size)
    }

    @Test
    fun `getCellsWhichShallRunAProductionWithProducingOutsideResources() - find a forester`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createForester(d.coords))
        val forester = d.mapManager.queryBuilding(d.coords)!!
        forester.setConstructionFinished()
        //some random coordinates to satisfy worker housing requirement
        forester.workerLivesAt = Coordinates(1,1)

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

    @Test
    fun `addHouseAssignments() - trivial test - no production`() {
        //Init
        val coordsHouse = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(coordsHouse))
        val house = d.mapManager.findSpecificCell(coordsHouse)!!
        house.building!!.setConstructionFinished()

        //Do
        val states = d.mapManager.addLevel1HouseAssignmentsWithHouseAsBase(house)

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
        val states = d.mapManager.addLevel1HouseAssignmentsWithHouseAsBase(house)

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
        //Apply the states
        d.gameStateManager.applyStates(states)

        //Do it again, to check if the states are applied in the right way
        val states2 = d.mapManager.addLevel1HouseAssignmentsWithHouseAsBase(house)

        //Check
        assertEquals(listOf<GameState>(), states2)
    }

    @Test
    fun `addHouseAssignments() - add assignment of lvl 2 house - lumbermill in the house and in the lumbermill`() {
        //Init
        val coordsHouse = Coordinates(1,1)
        val coordsLumbermill = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(coordsHouse))
        d.gameStateManager.applyState(GameStateCreator.createLumbermill(coordsLumbermill))
        val house = d.mapManager.findSpecificCell(coordsHouse)!!
        val lumbermill = d.mapManager.findSpecificCell(coordsLumbermill)!!
        house.building!!.setConstructionFinished()
        lumbermill.building!!.setConstructionFinished()

        //Do
        val states = d.mapManager.addLevel2HouseAssignmentsWithHouseAsBase(house)

        //Check
        assertEquals(
            listOf(
                //assign to the production building
                GameState(coordsLumbermill, Operator.Set, Type.ProductionAssignment, Assignment(coordsHouse)),
                //assign to the house
                GameState(coordsHouse, Operator.Set, Type.HouseAssignment, Assignment(coordsLumbermill)),
            ),
            states
        )
        //Apply the states
        d.gameStateManager.applyStates(states)

        //Do it again, to check if the states are applied in the right way
        val states2 = d.mapManager.addLevel2HouseAssignmentsWithHouseAsBase(house)

        //Check
        assertEquals(listOf<GameState>(), states2)
    }

    @Test
    fun `addHouseAssignments() - Add more production buildings, then slots are available`() {
        //Init
        val coordsHouse = Coordinates(0,0)
        val prod1 = Coordinates(2,0)
        val prod2 = Coordinates(4,0)
        val prod3 = Coordinates(6,0)
        val prod4 = Coordinates(1,1)
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(coordsHouse))
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(prod1))
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(prod2))
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(prod3))
        d.gameStateManager.applyState(GameStateCreator.createLumberjack(prod4))
        val house = d.mapManager.findSpecificCell(coordsHouse)!!
        val houseBuilding = d.mapManager.queryHouse(coordsHouse)!!
        val lumber1 = d.mapManager.findSpecificCell(prod1)!!
        val lumber2 = d.mapManager.findSpecificCell(prod2)!!
        val lumber3 = d.mapManager.findSpecificCell(prod3)!!
        val lumber4 = d.mapManager.findSpecificCell(prod4)!!
        houseBuilding.setConstructionFinished()
        lumber1.building!!.setConstructionFinished()
        lumber2.building!!.setConstructionFinished()
        lumber3.building!!.setConstructionFinished()
        lumber4.building!!.setConstructionFinished()

        //Do
        val states = d.mapManager.addLevel1HouseAssignmentsWithHouseAsBase(house)
        //Check
        assertEquals(
            listOf(
                GameState(coordinates=Coordinates(x=2, y=0), operator=Operator.Set, type=Type.ProductionAssignment, data=Assignment(coordinates=Coordinates(x=0, y=0))),
                GameState(coordinates=Coordinates(x=0, y=0), operator=Operator.Set, type=Type.HouseAssignment, data=Assignment(coordinates=Coordinates(x=2, y=0))),
                GameState(coordinates=Coordinates(x=4, y=0), operator=Operator.Set, type=Type.ProductionAssignment, data=Assignment(coordinates=Coordinates(x=0, y=0))),
                GameState(coordinates=Coordinates(x=0, y=0), operator=Operator.Set, type=Type.HouseAssignment, data=Assignment(coordinates=Coordinates(x=4, y=0)))
            ),
            states)

        assertEquals(HousingDemand(lvl1 = 2, lvl2 = 1, lvl3 = 0, lvl4 = 0), houseBuilding.currentHousingAvailable)
        //Apply the states
        d.gameStateManager.applyStates(states)
        //The available housing should be reduced, when applying the states
        assertEquals(HousingDemand(lvl1 = 0, lvl2 = 1, lvl3 = 0, lvl4 = 0), houseBuilding.currentHousingAvailable)

        //Do it again - to check that the house is full
        //All production buildings should be assigned now
        val states2 = d.mapManager.addLevel1HouseAssignmentsWithHouseAsBase(house)

        //Check
        assertEquals(listOf<GameState>(), states2)
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
        val newStates = d.mapManager.removeHouseAssignmentsWithHouseAsBase(house)

        //Check
        assertEquals(listOf<GameState>(), newStates)
    }

    @Test
    fun `removeHouseAssignments() - nothing to remove, only a house, no production building`() {
        //Init
        val coordsHouse = Coordinates(0,2)
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(coordsHouse))
        val house = d.mapManager.findSpecificCell(coordsHouse)!!
        house.building!!.setConstructionFinished()

        //Do - remove them again
        val newStates = d.mapManager.removeHouseAssignmentsWithHouseAsBase(house)

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
        val houseCell = d.mapManager.findSpecificCell(coordsHouse)!!
        val lumberjack = d.mapManager.queryBuilding(coordsLumberjack)!!
        val house = houseCell.building as House
        house.setConstructionFinished()
        lumberjack.setConstructionFinished()

        //Check available housing - before assigning a worker
        assertEquals(HousingDemand(2,1,0,0), house.currentHousingAvailable)

        //setup housing
        val states = d.mapManager.addLevel1HouseAssignmentsWithHouseAsBase(houseCell)
        d.gameStateManager.applyStates(states)

        //Check available housing - after assigning a worker
        assertEquals(HousingDemand(1,1,0,0), house.currentHousingAvailable)

        //Do - remove them again
        val newStates = d.mapManager.removeHouseAssignmentsWithHouseAsBase(houseCell)

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
        d.gameStateManager.applyStates(newStates)

        //Check available housing - after removing a worker
        assertEquals(HousingDemand(2,1,0,0), house.currentHousingAvailable)
    }

    @Test
    fun `getCellsWithHousesAndARunningProductionAndEmptySpacesAvailable() - zero are found - trivial`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        //Check
        assertEquals(0, d.mapManager.getCellsWithHousesAndARunningProductionAndEmptySpacesAvailable().size)
    }

    @Test
    fun `getCellsWithHousesAndARunningProductionAndEmptySpacesAvailable() - one is found`() {
        //Init
        d.gameStateManager.applyState(GameStateCreator.createLvl1House(d.coords))
        val house = d.mapManager.queryBuilding(d.coords)!!
        house.setConstructionFinished()
        house.productionCount = 66 //Set in the middle of production

        //Check
        assertEquals(1, d.mapManager.getCellsWithHousesAndARunningProductionAndEmptySpacesAvailable().size)
    }

}