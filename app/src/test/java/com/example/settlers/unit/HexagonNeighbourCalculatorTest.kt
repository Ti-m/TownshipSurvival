package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HexagonNeighbourCalculatorTest {

    private val logger: Logger = DisabledLogger()
    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var gameStateManager: GameStateManager
    private lateinit var transportManager: TransportManager
    private lateinit var sut: HexagonNeighbourCalculator
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        sut = HexagonNeighbourCalculator(mapManager)
        transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager, sut),
            logger)
        gameStateManager = GameStateManager(transportManager, mapManager, logger)
        coords = Coordinates(0,0)
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
        Assert.assertEquals(
            listOf(
                Coordinates(4, 2),
                Coordinates(2, 2),
                Coordinates(1, 1),
                Coordinates(2, 0),
                Coordinates(4, 0),
                Coordinates(5, 1)
            ), neighbours
        )
    }

    @Test
    fun `getNeighbourOfCell with obstacles`() {
        val middle = Coordinates(3,1)
        val destination = Coordinates(4,2)
        gameStateManager.applyStates(listOf(GameState(destination, Operator.Set, Type.Building, Road())))
        val neighbours: List<Coordinates> = sut.getNeighboursOfCellDoubleCoords(middle, destination, false)
        Assert.assertEquals(
            listOf(
                Coordinates(4, 2)
            ), neighbours
        )
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
        Assert.assertEquals(
            listOf(
                Coordinates(1, 1),
                Coordinates(2, 0)
            ), neighbours
        )
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
        Assert.assertEquals(
            listOf(
                Coordinates(2, 2),
                Coordinates(3, 1),
                Coordinates(5, 1),
            ), neighbours
        )
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
        Assert.assertEquals(
            listOf(
                Coordinates(1, 1)
            ), neighbours
        )
    }
}