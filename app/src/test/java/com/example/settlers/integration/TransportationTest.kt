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
    fun `transport of a single item`() {
        //TODO this seems to be some higherlvl class, which handles the different managers. Like GameManager??

        val provider = Coordinates(0,0)
        val destination = Coordinates(1,1)//This test only works for a single tile, because the it ticks only once

        d.gameStateManager.applyStates(listOf(
            GameStateCreator.createTownhall(provider),
            GameStateCreator.createLumberjack(destination),
        ))
        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))

        d.gameStateManager.tick()
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = provider))

        d.gameStateManager.tick()
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = destination))
        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = provider))

        //TODO Another tick to convert. Do I really want to do this here? This test gets really messy. Block Cells?
        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Lumber), d.mapManager.queryInStorage(at = destination))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = provider))

        d.gameStateManager.tick()
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = provider))
        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone), d.mapManager.queryInStorage(at = provider))
        assertEquals(listOf(Lumber), d.mapManager.queryInProduction(at = destination))
    }

    @Test
    fun `transport of 3 items - full disclosure`() {
        d.gameStateManager.applyStates(GameStateCreator.L3_T3_unfinishedRoad())
        d.gameStateManager.applyState(GameStateCreator.createRoad(Coordinates(3,1)))//Finish road


        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone), d.mapManager.queryInStorage(at = Coordinates(2,0)))
        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone), d.mapManager.queryInStorage(at = Coordinates(1,1)))
        assertEquals(listOf(Lumber, Lumber, Lumber, Lumber, Lumber, Lumber, Stone, Stone, Stone), d.mapManager.queryInStorage(at = Coordinates(2,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))

        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(2,0)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(1,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(2,2)))

        //Don't show these each round, until actually something goes to production
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(2,0)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(1,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Lumber), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Lumber), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Lumber), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(listOf(Lumber), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(listOf(Lumber), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(listOf(Lumber), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(6,2)))

        d.gameStateManager.tick()
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(3,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(5,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInTransport(at = Coordinates(7,1)))
        assertEquals(listOf(Lumber), d.mapManager.queryInTransport(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInStorage(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,0)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(7,1)))
        assertEquals(emptyList<Resource>(), d.mapManager.queryInProduction(at = Coordinates(6,2)))

        assertEquals(emptyList<Resource>(), d.mapManager.queryRequires(at = Coordinates(6,0)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(7,1)))
        assertEquals(listOf(Lumber, Lumber), d.mapManager.queryRequires(at = Coordinates(6,2)))
        //Some steps are skipped for brevity
    }

    @Test
    fun `transportation - checks the sorting order in which the requesting buildings get deliveries`() {
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

        //This tick represents the initial state setting in the real app on run, before the first
        // time is clicked on step
        d.gameStateManager.tick()

        //runConstruction via tick()
        for (x in 1 .. 8) {
            //sut.runProduction(cell)
            d.gameStateManager.tick()
        }
        assertEquals(listOf(Lumber), d.mapManager.queryInProduction(cFletcher2))
        assertEquals(listOf(Lumber, Stone), d.mapManager.queryRequires(cFletcher2))

        for (x in 1 .. 7) {
            //sut.runProduction(cell)
            d.gameStateManager.tick()
        }
        assertTrue(lumber.isConstructed())

        for (x in 1 .. 11) {
            //sut.runProduction(cell)
            d.gameStateManager.tick()
        }
        assertTrue(fletcher2.isConstructed())

        for (x in 1 .. 4) {
            //sut.runProduction(cell)
            d.gameStateManager.tick()
        }
        assertEquals(listOf(Wood), d.mapManager.queryRequires(cFletcher2))
        assertEquals(listOf(Wood), d.mapManager.queryInStorage(cFletcher2))

        d.gameStateManager.tick()
        d.gameStateManager.tick()
        assertEquals(listOf(Wood), d.mapManager.queryRequires(cFletcher2))
    }
}
