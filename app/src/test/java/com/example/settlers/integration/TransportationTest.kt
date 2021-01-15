package com.example.settlers.integration

import com.example.settlers.*
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class TransportationTest {

    private lateinit var mapManager: MapManagerPreparedForTest
    private lateinit var transportManager: TransportManager
    private lateinit var gameStateManager: GameStateManager

    @Before
    fun prepare() {
        mapManager = MapManagerPreparedForTest()
        transportManager = TransportManagerPreparedForTest(mapManager)
        gameStateManager = GameStateManagerPreparedForTest(transportManager, mapManager)
    }

    @Test
    fun `tansport of a single item`() {
        //TODO this seems to be some higherlvl class, which handles the different managers. Like GameManager??

        val provider = Coordinates(0,0)
        val destiantion = Coordinates(1,1)//This test only works for a single tile, because the it ticks only once

        gameStateManager.applyStates(listOf(
            GameState(provider, Operator.Set, Type.Building, Townhall()),
            GameState(destiantion, Operator.Set, Type.Building, Lumberjack()),
        ))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = destiantion))
        assertEquals(listOf(Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        //TODO Another tick to convert. Do I really want to do this here? This test gets really messy. Block Cells?
        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood), mapManager.queryInStorage(at = destiantion))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = provider))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = provider))
        assertEquals(listOf(Wood), mapManager.queryInProduction(at = destiantion))
    }

    @Test
    fun `transport of 3 items - full disclosure`() {
        gameStateManager.applyStates(GameStateCreator.L3_T3_unfinishedRoad())
        gameStateManager.applyState(GameState(Coordinates(3,1), Operator.Set, Type.Building, Road()))//Finish road


        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = Coordinates(2,0)))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = Coordinates(1,1)))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), mapManager.queryInStorage(at = Coordinates(2,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))

        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(2,0)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(1,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(2,2)))

        //Don't show these each round, until actually something goes to production
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(2,0)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(1,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,2)))

        gameStateManager.tick()
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), mapManager.queryRequires(at = Coordinates(6,2)))
        //Some steps are skipped for brevity
    }

    @Test
    fun `transportation - checks the sortingorder in which the requesting buildings get deliveries`() {
        //init
        val cTown = Coordinates(0,0)
        val cRoad1 = Coordinates(2,0)
        val cRoad2 = Coordinates(4,0)
        val cLumber = Coordinates(1,1)
        val cFletcher1 = Coordinates(6,0)
        val cFletcher2 = Coordinates(5,1)
        val tree1 = Coordinates(3,1)
        val tree2 = Coordinates(7,1)
        val tree3 = Coordinates(0,2)
        val tree4 = Coordinates(2,2)
        val tree5 = Coordinates(4,2)
        val tree6 = Coordinates(6,2)

        gameStateManager.applyStates(listOf(
            GameStateCreator.createTownhall(cTown),
            GameStateCreator.createRoad(cRoad1),
            GameStateCreator.createRoad(cRoad2),
            GameStateCreator.createLumberjack(cLumber),
            GameStateCreator.createFletcher(cFletcher1),
            GameStateCreator.createFletcher(cFletcher2),
            GameStateCreator.createTree(tree1),
            GameStateCreator.createTree(tree2),
            GameStateCreator.createTree(tree3),
            GameStateCreator.createTree(tree4),
            GameStateCreator.createTree(tree5),
            GameStateCreator.createTree(tree6),
        ))

        val town = mapManager.queryBuilding(cTown)!!
        val road1 = mapManager.queryBuilding(cRoad1)!!
        val road2 = mapManager.queryBuilding(cRoad2)!!
        val lumber = mapManager.queryBuilding(cLumber)!!
        val fletcher1 = mapManager.queryBuilding(cFletcher1)!!
        val fletcher2 = mapManager.queryBuilding(cFletcher2)!!

        //runConstruction via tick()
        for (x in 1 .. 16) {
            //sut.runProduction(cell)
            gameStateManager.tick()
        }
        assertTrue(lumber.isConstructed())
        assertEquals(listOf(Wood), mapManager.queryInProduction(cFletcher2))
        assertEquals(listOf(Wood), mapManager.queryRequires(cFletcher2))

        for (x in 1 .. 14) {
            //sut.runProduction(cell)
            gameStateManager.tick()
        }

        assertEquals(listOf(Wood), mapManager.queryInStorage(cFletcher2))
        assertEquals(listOf(Wood), mapManager.queryInProduction(cFletcher2))
        assertEquals(listOf(Wood), mapManager.queryRequires(cFletcher2))
    }
}
