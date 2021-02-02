package com.example.settlers.integration

import com.example.settlers.*
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class TransportationTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun prepare() {
        d = BasicTestDependencies()
    }

    @Test
    fun `tansport of a single item`() {
        //TODO this seems to be some higherlvl class, which handles the different managers. Like GameManager??

        val provider = Coordinates(0,0)
        val destiantion = Coordinates(1,1)//This test only works for a single tile, because the it ticks only once

        d.gameStateManager.applyStates(listOf(
            GameState(provider, Operator.Set, Type.Building, Townhall()),
            GameState(destiantion, Operator.Set, Type.Building, Lumberjack()),
        ))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))

        d.gameStateManager.tick()
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood, Wood, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = provider))

        d.gameStateManager.tick()
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = destiantion))
        assertEquals(listOf(Wood, Wood, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = provider))

        //TODO Another tick to convert. Do I really want to do this here? This test gets really messy. Block Cells?
        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood), d.mapManager.queryInStorage(at = destiantion))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = provider))

        d.gameStateManager.tick()
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Wood, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))
        assertEquals(listOf(Wood), d.mapManager.queryInProduction(at = destiantion))
    }

    @Test
    fun `transport of 3 items - full disclosure`() {
        d.gameStateManager.applyStates(GameStateCreator.L3_T3_unfinishedRoad())
        d.gameStateManager.applyState(GameState(Coordinates(3,1), Operator.Set, Type.Building, Road()))//Finish road


        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), d.mapManager.queryInStorage(at = Coordinates(2,0)))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), d.mapManager.queryInStorage(at = Coordinates(1,1)))
        assertEquals(listOf(Wood, Wood, Wood, Stone, Stone, Stone), d.mapManager.queryInStorage(at = Coordinates(2,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))

        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(2,0)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(1,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(2,2)))

        //Don't show these each round, until actually something goes to production
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(2,0)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(1,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Wood), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(listOf(Wood), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Wood, Wood), d.mapManager.queryRequires(at = Coordinates(6,2)))
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

        d.gameStateManager.applyStates(listOf(
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

        val town = d.mapManager.queryBuilding(cTown)!!
        val road1 = d.mapManager.queryBuilding(cRoad1)!!
        val road2 = d.mapManager.queryBuilding(cRoad2)!!
        val lumber = d.mapManager.queryBuilding(cLumber)!!
        val fletcher1 = d.mapManager.queryBuilding(cFletcher1)!!
        val fletcher2 = d.mapManager.queryBuilding(cFletcher2)!!

        //runConstruction via tick()
        for (x in 1 .. 16) {
            //sut.runProduction(cell)
            d.gameStateManager.tick()
        }
        assertTrue(lumber.isConstructed())
        assertEquals(listOf(Wood), d.mapManager.queryInProduction(cFletcher2))
        assertEquals(listOf(Wood), d.mapManager.queryRequires(cFletcher2))

        for (x in 1 .. 14) {
            //sut.runProduction(cell)
            d.gameStateManager.tick()
        }

        assertEquals(listOf(Wood), d.mapManager.queryInStorage(cFletcher2))
        assertEquals(listOf(Wood), d.mapManager.queryInProduction(cFletcher2))
        assertEquals(listOf(Wood), d.mapManager.queryRequires(cFletcher2))
    }
}
