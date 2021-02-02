package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MainActivityHelperTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun `Set initial spawner`() {
        MainActivityHelper.setInitialSpawner(d.gameStateManager, d.mapManager)
        val southEastEdge = d.mapManager.getSouthEastEdge()
        Assert.assertTrue(d.mapManager.queryBuilding(southEastEdge) is Spawner)
    }
}