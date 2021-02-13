package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class TransportManagerTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun `moveResources step #1`() {
        val c1 = Coordinates(0,0)
        val c2 = Coordinates(1,1)
        val dest = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTownhall(c1),
            GameStateCreator.createRoad(c2),
            GameStateCreator.createLumberjack(dest),
        ))
        d.mapManager.resetTouched()
        val result = d.transportManager.moveResources(d.mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameStateCreator.removeLumberFromStorage(c1),
            GameStateCreator.addLumberToTransport(c1),
        ), result)
    }

    @Test
    fun `moveResources step #2`() {
        val c1 = Coordinates(0,0)
        val c2 = Coordinates(1,1)
        val dest = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.addLumberToTransport(c1),
            GameStateCreator.createRoad(c1),
            GameStateCreator.createRoad(c2),
            GameStateCreator.createLumberjack(dest),
        ))
        d.mapManager.resetTouched()
        val result = d.transportManager.moveResources(d.mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameStateCreator.removeLumberFromTransport(c1),
            GameStateCreator.addLumberToTransport(c2),
        ), result)
    }

    @Test
    fun `moveResources step #3`() {
        val c2 = Coordinates(1,1)
        val dest = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.addLumberToTransport(c2),
            GameStateCreator.createRoad(c2),
            GameStateCreator.createLumberjack(dest),
        ))
        d.mapManager.resetTouched()

        val result = d.transportManager.moveResources(d.mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf(
            GameStateCreator.removeLumberFromTransport(c2),
            GameStateCreator.addLumberToTransport(dest),
        ), result)
    }

    @Test
    fun `moveResources step #4`() {
        val dest = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.addLumberToTransport(dest),
            GameStateCreator.createLumberjack(dest),
        ))
        val result = d.transportManager.moveResources(d.mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf<GameState>(), result)
    }

    @Test
    fun `moveResources already touched`() {
        val c1 = Coordinates(0,0)
        val c2 = Coordinates(1,1)
        val dest = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTownhall(c1),
            GameStateCreator.createRoad(c2),
            GameStateCreator.createLumberjack(dest),
        ))
        d.mapManager.findSpecificCell(Coordinates(0,0))!!.touched = true
        val result = d.transportManager.moveResources(d.mapManager.findSpecificCell(dest)!!)

        assertEquals(listOf<GameState>(
        ), result)
    }

    @Test
    fun `shootWithTowerCalculatePath all Conditions green`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(towerCoordinates),
            GameStateCreator.createZombie(zombie)
        ))
        d.mapManager.resetTouched()
        val targetCoordinates = d.transportManager.shootWithTowerCalculatePath(towerCoordinates, tower.range)

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
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(towerCoordinates),
        ))
        d.mapManager.resetTouched()
        val targetCoordinates = d.transportManager.shootWithTowerCalculatePath(towerCoordinates, tower.range)

        assertNull(targetCoordinates)
    }

    @Test
    fun `shootWithTowerCalculatePath - target is inside tower - to late for shooting`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTower(towerCoordinates),
            GameStateCreator.createZombie(towerCoordinates),
        ))
        d.mapManager.resetTouched()
        val targetCoordinates = d.transportManager.shootWithTowerCalculatePath(towerCoordinates, tower.range)

        assertNull(targetCoordinates)
    }

    @Test
    fun `handleRequestsInStorage - checks if in transport are already 2 items and nothing more is allowed to be added`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTownhall(d.coords),
            GameStateCreator.addWoodToStorage(d.coords),
            GameStateCreator.addWoodToStorage(d.coords),
            GameStateCreator.addWoodToStorage(d.coords),
            GameStateCreator.addWoodToStorage(d.coords),
            GameStateCreator.addWoodToStorage(d.coords),
            GameStateCreator.createFletcher(Coordinates(2,0))
        ))
        val cell = d.mapManager.findSpecificCell(d.coords)!!
        cell.building!!.setConstructionFinished()
        d.mapManager.resetTouched()
        val dest = d.mapManager.findSpecificCell(Coordinates(2,0))!!

        d.gameStateManager.applyStates(d.transportManager.moveResources(dest))
        d.mapManager.resetTouched()
        d.gameStateManager.applyStates(d.transportManager.moveResources(dest))
        d.mapManager.resetTouched()
        d.gameStateManager.applyStates(d.transportManager.moveResources(dest))
        d.mapManager.resetTouched()
        d.gameStateManager.applyStates(d.transportManager.moveResources(dest))
        d.mapManager.resetTouched()
        d.gameStateManager.applyStates(d.transportManager.moveResources(dest))
    }

    @Test
    fun `isSpaceAvailableForWorldResource - available`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords)
        ))
        assertEquals(true, d.transportManager.isSpaceAvailableForWorldResource(d.coords))
    }

    @Test
    fun `isSpaceAvailableForWorldResource - not available wit range 3`() {
        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createFletcher(d.coords),
            //Block all possible cells in range 3
            GameStateCreator.createFletcher(Coordinates(2,0)),
            GameStateCreator.createFletcher(Coordinates(4,0)),
            GameStateCreator.createFletcher(Coordinates(6,0)),
            GameStateCreator.createFletcher(Coordinates(1,1)),
            GameStateCreator.createFletcher(Coordinates(3,1)),
            GameStateCreator.createFletcher(Coordinates(5,1)),
            GameStateCreator.createFletcher(Coordinates(0,2)),
            GameStateCreator.createFletcher(Coordinates(2,2)),
            GameStateCreator.createFletcher(Coordinates(4,2)),
            GameStateCreator.createFletcher(Coordinates(1,3)),
            GameStateCreator.createFletcher(Coordinates(3,3)),
        ))
        assertEquals(false, d.transportManager.isSpaceAvailableForWorldResource(d.coords))
    }
}