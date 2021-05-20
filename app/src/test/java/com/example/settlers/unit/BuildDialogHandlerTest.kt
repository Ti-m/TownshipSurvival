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
        sut.selectedCallback(Forester(), d.coords)
        assertTrue(cell.building is Forester)
        sut.selectedCallback(Lumberjack(), d.coords)
        assertTrue(cell.building is Lumberjack)
        sut.selectedCallback(Stonemason(), d.coords)
        assertTrue(cell.building is Stonemason)
        sut.selectedCallback(Fletcher(), d.coords)
        assertTrue(cell.building is Fletcher)
        sut.selectedCallback(Lumbermill(), d.coords)
        assertTrue(cell.building is Lumbermill)
        sut.selectedCallback(Pyramid(), d.coords)
        assertTrue(cell.building is Pyramid)
        sut.selectedCallback(Road(), d.coords)
        assertTrue(cell.building is Road)
        sut.selectedCallback(Tower(), d.coords)
        assertTrue(cell.building is Tower)
    }

    @Test
    fun onClick_requires() {
        sut.selectedCallback(Townhall(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.requiresConstruction)
        sut.selectedCallback(Forester(), d.coords)
        assertEquals(listOf(Lumber, Lumber), cell.building!!.requiresConstruction)
        sut.selectedCallback(Lumberjack(), d.coords)
        assertEquals(listOf(Lumber, Lumber), cell.building!!.requiresConstruction)
        sut.selectedCallback(Stonemason(), d.coords)
        assertEquals(listOf(Lumber, Lumber), cell.building!!.requiresConstruction)
        sut.selectedCallback(Fletcher(), d.coords)
        assertEquals(listOf(Lumber, Lumber, Stone), cell.building!!.requiresConstruction)
        sut.selectedCallback(Lumbermill(), d.coords)
        assertEquals(listOf(Lumber, Lumber, Stone), cell.building!!.requiresConstruction)
        sut.selectedCallback(Pyramid(), d.coords)
        assertEquals(mutableListOf<Resource>().apply {
            this.addAll(MutableList(50) { Lumber })
            this.addAll(MutableList(50) { Stone })
        }, cell.building!!.requiresConstruction)
        sut.selectedCallback(Road(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.requiresConstruction)
        sut.selectedCallback(Tower(), d.coords)
        assertEquals(listOf(Lumber, Stone, Stone), cell.building!!.requiresConstruction)
    }

    @Test
    fun onClick_offers() {
        sut.selectedCallback(Townhall(), d.coords)
        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone, Fish, Fish), cell.building!!.offers)
        sut.selectedCallback(Forester(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Lumberjack(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Stonemason(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Fletcher(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Lumbermill(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Pyramid(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Road(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Tower(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(Fisherman(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(HouseLevel1(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(HouseLevel2(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.selectedCallback(HouseLevel3(), d.coords)
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
    }
}