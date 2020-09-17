package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.BreadthFirstSearchRouting
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuildDialogHandlerTest {
    private lateinit var cell: Cell
    private lateinit var transportManager: TransportManager
    private lateinit var mapManager: MapManager
    private lateinit var gameStateManager: GameStateManager

    private lateinit var sut: BuildDialogHandler

    @Before
    fun prepare() {
        cell = Cell(coordinates = Coordinates(0,0),type = GroundType.Desert)
        mapManager = MapManagerPreparedForTest(
            cells = mapOf(//override defaults
                Pair(Coordinates(0,0), cell)
            ),
            mapSize = 1
        )
        transportManager = TransportManagerPreparedForTest(mapManager)
        gameStateManager = GameStateManagerPreparedForTest(transportManager, mapManager)
        sut = BuildDialogHandler(gameStateManager)
    }

    @Test
    fun onClick_buildingType() {
        sut.onClick(cell, Townhall())
        assertTrue(cell.building is Townhall)
        sut.onClick(cell, Lumberjack())
        assertTrue(cell.building is Lumberjack)
        sut.onClick(cell, Road())
        assertTrue(cell.building is Road)
    }

    @Test
    fun onClick_requires() {
        sut.onClick(cell, Townhall())
        assertEquals(emptyList<Resource>(), cell.building!!.requires)
        sut.onClick(cell, Lumberjack())
        assertEquals(listOf(Wood, Wood), cell.building!!.requires)
        sut.onClick(cell, Road())
        assertEquals(emptyList<Resource>(), cell.building!!.requires)
    }

    @Test
    fun onClick_offers() {
        sut.onClick(cell, Townhall())
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), cell.building!!.offers)
        sut.onClick(cell, Lumberjack())
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.onClick(cell, Road())
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
    }

    @Test
    fun onClick_redraw() {
        assertEquals(false, cell.redraw)
        sut.onClick(cell, Townhall())
        assertEquals(true, cell.redraw)
    }
}