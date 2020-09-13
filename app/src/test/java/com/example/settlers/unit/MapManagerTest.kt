package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapManagerTest {

    private val logger: Logger = DisabledLogger()
    private lateinit var sut: MapManagerTestData
    private lateinit var gameStateManager: GameStateManager
    private lateinit var transportManager: TransportManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        sut = MapManagerTestData()
        transportManager = TransportManager(sut, BreadthFirstSearchRouting(sut), logger)
        gameStateManager = GameStateManager(transportManager, sut, logger)
        coords = Coordinates(0,0)
    }

    @Test
    fun queryResourcesOffered() {
        gameStateManager.applyStates(listOf(GameState(coords, Operator.Set, Type.Storage, Wood)))
        val result = sut.queryInStorage(coords)

        assertEquals(listOf(Wood), result)
    }

    @Test
    fun whereIsResourceOfferedAt() {
        gameStateManager.applyStates(listOf(GameState(coords, Operator.Set, Type.Storage, Wood)))
        //The coordinates are irrelevant here
        val result = sut.whereIsResourceOfferedAt(TransportRequest(Coordinates(0,0), Wood))

        assertEquals(coords, result)
    }

    @Test
    fun getCellsWhichRequireStuff() {
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
            )
        )
        assertEquals(
            mapOf(Pair(coords, Cell(coords, GroundType.Desert, requires = mutableListOf(Wood)))),
            sut.getCellsWhichRequireStuff()
        )
    }

    @Test
    fun matchTransportToStorage() {
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Transport, Wood),
            )
        )
        //The specific coordinates are irrelevant here
        val result = sut.convertTransportToStorage(sut.findSpecificCell(coords)!!)

        assertEquals(listOf(
            GameState(coords, Operator.Set, Type.Storage, Wood),
            GameState(coords, Operator.Remove, Type.Transport, Wood)
        ), result)

        gameStateManager.applyStates(result)
        assertEquals(listOf(Wood), sut.queryInStorage(coords))
    }

    @Test
    fun convertStorageToProduction() {
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Required, Wood),
                GameState(coords, Operator.Set, Type.Storage, Wood),
            )
        )
        //The specific coordinates are irrelevant here
        val result = sut.convertStorageToProduction(sut.findSpecificCell(coords)!!)

        assertEquals(listOf(
            GameState(coords, Operator.Set, Type.Production, Wood),
            GameState(coords, Operator.Remove, Type.Storage, Wood),
            GameState(coords, Operator.Remove, Type.Required, Wood),
        ), result)

        gameStateManager.applyStates(result)

        assertEquals(listOf(Wood), sut.queryInProduction(coords))
    }

    @Test
    fun getNeighbourOfCell() {
        val middle = Coordinates(3,1)
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(
            middle,
            Coordinates(9,9) /* This coordinate is randomly selected*/,
            true
        )
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