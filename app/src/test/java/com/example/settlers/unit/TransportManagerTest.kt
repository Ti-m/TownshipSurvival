package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class TransportManagerTest {

    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var sut: TransportManager
    private lateinit var gameStateManager: GameStateManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        sut = TransportManagerPreparedForTest(mapManager)
        gameStateManager = GameStateManagerPreparedForTest(sut, mapManager)
        coords = Coordinates(0,0)
    }

    @Test
    fun `whereIsNextResourceInTransportWithAccess from same tile`() {
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Building, Road())
        ))
        mapManager.resetTouched()

        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInTransportWithAccess(TransportRequest(coords, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsNextResourceInTransportWithAccess from one tile over`() {
        val dest = Coordinates(2,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Transport, Wood),
            GameState(coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        mapManager.resetTouched()

        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInTransportWithAccess(TransportRequest(dest, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsNextResourceInStorageWithAccess from same tile`() {
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Building, Road())
        ))
        mapManager.resetTouched()
        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInStorageWithAccess(TransportRequest(coords, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `whereIsNextResourceInStorageWithAccess from one tile over`() {
        val dest = Coordinates(2,0)
        gameStateManager.applyStates(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        mapManager.resetTouched()
        //The coordinates are irrelevant here
        val result = sut.whereIsNextResourceInStorageWithAccess(TransportRequest(dest, Wood))

        assertEquals(coords, result)
    }

    @Test
    fun `moveResources step #1`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.resetTouched()
        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameState(Coordinates(x=0, y=0), Operator.Remove, Type.Storage, Wood),
            GameState(Coordinates(x=0, y=0), Operator.Set, Type.Transport, Wood)
        ), result)
    }

    @Test
    fun `moveResources step #2`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(Coordinates(0,0), Operator.Set, Type.Transport, Wood),
            GameState(Coordinates(0,0), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.resetTouched()
        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameState(Coordinates(x=0, y=0), Operator.Remove, Type.Transport, Wood),
            GameState(Coordinates(x=1, y=1), Operator.Set, Type.Transport, Wood)
        ), result)
    }

    @Test
    fun `moveResources step #3`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(Coordinates(1,1), Operator.Set, Type.Transport, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.resetTouched()

        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameState(Coordinates(x=1, y=1), Operator.Remove, Type.Transport, Wood),
            GameState(Coordinates(x=2, y=2), Operator.Set, Type.Transport, Wood)
        ), result)
    }

    @Test
    fun `moveResources step #4`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(dest, Operator.Set, Type.Transport, Wood),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf<GameState>(), result)
    }

    @Test
    fun `moveResources already touched`() {
        val dest = Coordinates(2,2)
        gameStateManager.applyStates(listOf(
            GameState(Coordinates(0,0), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Lumberjack())
        ))
        mapManager.findSpecificCell(Coordinates(0,0))!!.touched = true
        val result = sut.moveResources(mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf<GameState>(
        ), result)
    }

    @Test
    fun `shootWithTowerCalculatePath all Conditions green`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        gameStateManager.applyStates(listOf(
            GameState(towerCoordinates, Operator.Set, Type.Building, tower),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        mapManager.resetTouched()
        val targetCoordinates = sut.shootWithTowerCalculatePath(towerCoordinates, tower.range)

        assertEquals(TargetCoordinates(
            start = towerCoordinates,
            path = listOf(Coordinates(x=2, y=0)),
            destination = zombie
        ), targetCoordinates)
    }

    @Test
    fun `shootWithTowerCalculatePath no target`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        gameStateManager.applyStates(listOf(
            GameState(towerCoordinates, Operator.Set, Type.Building, tower)
        ))
        mapManager.resetTouched()
        val targetCoordinates = sut.shootWithTowerCalculatePath(towerCoordinates, tower.range)

        assertNull(targetCoordinates)
    }

    @Test
    fun `shootWithTowerCalculatePath - target is inside tower - to late for shooting`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        gameStateManager.applyStates(listOf(
            GameState(towerCoordinates, Operator.Set, Type.Building, tower),
            GameState(towerCoordinates, Operator.Set, Type.MovingObject, Zombie)
        ))
        mapManager.resetTouched()
        val targetCoordinates = sut.shootWithTowerCalculatePath(towerCoordinates, tower.range)

        assertNull(targetCoordinates)
    }

    @Test
    fun `handleRequestsInStorage - checks if in transport are already 2 items and nothing more is allowed to be added`() {
        gameStateManager.applyStates(listOf(
            GameStateCreator.createTownhall(coords),
            GameStateCreator.addWoodToStorage(coords),
            GameStateCreator.addWoodToStorage(coords),
            GameStateCreator.addWoodToStorage(coords),
            GameStateCreator.addWoodToStorage(coords),
            GameStateCreator.addWoodToStorage(coords),
            GameStateCreator.createFletcher(Coordinates(2,0))
        ))
        val cell = mapManager.findSpecificCell(coords)!!
        cell.building!!.setConstructionFinished()
        mapManager.resetTouched()
        val dest = mapManager.findSpecificCell(Coordinates(2,0))!!

        gameStateManager.applyStates(sut.moveResources(dest))
        mapManager.resetTouched()
        gameStateManager.applyStates(sut.moveResources(dest))
        mapManager.resetTouched()
        gameStateManager.applyStates(sut.moveResources(dest))
        mapManager.resetTouched()
        gameStateManager.applyStates(sut.moveResources(dest))
        mapManager.resetTouched()
        gameStateManager.applyStates(sut.moveResources(dest))
    }
}