package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuildDialogHandlerTest {
    private lateinit var cell: Cell
    private lateinit var coordinates: Coordinates
    private lateinit var transportManager: TransportManager
    private lateinit var mapManager: MapManager
    private lateinit var gameStateManager: GameStateManager

    private lateinit var sut: BuildDialogHandler

    @Before
    fun prepare() {
        coordinates = Coordinates(0,0)
        cell = Cell(coordinates = coordinates,type = GroundType.Desert)
        mapManager = MapManagerPreparedForTest(
            cells = mapOf(//override defaults
                Pair(Coordinates(0,0), cell)
            ),
            mapSize = 1
        )
        val neighbourCalculator = HexagonNeighbourCalculator(mapManager)
        val emptyCellFinder = EmptyCellFinder(mapManager, neighbourCalculator)
        val nearbyWorldResourceFinder = NearbyWorldResourceFinder(mapManager, neighbourCalculator)
        transportManager = TransportManager(
            mapManager,
            BreadthFirstSearchRouting(mapManager, neighbourCalculator),
            emptyCellFinder,
            nearbyWorldResourceFinder
        )
        gameStateManager = GameStateManager(transportManager, mapManager)

        sut = BuildDialogHandler(gameStateManager)
    }

    @Test
    fun onClick_buildingType() {
        sut.selectedCallback(Townhall(), coordinates)
        assertTrue(cell.building is Townhall)
        sut.selectedCallback(Lumberjack(), coordinates)
        assertTrue(cell.building is Lumberjack)
        sut.selectedCallback(Road(), coordinates)
        assertTrue(cell.building is Road)
        sut.selectedCallback(Tower(), coordinates)
        assertTrue(cell.building is Tower)
    }

    @Test
    fun onClick_requires() {
        sut.selectedCallback(Townhall(), coordinates)
        assertEquals(emptyList<Resource>(), cell.building!!.requiresConstruction)
        sut.selectedCallback(Lumberjack(), coordinates)
        assertEquals(listOf(Wood, Wood), cell.building!!.requiresConstruction)
        sut.selectedCallback(Road(), coordinates)
        assertEquals(emptyList<Resource>(), cell.building!!.requiresConstruction)
        sut.selectedCallback(Tower(), coordinates)
        assertEquals(listOf(Wood, Stone, Stone), cell.building!!.requiresConstruction)
    }

    @Test
    fun onClick_offers() {
        sut.selectedCallback(Townhall(), coordinates)
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), cell.building!!.offers)
        sut.selectedCallback(Lumberjack(), coordinates)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Road(), coordinates)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Tower(), coordinates)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
    }
}