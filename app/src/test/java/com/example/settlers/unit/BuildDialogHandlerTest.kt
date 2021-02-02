package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuildDialogHandlerTest {

    private lateinit var d: BasicTestDependencies

    private lateinit var cell: Cell

    private lateinit var sut: BuildDialogHandler

    @Before
    fun prepare() {
        cell = Cell(coordinates = Coordinates(0,0),type = GroundType.Desert)
        d = BasicTestDependencies(
            mapManager = MapManagerPreparedForTest(
                cells = mapOf(//override defaults
                    Pair(Coordinates(0,0), cell)
                ),
                mapSize = 1
            )
        )

        sut = BuildDialogHandler(d.gameStateManager)
    }

    @Test
    fun onClick_buildingType() {
        sut.selectedCallback(Townhall(), d.coords)
        assertTrue(cell.building is Townhall)
        sut.selectedCallback(Lumberjack(), d.coords)
        assertTrue(cell.building is Lumberjack)
        sut.selectedCallback(Road(), d.coords)
        assertTrue(cell.building is Road)
        sut.selectedCallback(Tower(), d.coords)
        assertTrue(cell.building is Tower)
    }

    @Test
    fun onClick_requires() {
        sut.selectedCallback(Townhall(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.requiresConstruction)
        sut.selectedCallback(Lumberjack(), d.coords)
        assertEquals(listOf(Wood, Wood), cell.building!!.requiresConstruction)
        sut.selectedCallback(Road(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.requiresConstruction)
        sut.selectedCallback(Tower(), d.coords)
        assertEquals(listOf(Wood, Stone, Stone), cell.building!!.requiresConstruction)
    }

    @Test
    fun onClick_offers() {
        sut.selectedCallback(Townhall(), d.coords)
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), cell.building!!.offers)
        sut.selectedCallback(Lumberjack(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Road(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Tower(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
    }
}