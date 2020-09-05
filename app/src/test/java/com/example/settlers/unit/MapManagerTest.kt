package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapManagerTest {

    private lateinit var sut: MapManagerTestData
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        sut = MapManagerTestData()
        coords = Coordinates(0,0)
    }

    @Test
    fun queryResourcesOffered() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Offered, Wood)))
        val result = sut.queryResourcesOffered(coords)

        assertEquals(listOf(Wood), result)
    }

    @Test
    fun whereIsResourceOfferedAt() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Offered, Wood)))
        val result = sut.whereIsResourceOfferedAt(Wood)

        assertEquals(coords, result)
    }

    @Test
    fun applyStates_SetRemoveResourceOffered() {
        //Set
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Offered, Wood)))
        assertEquals(listOf(Wood), sut.queryResourcesOffered(coords))
        //Remove
        sut.applyStates(listOf(GameState(coords, Operator.Remove, Type.Offered, Wood)))
        assertEquals(listOf<Resource>(), sut.queryResourcesOffered(coords))
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
        assertEquals(Wood, sut.queryResource1(coords))
        assertEquals(Wood, sut.queryResource2(coords))
        //Remove
        sut.applyStates(
            listOf(
                GameState(coords, Operator.Remove, Type.Resource, Wood),
                GameState(coords, Operator.Remove, Type.Resource, Wood)
            )
        )
        assertNull(sut.queryResource1(coords))
        assertNull(sut.queryResource2(coords))
    }

    @Test
    fun applyStates_SetTownhall() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Townhall())))
        assertTrue(sut.queryBuilding(coords) is Townhall)

        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), sut.queryResourcesOffered(coords))
    }

    @Test
    fun applyStates_SetLumberjack() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Lumberjack())))
        assertTrue(sut.queryBuilding(coords) is Lumberjack)
    }

    @Test
    fun applyStates_SetRoad() {
        sut.applyStates(listOf(GameState(coords, Operator.Set, Type.Building, Road())))
        assertTrue(sut.queryBuilding(coords) is Road)
    }

    @Test
    fun getNeighbourOfCell() {
        val middle = Coordinates(3,1)
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(middle, true)
        assertEquals(listOf(
            Coordinates(4,2),
            Coordinates(2,2),
            Coordinates(1,1),
            Coordinates(2,0),
            Coordinates(4,0),
            Coordinates(5,1)
        ), neighbours)
    }

//    @Test
//    fun getNeighbourOfCellWithObstacles() {
//        val middle = Coordinates(3,1)
//        val destiantion = Coordinates(4,2)
//        sut.applyStates(listOf(GameState(destiantion, Command.SetBuildingRoad)))
//        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(middle,false)
//        Assert.assertEquals(listOf(
//            Coordinates(4,2)
//        ), neighbours)
//    }
}