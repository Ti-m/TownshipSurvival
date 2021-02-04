package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TowerTargetFinderTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun setup() {
        d = BasicTestDependencies()
    }

    @Test
    fun `findTargetForTower, no target`() {

        val coordinates = Coordinates(0,0)
        val tower = Tower()

        d.gameStateManager.applyStates(listOf(
            GameState(coordinates, Operator.Set, Type.Building, tower)
        ))
        d.mapManager.resetTouched()
        val foundAt = d.towerTagetFinder.find(coordinates, tower.range)

        Assert.assertEquals(null, foundAt)
    }

    @Test
    fun `findTargetForTower, one target`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        d.gameStateManager.applyStates(listOf(
            GameState(towerCoordinates, Operator.Set, Type.Building, tower),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        d.mapManager.resetTouched()

        val foundAt = d.towerTagetFinder.find(towerCoordinates, tower.range)

        Assert.assertEquals(zombie, foundAt)
    }

    @Test
    fun `findTargetForTower,two targets`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        val zombie = Coordinates(4,0)// 2 away
        val zombie2 = Coordinates(1,1) //1 away
        d.gameStateManager.applyStates(listOf(
            GameState(towerCoordinates, Operator.Set, Type.Building, tower),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie),
            GameState(zombie2, Operator.Set, Type.MovingObject, Zombie)
        ))
        d.mapManager.resetTouched()
        val foundAt = d.towerTagetFinder.find(towerCoordinates, tower.range)

        Assert.assertEquals(zombie2, foundAt)
    }

    @Test
    fun `findTargetForTower, one target - out of range`() {
        val tower = Tower()
        val towerCoordinates = Coordinates(0,0)
        val zombie = Coordinates(6,2)// 4 away
        d.gameStateManager.applyStates(listOf(
            GameState(towerCoordinates, Operator.Set, Type.Building, tower),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        d.mapManager.resetTouched()

        val foundAt = d.towerTagetFinder.find(towerCoordinates, tower.range)

        Assert.assertNull(foundAt)
    }
}