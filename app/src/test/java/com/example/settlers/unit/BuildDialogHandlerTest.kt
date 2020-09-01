package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.testdoubles.MapManagerTestData
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BuildDialogHandlerTest {
    lateinit var cell: Cell
    lateinit var transportManager: TransportManager
    lateinit var mapManager: MapManager
    lateinit var logger: Logger

    lateinit var sut: BuildDialogHandler

    @Before
    fun prepare() {
        cell = Cell(coordinates = Coordinates(0,0),type = GroundType.Desert)
        mapManager = MapManagerTestData(
            listOf(//override defaults
                cell
            )
        )
        logger = DisabledLogger()
        transportManager = TransportManager(mapManager, BreadthFirstSearchRouting(mapManager), logger)

        sut = BuildDialogHandler(transportManager, mapManager)
    }

    @Test
    fun onClick_buildingType() {
        //val coords: Coordinates = Coordinates(0,0)
        //val transportRequestNew = TransportRequestNew(coords, Resource.Wood)

        sut.onClick(cell, 0)
        assertEquals(BuildingType.Townhall, cell.building!!.type)
        sut.onClick(cell, 1)
        assertEquals(BuildingType.Lumberjack, cell.building!!.type)
        sut.onClick(cell, 2)
        assertEquals(BuildingType.Road, cell.building!!.type)
    }

    @Test
    fun onClick_requires() {
        sut.onClick(cell, 0)//Townhall
        assertEquals(emptyList<Resource>(), cell.building!!.requires)
        sut.onClick(cell, 1)//Lumberjack
        assertEquals(listOf(Resource.Wood, Resource.Wood), cell.building!!.requires)
        sut.onClick(cell, 2)//Road
        assertEquals(emptyList<Resource>(), cell.building!!.requires)
    }

    @Test
    fun onClick_offers() {
        sut.onClick(cell, 0)//Townhall
        assertEquals(listOf(Resource.Wood, Resource.Wood, Resource.Wood, Resource.Stone, Resource.Stone, Resource.Stone), cell.building!!.offers)
        sut.onClick(cell, 1)//Lumberjack
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
        sut.onClick(cell, 2)//Road
        assertEquals(emptyList<Resource>(), cell.building!!.offers)
    }

    @Test
    fun onClick_redraw() {
        assertEquals(false, cell.redraw)
        sut.onClick(cell, 0)//Townhall
        assertEquals(true, cell.redraw)
    }
}