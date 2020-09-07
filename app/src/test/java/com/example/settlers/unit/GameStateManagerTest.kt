package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GameStateManagerTest {

    private val logger: Logger = DisabledLogger()
    private lateinit var mapManager: MapManagerTestData
    private lateinit var sut: GameStateManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        mapManager = MapManagerTestData()
        sut = GameStateManager(mapManager, logger)
        coords = Coordinates(0,0)
    }

    @Test
    fun applyStates_SetRemoveResourceOffered() {
        //Set
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Offered, Wood)))
        Assert.assertEquals(listOf(Wood), mapManager.queryResourcesOffered(coords))
        //Remove
        sut.applyStates(listOf(GameState(coords, Operator.Remove, Type.Offered, Wood)))
        Assert.assertEquals(listOf<Resource>(), mapManager.queryResourcesOffered(coords))
    }

    @Test
    fun applyStates_SetRemoveResource() {
        //Set
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Resource, Wood),
                GameState(coords, Operator.Set, Type.Resource, Wood)
            )
        )
        Assert.assertEquals(listOf(Wood, Wood), mapManager.queryResources(coords))
        //Remove
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Remove, Type.Resource, Wood),
                GameState(coords, Operator.Remove, Type.Resource, Wood)
            )
        )
        Assert.assertEquals(emptyList<Resource>(), mapManager.queryResources(coords))
    }

    @Test
    fun applyStates_SetTownhall() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Townhall())))
        Assert.assertTrue(mapManager.queryBuilding(coords) is Townhall)

        Assert.assertEquals(
            listOf(Wood, Wood, Wood, Stone, Stone, Stone),
            mapManager.queryResourcesOffered(coords)
        )
    }

    @Test
    fun applyStates_SetLumberjack() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Lumberjack())))
        Assert.assertTrue(mapManager.queryBuilding(coords) is Lumberjack)
    }

    @Test
    fun applyStates_SetRoad() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Road())))
        Assert.assertTrue(mapManager.queryBuilding(coords) is Road)
    }
}