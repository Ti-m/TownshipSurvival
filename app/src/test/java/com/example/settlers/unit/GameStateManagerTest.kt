package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameStateManagerTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun applyStates_SetRemoveResourceOffered() {
        //Set
        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Set, Type.Storage, Wood)))
        assertEquals(listOf(Wood), d.mapManager.queryInStorage(d.coords))
        //Remove
        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Remove, Type.Storage, Wood)))
        assertEquals(listOf<Resource>(), d.mapManager.queryInStorage(d.coords))
    }

    @Test
    fun applyStates_SetRemoveResource() {
        //Set
        d.gameStateManager.applyStates(
            listOf(
                GameState(d.coords, Operator.Set, Type.Transport, Wood),
                GameState(d.coords, Operator.Set, Type.Transport, Wood)
            )
        )
        assertEquals(listOf(Wood, Wood), d.mapManager.queryInTransport(d.coords))
        //Remove
        d.gameStateManager.applyStates(
            listOf(
                GameState(d.coords, Operator.Remove, Type.Transport, Wood),
                GameState(d.coords, Operator.Remove, Type.Transport, Wood)
            )
        )
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(d.coords))
    }

    @Test
    fun applyStates_SetTownhall() {
        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Set, Type.Building, Townhall())))
        assertTrue(d.mapManager.queryBuilding(d.coords) is Townhall)

        assertEquals(
            listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone, Fish, Fish),
            d.mapManager.queryInStorage(d.coords)
        )
    }

    @Test
    fun applyStates_SetLumberjack() {
        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Set, Type.Building, Lumberjack())))
        assertTrue(d.mapManager.queryBuilding(d.coords) is Lumberjack)
    }

    @Test
    fun applyStates_SetRoad() {
        d.gameStateManager.applyStates(listOf(GameState(d.coords, Operator.Set, Type.Building, Road())))
        assertTrue(d.mapManager.queryBuilding(d.coords) is Road)
    }

    @Test
    fun `Test Zombie movement - Find targets and move there`() {
        val destination = Coordinates(0,0)
        val wrongTarget = Coordinates(7,3)
        val spawner = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(wrongTarget, Operator.Set, Type.Building, Tower()),
            GameState(destination, Operator.Set, Type.Building, Townhall()),
            GameState(spawner, Operator.Set, Type.MovingObject, Zombie)
        ))
        d.mapManager.resetTouched()
        d.gameStateManager.tick()

        assertEquals(Zombie, d.mapManager.findSpecificCell(Coordinates(1, 1))!!.movingObject)
        assertEquals(null, d.mapManager.findSpecificCell(Coordinates(2, 2))!!.movingObject)

        //another step
        d.mapManager.resetTouched()
        d.gameStateManager.tick()

        assertEquals(Zombie, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.movingObject)
        assertEquals(null, d.mapManager.findSpecificCell(Coordinates(1, 1))!!.movingObject)
        assertEquals(null, d.mapManager.findSpecificCell(Coordinates(2, 2))!!.movingObject)
    }

    @Test
    fun `Removing a building removes requested items`() {
        val cell = d.mapManager.findSpecificCell(Coordinates(0, 0))!!
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Transport, Wood))
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Production, Wood))
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Storage, Wood))
        assertEquals(2, cell.requires.count())
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Remove, Type.Building, null))
        assertEquals(0, cell.production.count())
        assertEquals(0, cell.storage.count())
        assertEquals(0, cell.transport.count())
        assertNull(cell.building)
    }

    @Test
    fun `Replacing a building updates requested items`() {
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        assertEquals(2, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(0, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        assertEquals(0, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(11, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
    }

    @Test
    fun `Replacing a building does not delete the storage`() {
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        assertEquals(0, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(11, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        assertEquals(2, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(11, d.mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
    }

    @Test
    fun `A Building gets destroyed, if a zombie steps into its cell`() {
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.MovingObject, Zombie))
        d.gameStateManager.tick()
        assertNull(d.mapManager.findSpecificCell(Coordinates(0, 0))!!.building)
    }

    @Test
    fun `A Building gets destroyed, if a zombie steps into its cell, but ignore roads`() {
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Road()))
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.MovingObject, Zombie))
        d.gameStateManager.tick()
        assertTrue(d.mapManager.findSpecificCell(Coordinates(0, 0))!!.building is Road)
    }

    @Test
    fun `A Building gets destroyed, if a zombie steps into its cell, but ignore spawners`() {
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Spawner()))
        d.gameStateManager.applyState(GameState(Coordinates(0,0), Operator.Set, Type.MovingObject, Zombie))
        d.gameStateManager.tick()
        assertTrue(d.mapManager.findSpecificCell(Coordinates(0, 0))!!.building is Spawner)
    }

    @Test
    fun `Set explosion animation and check the progress`() {
        d.gameStateManager.applyState(GameState(d.coords, Operator.Set, Type.Animation, ExplosionAnimation()))
        assertTrue(d.mapManager.queryAnimation(d.coords)!! is ExplosionAnimation)
        assertTrue(d.mapManager.queryAnimation(d.coords)!!.parts.first() is ExplosionAnimationOne)
        d.gameStateManager.tick()
        assertTrue(d.mapManager.queryAnimation(d.coords)!!.parts.first() is ExplosionAnimationTwo)
        d.gameStateManager.tick()
        assertTrue(d.mapManager.queryAnimation(d.coords)!!.parts.first() is ExplosionAnimationThree)
        d.gameStateManager.tick()
        assertNull(d.mapManager.queryAnimation(d.coords)) //Remove animation, when the cycle is over
    }

    @Test
    fun `engageTarget with ammu`() {
        val tower = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        d.gameStateManager.applyStates(listOf(
            GameState(tower, Operator.Set, Type.Building, Tower()),
            GameState(tower, Operator.Set, Type.Production, Arrow),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        //Finish construction of the tower to allow shooting
        d.mapManager.getCellsWithTowers().values.first().building!!.setConstructionFinished()
        assertTrue(d.mapManager.isMovingObject(zombie))//not destroyed
        d.gameStateManager.tick()
        assertFalse(d.mapManager.isMovingObject(zombie))//destroyed
        assertFalse(d.mapManager.isMovingObject(Coordinates(2,0)))//check if the zombie just moved away
    }

    @Test
    fun `engageTarget with ammu and range check`() {
        val tower = Coordinates(0,0)
        val zombie = Coordinates(7,3)// 5 away
        d.gameStateManager.applyStates(listOf(
            GameState(tower, Operator.Set, Type.Building, Tower()),
            GameState(tower, Operator.Set, Type.Production, Arrow),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        //Finish construction of the tower to allow shooting
        d.mapManager.getCellsWithTowers().values.first().building!!.setConstructionFinished()
        assertTrue(d.mapManager.isMovingObject(zombie))//not destroyed
        d.gameStateManager.tick()
        //not destroyed - out of range, but moved closer
        assertFalse(d.mapManager.isMovingObject(zombie))
        assertTrue(d.mapManager.isMovingObject(Coordinates(5,3)))//moved closer
        //again
        d.gameStateManager.tick()
        //not destroyed - out of range, but moved closer
        assertFalse(d.mapManager.isMovingObject(Coordinates(5,3)))
        assertTrue(d.mapManager.isMovingObject(Coordinates(3,3)))//moved closer
        d.gameStateManager.tick()
        //destroyed
        assertFalse(d.mapManager.isMovingObject(Coordinates(3,3)))
        assertFalse(d.mapManager.isMovingObject(Coordinates(2,2)))//not moved closer
    }

    @Test
    fun `engageTarget no ammu`() {
        val tower = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        d.gameStateManager.applyStates(listOf(
            GameState(tower, Operator.Set, Type.Building, Tower()),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie),
        ))
        //Finish construction of the tower to allow shooting
        d.mapManager.getCellsWithTowers().values.first().building!!.setConstructionFinished()
        assertTrue(d.mapManager.isMovingObject(zombie))//not destroyed
        d.gameStateManager.tick()
        //Check all cells to recognize if the zombie is duplicated
        assertFalse(d.mapManager.isMovingObject(Coordinates(0,0)))
        assertTrue(d.mapManager.isMovingObject(Coordinates(2,0)))//still not destroyed, but the zombie moved to here
        assertFalse(d.mapManager.isMovingObject(Coordinates(4,0)))
        assertFalse(d.mapManager.isMovingObject(Coordinates(1,1)))
        assertFalse(d.mapManager.isMovingObject(Coordinates(3,1)))
        assertFalse(d.mapManager.isMovingObject(Coordinates(5,1)))
        assertFalse(d.mapManager.isMovingObject(Coordinates(0,2)))
        assertFalse(d.mapManager.isMovingObject(Coordinates(2,2)))
        assertFalse(d.mapManager.isMovingObject(Coordinates(4,2)))
    }

    @Test
    fun convertStorageToProduction() {
        d.gameStateManager.applyStates(
            listOf(
                GameState(d.coords, Operator.Set, Type.Required, Wood),
                GameState(d.coords, Operator.Set, Type.Required, Wood),
                GameState(d.coords, Operator.Set, Type.Storage, Wood),
            )
        )

        d.gameStateManager.tick()

        assertEquals(listOf(Wood), d.mapManager.queryInProduction(d.coords))
    }

    @Test
    fun convertTransportToStorage() {
        d.gameStateManager.applyStates(
            listOf(
                GameState(d.coords, Operator.Set, Type.Required, Wood),
                GameState(d.coords, Operator.Set, Type.Required, Wood),
                GameState(d.coords, Operator.Set, Type.Transport, Wood),
            )
        )

        d.gameStateManager.tick()

        assertEquals(listOf(Wood), d.mapManager.queryInStorage(d.coords))
    }

    @Test
    fun `refreshProductionRequirements - nothing in production already`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(d.coords),
            //Finished manually, so clear required.
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        assertEquals(listOf<Resource>(), d.mapManager.queryRequires(d.coords))
        //exercise
        d.gameStateManager.tick()

        //check
        assertEquals(listOf(Arrow, Arrow, Arrow), d.mapManager.queryRequires(d.coords))
    }

    @Test
    fun `refreshProductionRequirements - one item is already in production - two to go`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(d.coords),
            //Finished manually, so clear required.
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            //One arrow is already there
            GameStateCreator.addArrowToProduction(d.coords),
        ))
        d.mapManager.queryBuilding(d.coords)!!.setConstructionFinished()

        assertEquals(listOf<Resource>(), d.mapManager.queryRequires(d.coords))
        assertEquals(listOf(Arrow), d.mapManager.queryInProduction(d.coords))
        //exercise
        d.gameStateManager.tick()

        //check
        assertEquals(listOf(Arrow, Arrow), d.mapManager.queryRequires(d.coords))
    }

    @Test
    fun `runProduction until an item is put into storage`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeStoneFromRequired(d.coords),
            GameStateCreator.addWoodToProduction(d.coords),
        ))
        val fletcher = d.mapManager.queryBuilding(d.coords)!!
        fletcher.setConstructionFinished()
        fletcher.workerLivesAt = Coordinates(0,0)//Anything except null
        for (x in 0 .. 8) {
            //d.gameStateManager.runProduction(cell)
            d.gameStateManager.tick()
        }
        //Still empty
        assertEquals(listOf<Resource>(), d.mapManager.queryInStorage(d.coords))

        d.gameStateManager.tick()

        //Now there should be an produced item
        assertEquals(listOf(Arrow), d.mapManager.queryInStorage(d.coords))
    }

    @Test
    fun `runConstruction - remove items from production at start of construction`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLumberjack(d.coords),
            GameStateCreator.addLumberToProduction(d.coords),
            GameStateCreator.addLumberToProduction(d.coords)
        ))

        //construction not started
        assertEquals(0, d.mapManager.queryBuilding(d.coords)!!.constructionCount)

        //runConstruction via tick()
        d.gameStateManager.tick()

        //check
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(d.coords))
    }

    @Test
    fun `runConstruction - items are properly removed, even if there are additional in construction`() {
        //init
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLumberjack(d.coords),
            GameStateCreator.addLumberToProduction(d.coords),
            GameStateCreator.addLumberToProduction(d.coords),
            GameStateCreator.addLumberToProduction(d.coords),
            GameStateCreator.addStoneToProduction(d.coords),
        ))

        //construction not started
        assertEquals(0, d.mapManager.queryBuilding(d.coords)!!.constructionCount)

        //runConstruction via tick()
        d.gameStateManager.tick()

        //check
        assertEquals(listOf(Lumber, Stone), d.mapManager.queryInProduction(d.coords))
    }

    @Test
    fun `runProductionWithConsumingOutsideResource - Lumberjack`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLumberjack(d.coords),
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.removeLumberFromRequired(d.coords),
            GameStateCreator.createTree(Coordinates(2,0))
        ))
        val lumberjack = d.mapManager.queryBuilding(d.coords)!!

        lumberjack.setConstructionFinished()
        //some random coordinates to satisfy worker housing requirement
        lumberjack.workerLivesAt = Coordinates(1,1)

        for (x in 0 .. 8) {
            //d.gameStateManager.runProduction(cell)
            d.gameStateManager.tick()
        }
        //Still empty
        assertEquals(listOf<Resource>(), d.mapManager.queryInStorage(d.coords))

        d.gameStateManager.tick()

        //Now there should be an produced item
        assertEquals(listOf(Wood), d.mapManager.queryInStorage(d.coords))
    }

    @Test
    fun `runProductionWithConsumingOutsideResource - Stonemason`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createStonemason(d.coords),
            GameStateCreator.removeWoodFromRequired(d.coords),
            GameStateCreator.removeWoodFromRequired(d.coords),
            GameStateCreator.createRock(Coordinates(2,0))
        ))
        val stonemason = d.mapManager.queryBuilding(d.coords)!!

        stonemason.setConstructionFinished()
        //some random coordinates to satisfy worker housing requirement
        stonemason.workerLivesAt = Coordinates(1,1)

        for (x in 0 .. 8) {
            //d.gameStateManager.runProduction(cell)
            d.gameStateManager.tick()
        }
        //Still empty
        assertEquals(listOf<Resource>(), d.mapManager.queryInStorage(d.coords))

        d.gameStateManager.tick()

        //Now there should be an produced item
        assertEquals(listOf(Stone), d.mapManager.queryInStorage(d.coords))
    }

    @Test
    fun `runProductionWithProducingOutsideResource - Forester - check planting`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createForester(d.coords),
            GameStateCreator.removeWoodFromRequired(d.coords),
            GameStateCreator.removeWoodFromRequired(d.coords)
        ))
        val forester = d.mapManager.queryBuilding(d.coords)!!

        forester.setConstructionFinished()
        //some random coordinates to satisfy worker housing requirement
        forester.workerLivesAt = Coordinates(1,1)

        d.gameStateManager.tick()

        assertEquals(null, d.mapManager.queryWorldResource(d.coords))
        assertEquals(Tree, d.mapManager.queryWorldResource(Coordinates(1,1)))

        for (x in 0 .. 20) {
            d.gameStateManager.tick()
        }

        assertEquals(Tree, d.mapManager.queryWorldResource(Coordinates(1,1)))
        assertEquals(Tree, d.mapManager.queryWorldResource(Coordinates(2,0)))
    }

    @Test
    fun setNextSpawner() {
        assertFalse(d.mapManager.queryBuilding(Coordinates(7,3)) is Spawner)
        d.gameStateManager.setNextSpawner()
        assertTrue(d.mapManager.queryBuilding(Coordinates(7,3)) is Spawner)

        assertFalse(d.mapManager.queryBuilding(Coordinates(5,3)) is Spawner)
        d.gameStateManager.setNextSpawner()
        assertTrue(d.mapManager.queryBuilding(Coordinates(5,3)) is Spawner)
    }

    @Test
    fun `getCellsWhichShallRunAProduction - houses shall run a production, if at least one worker is assigned`() {
        val coordsHouse = Coordinates(0,0)
        val coordsLumberjack = Coordinates(2,0)

        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createLvl1House(coordsHouse),
            GameStateCreator.addFishToProduction(coordsHouse),
            GameStateCreator.createLumberjack(coordsLumberjack),

            ))
        val house = d.mapManager.queryHouse(coordsHouse)!!
        val lumberjack = d.mapManager.queryBuilding(coordsLumberjack)!!
        house.setConstructionFinished()
        lumberjack.setConstructionFinished()

        assertFalse(house.isProductionInProgress())

        d.gameStateManager.tick()

        assertTrue(house.isProductionInProgress())
        assertEquals(2, house.productionCount)

        d.gameStateManager.tick()

        assertEquals(4, house.productionCount)
    }
}