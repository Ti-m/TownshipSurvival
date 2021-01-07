package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameStateManagerTest {

    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var transportManager: TransportManager
    private lateinit var sut: GameStateManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        transportManager = TransportManagerPreparedForTest(mapManager)
        sut = GameStateManagerPreparedForTest(transportManager, mapManager)
        coords = Coordinates(0,0)
    }

    @Test
    fun applyStates_SetRemoveResourceOffered() {
        //Set
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Storage, Wood)))
        assertEquals(listOf(Wood), mapManager.queryInStorage(coords))
        //Remove
        sut.applyStates(listOf(GameState(coords, Operator.Remove, Type.Storage, Wood)))
        assertEquals(listOf<Resource>(), mapManager.queryInStorage(coords))
    }

    @Test
    fun applyStates_SetRemoveResource() {
        //Set
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Transport, Wood),
                GameState(coords, Operator.Set, Type.Transport, Wood)
            )
        )
        assertEquals(listOf(Wood, Wood), mapManager.queryInTransport(coords))
        //Remove
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Remove, Type.Transport, Wood),
                GameState(coords, Operator.Remove, Type.Transport, Wood)
            )
        )
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(coords))
    }

    @Test
    fun applyStates_SetTownhall() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Townhall())))
        assertTrue(mapManager.queryBuilding(coords) is Townhall)

        assertEquals(
            listOf(Wood, Wood, Wood, Stone, Stone, Stone),
            mapManager.queryInStorage(coords)
        )
    }

    @Test
    fun applyStates_SetLumberjack() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Lumberjack())))
        assertTrue(mapManager.queryBuilding(coords) is Lumberjack)
    }

    @Test
    fun applyStates_SetRoad() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Road())))
        assertTrue(mapManager.queryBuilding(coords) is Road)
    }

    @Test
    fun `Test Zombie movement - Find targets and move there`() {
        val destination = Coordinates(0,0)
        val wrongTarget = Coordinates(7,3)
        val spawner = Coordinates(2,2)
        sut.applyStates(listOf(
            GameState(wrongTarget, Operator.Set, Type.Building, Tower()),
            GameState(destination, Operator.Set, Type.Building, Townhall()),
            GameState(spawner, Operator.Set, Type.MovingObject, Zombie)
        ))
        mapManager.resetTouched()
        sut.tick()

        assertEquals(Zombie, mapManager.findSpecificCell(Coordinates(1, 1))!!.movingObject)
        assertEquals(null, mapManager.findSpecificCell(Coordinates(2, 2))!!.movingObject)

        //another step
        mapManager.resetTouched()
        sut.tick()

        assertEquals(Zombie, mapManager.findSpecificCell(Coordinates(0, 0))!!.movingObject)
        assertEquals(null, mapManager.findSpecificCell(Coordinates(1, 1))!!.movingObject)
        assertEquals(null, mapManager.findSpecificCell(Coordinates(2, 2))!!.movingObject)
    }

    @Test
    fun `Removing a building removes requested items`() {
        val cell = mapManager.findSpecificCell(Coordinates(0, 0))!!
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Transport, Wood))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Production, Wood))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Storage, Wood))
        assertEquals(2, cell.requires.count())
        sut.applyState(GameState(Coordinates(0,0), Operator.Remove, Type.Building, null))
        assertEquals(0, cell.production.count())
        assertEquals(0, cell.storage.count())
        assertEquals(0, cell.transport.count())
        assertNull(cell.building)
    }

    @Test
    fun `Replacing a building updates requested items`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        assertEquals(2, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(0, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        assertEquals(0, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(6, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
    }

    @Test
    fun `Replacing a building does not delete the storage`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        assertEquals(0, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(6, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Lumberjack()))
        assertEquals(2, mapManager.findSpecificCell(Coordinates(0, 0))!!.requires.count())
        assertEquals(6, mapManager.findSpecificCell(Coordinates(0, 0))!!.storage.count())
    }

    @Test
    fun `A Building gets destroyed, if a zombie steps into its cell`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.MovingObject, Zombie))
        sut.tick()
        assertNull(mapManager.findSpecificCell(Coordinates(0, 0))!!.building)
    }

    @Test
    fun `A Building gets destroyed, if a zombie steps into its cell, but ignore roads`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Road()))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.MovingObject, Zombie))
        sut.tick()
        assertTrue(mapManager.findSpecificCell(Coordinates(0, 0))!!.building is Road)
    }

    @Test
    fun `A Building gets destroyed, if a zombie steps into its cell, but ignore spawners`() {
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.Building, Spawner()))
        sut.applyState(GameState(Coordinates(0,0), Operator.Set, Type.MovingObject, Zombie))
        sut.tick()
        assertTrue(mapManager.findSpecificCell(Coordinates(0, 0))!!.building is Spawner)
    }

    @Test
    fun `Set explosion animation and check the progress`() {
        sut.applyState(GameState(coords, Operator.Set, Type.Animation, ExplosionAnimation()))
        assertTrue(mapManager.queryAnimation(coords)!! is ExplosionAnimation)
        assertTrue(mapManager.queryAnimation(coords)!!.parts.first() is ExplosionAnimationOne)
        sut.tick()
        assertTrue(mapManager.queryAnimation(coords)!!.parts.first() is ExplosionAnimationTwo)
        sut.tick()
        assertTrue(mapManager.queryAnimation(coords)!!.parts.first() is ExplosionAnimationThree)
        sut.tick()
        assertNull(mapManager.queryAnimation(coords)) //Remove animation, when the cycle is over
    }

    @Test
    fun `engageTarget with ammu`() {
        val tower = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        sut.applyStates(listOf(
            GameState(tower, Operator.Set, Type.Building, Tower()),
            GameState(tower, Operator.Set, Type.Production, Arrow),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        //Finish construction of the tower to allow shooting
        mapManager.getCellsWithTowers().values.first().building!!.setConstructionFinished()
        assertTrue(mapManager.isMovingObject(zombie))//not destroyed
        sut.tick()
        assertFalse(mapManager.isMovingObject(zombie))//destroyed
        assertFalse(mapManager.isMovingObject(Coordinates(2,0)))//check if the zombie just moved away
    }

    @Test
    fun `engageTarget with ammu and range check`() {
        val tower = Coordinates(0,0)
        val zombie = Coordinates(7,3)// 5 away
        sut.applyStates(listOf(
            GameState(tower, Operator.Set, Type.Building, Tower()),
            GameState(tower, Operator.Set, Type.Production, Arrow),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        //Finish construction of the tower to allow shooting
        mapManager.getCellsWithTowers().values.first().building!!.setConstructionFinished()
        assertTrue(mapManager.isMovingObject(zombie))//not destroyed
        sut.tick()
        //not destroyed - out of range, but moved closer
        assertFalse(mapManager.isMovingObject(zombie))
        assertTrue(mapManager.isMovingObject(Coordinates(5,3)))//moved closer
        //again
        sut.tick()
        //not destroyed - out of range, but moved closer
        assertFalse(mapManager.isMovingObject(Coordinates(5,3)))
        assertTrue(mapManager.isMovingObject(Coordinates(3,3)))//moved closer
        sut.tick()
        //destroyed
        assertFalse(mapManager.isMovingObject(Coordinates(3,3)))
        assertFalse(mapManager.isMovingObject(Coordinates(2,2)))//not moved closer
    }

    @Test
    fun `engageTarget no ammu`() {
        val tower = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        sut.applyStates(listOf(
            GameState(tower, Operator.Set, Type.Building, Tower()),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie),
        ))
        //Finish construction of the tower to allow shooting
        mapManager.getCellsWithTowers().values.first().building!!.setConstructionFinished()
        assertTrue(mapManager.isMovingObject(zombie))//not destroyed
        sut.tick()
        //Check all cells to recognize if the zombie is duplicated
        assertFalse(mapManager.isMovingObject(Coordinates(0,0)))
        assertTrue(mapManager.isMovingObject(Coordinates(2,0)))//still not destroyed, but the zombie moved to here
        assertFalse(mapManager.isMovingObject(Coordinates(4,0)))
        assertFalse(mapManager.isMovingObject(Coordinates(1,1)))
        assertFalse(mapManager.isMovingObject(Coordinates(3,1)))
        assertFalse(mapManager.isMovingObject(Coordinates(5,1)))
        assertFalse(mapManager.isMovingObject(Coordinates(0,2)))
        assertFalse(mapManager.isMovingObject(Coordinates(2,2)))
        assertFalse(mapManager.isMovingObject(Coordinates(4,2)))
    }

    @Test
    fun convertStorageToProduction() {
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Storage, Wood),
            )
        )

        sut.tick()

        assertEquals(listOf(Wood), mapManager.queryInProduction(coords))
    }

    @Test
    fun convertTransportToStorage() {
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Transport, Wood),
            )
        )

        sut.tick()

        assertEquals(listOf(Wood), mapManager.queryInStorage(coords))
    }

    @Test
    fun refreshProductionRequirements() {
        //init
        sut.applyStates(listOf(
            GameStateCreator.createFletcher(coords),
            //Finished manually, so clear required.
            GameState(coords, Operator.Remove, Type.Required, Wood),
            GameState(coords, Operator.Remove, Type.Required, Wood)
        ))
        mapManager.queryBuilding(coords)!!.setConstructionFinished()

        assertEquals(listOf<Resource>(), mapManager.queryRequires(coords))
        //exercise
        sut.tick()

        //check
        assertEquals(listOf(Wood), mapManager.queryRequires(coords))
    }

    @Test
    fun `runProduction until an item is put into storage`() {
        sut.applyState(GameStateCreator.createLumberjack(coords))
        val cell = mapManager.findSpecificCell(coords)!!
        cell.building!!.setConstructionFinished()
        for (x in 0 .. 8) {
            //sut.runProduction(cell)
            sut.tick()
        }
        //Still empty
        assertEquals(listOf<Resource>(), mapManager.queryInStorage(coords))

        sut.tick()

        //Now there should be an produced item
        assertEquals(listOf(Wood), mapManager.queryInStorage(coords))
    }


}