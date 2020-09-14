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
    fun `getNeighbourOfCell ignore obstacles`() {
        val middle = Coordinates(3,1)
        val destination =  Coordinates(9,9) // This coordinate is randomly selected,
        // makes no difference in this scenario
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(
            middle,
            destination,
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

    @Test
    fun `getNeighbourOfCell with obstacles`() {
        val middle = Coordinates(3,1)
        val destination = Coordinates(4,2)
        gameStateManager.applyStates(listOf(GameState(destination, Operator.Set, Type.Building, Road())))
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(middle, destination, false)
        assertEquals(listOf(
            Coordinates(4,2)
        ), neighbours)
    }

    @Test
    fun `getNeighbourOfCell ignore tiles of grid, index smaller then zero`() {
        val middle = Coordinates(0,0)
        val destination =  Coordinates(9,9) // This coordinate is randomly selected,
        // makes no difference in this scenario
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(
            middle,
            destination,
            true
        )
        assertEquals(listOf(
            Coordinates(1,1),
            Coordinates(2,0)
        ), neighbours)
    }

    @Test
    fun `getNeighbourOfCell ignore tiles of grid, index to high`() {
        val middle = Coordinates(4,2)
        val destination =  Coordinates(9,9) // This coordinate is randomly selected,
        // makes no difference in this scenario
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(
            middle,
            destination,
            true
        )
        assertEquals(listOf(
            Coordinates(2,2),
            Coordinates(3,1),
            Coordinates(5,1),
        ), neighbours)
    }

    @Test
    fun `getNeighbourOfCell allow any building`() {
        val middle = Coordinates(0,0)
        val destination =  Coordinates(9,9) // This coordinate is randomly selected,
        // makes no difference in this scenario
        gameStateManager.applyStates(listOf(GameState(Coordinates(1,1), Operator.Set, Type.Building, Lumberjack())))
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(
            middle,
            destination,
            false,
            true
        )
        assertEquals(listOf(
            Coordinates(1,1)
        ), neighbours)
    }
}