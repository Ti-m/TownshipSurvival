package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ZombieTargetFinderTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun setup() {
        d = BasicTestDependencies()
    }

    @Test
    fun `findTargetForZombie, ignore spawners and roads`() {
        val zombie = Coordinates(0,0)
        val townhall = Coordinates(4,0)// 2 away
        val road = Coordinates(1,1) //1 away
        d.gameStateManager.applyStates(listOf(
            GameState(road, Operator.Set, Type.Building, Road()),
            GameState(townhall, Operator.Set, Type.Building, Townhall()),
            GameState(zombie, Operator.Set, Type.MovingObject, Zombie)
        ))
        d.mapManager.resetTouched()

        val foundAt = d.zombieTargetFinder.find(zombie)

        Assert.assertEquals(townhall, foundAt)
    }
}