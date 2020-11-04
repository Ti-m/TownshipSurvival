package com.example.settlers.unit

import com.example.settlers.*
import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MapManagerTest {

    private lateinit var sut: MapManagerPreparedForTest
    private lateinit var gameStateManager: GameStateManager
    private lateinit var transportManager: TransportManager
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        sut = MapManagerPreparedForTest()
        transportManager = TransportManagerPreparedForTest(sut)
        gameStateManager = GameStateManagerPreparedForTest(transportManager, sut)
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
    fun getCellsWithBuildings() {
        val c2 = Coordinates(1,1)
        val c3 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(coords, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Building, Townhall()),
                GameState(c3, Operator.Set, Type.Building, Road()),
            )
        )
        val cell1 = sut.findSpecificCell(coords)!!
        val cell2 = sut.findSpecificCell(c2)!!
        val cell3 = sut.findSpecificCell(c3)!!

        val result = sut.getCellsWithBuildings()

        assertEquals(mapOf(
            Pair(coords, cell1),
            Pair(c2, cell2),
            Pair(c3, cell3)
        ), result)
    }

    @Test
    fun getCellsWhichShallRunAProduction() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!
        cell1.building!!.setConstructionFinished()

        val result = sut.getCellsWhichShallRunAProduction()

        assertEquals(mapOf(
            Pair(c1, cell1)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction nothing finished`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Production, Wood)
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!
        val cell2 = sut.findSpecificCell(c2)!!

        val result = sut.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1),
            Pair(c2, cell2)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction Test Building Material availability`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
                GameState(c2, Operator.Set, Type.Production, Wood)//One is missing
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!

        val result = sut.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1)
        ), result)
    }

    @Test
    fun `getCellsWhichShallRunAConstruction one already finished`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        gameStateManager.applyStates(
            listOf(
                GameState(c1, Operator.Set, Type.Building, Lumberjack()),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c1, Operator.Set, Type.Production, Wood),
                GameState(c2, Operator.Set, Type.Building, Lumberjack()),
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!
        val cell2 = sut.findSpecificCell(c2)!!
        cell2.building!!.setConstructionFinished()

        val result = sut.getCellsWhichShallRunAConstruction()

        assertEquals(mapOf(
            Pair(c1, cell1),
            //Pair(c2, cell2) //c2 is done
        ), result)
    }

    @Test
    fun `getCellsWithMovingObjects two cells`() {
        val c1 = Coordinates(1,1)
        val c2 = Coordinates(0,2)
        val gameStateCreator = GameStateCreator()
        gameStateManager.applyStates(
            listOf(
                gameStateCreator.createZombie(c1),
                gameStateCreator.createZombie(c2)
            )
        )
        val cell1 = sut.findSpecificCell(c1)!!
        val cell2 = sut.findSpecificCell(c2)!!

        val result = sut.getCellsWithMovingObjects()

        assertEquals(mapOf(
            Pair(c1, cell1),
            Pair(c2, cell2)
        ), result)
    }



    @Test
    fun `getSouthEastEdge one already finished`() {
        val coords: Coordinates = sut.getSouthEastEdge()
        assertEquals(Coordinates(7,3), coords)
    }
}