package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class NextItemWithAccessFinderTest {

    private lateinit var d: BasicTestDependencies

    @Before
    fun setup() {
        d = BasicTestDependencies()
    }

    @Test
    fun `findInStorage from same tile`() {
        d.gameStateManager.applyStates(listOf(
            GameState(d.coords, Operator.Set, Type.Storage, Wood),
            GameState(d.coords, Operator.Set, Type.Building, Road())
        ))
        d.mapManager.resetTouched()
        //The coordinates are irrelevant here
        val result = d.nextItemWithAccessFinder.findInStorage(TransportRequest(d.coords, Wood))

        assertEquals(d.coords, result)
    }

    @Test
    fun `findInStorage 1 available 1 away`() {
        val dest = Coordinates(2,0)
        d.gameStateManager.applyStates(listOf(
            GameState(d.coords, Operator.Set, Type.Storage, Wood),
            GameState(d.coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        d.mapManager.resetTouched()
        //The coordinates are irrelevant here
        val result = d.nextItemWithAccessFinder.findInStorage(TransportRequest(dest, Wood))

        assertEquals(d.coords, result)
    }

    @Test
    fun `findInStorage 1 available 2 away`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(available, Operator.Set, Type.Storage, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))
        d.mapManager.resetTouched()

        val foundAt = d.nextItemWithAccessFinder.findInStorage(TransportRequest(requester, Wood))

        assertEquals(available, foundAt)
    }

    @Test
    fun `findInStorage 2 available at 2 different tiles`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(available, Operator.Set, Type.Storage, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Storage, Wood),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))
        d.mapManager.resetTouched()

        val foundAt = d.nextItemWithAccessFinder.findInStorage(TransportRequest(requester, Wood))

        assertEquals(Coordinates(1, 1), foundAt)
    }

    @Test
    fun `findInTransport nothing available`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))

        val foundAt = d.nextItemWithAccessFinder.findInTransport(TransportRequest(requester, Wood))

        assertNull(foundAt)
    }

    @Test
    fun `findInTransport from same tile`() {
        d.gameStateManager.applyStates(listOf(
            GameState(d.coords, Operator.Set, Type.Transport, Wood),
            GameState(d.coords, Operator.Set, Type.Building, Road())
        ))
        d.mapManager.resetTouched()

        //The coordinates are irrelevant here
        val result = d.nextItemWithAccessFinder.findInTransport(TransportRequest(d.coords, Wood))

        assertEquals(d.coords, result)
    }

    @Test
    fun `findInTransport 1 available 1 away`() {
        val dest = Coordinates(2,0)
        d.gameStateManager.applyStates(listOf(
            GameState(d.coords, Operator.Set, Type.Transport, Wood),
            GameState(d.coords, Operator.Set, Type.Building, Road()),
            GameState(dest, Operator.Set, Type.Building, Road())
        ))
        d.mapManager.resetTouched()

        //The coordinates are irrelevant here
        val result = d.nextItemWithAccessFinder.findInTransport(TransportRequest(dest, Wood))

        assertEquals(d.coords, result)
    }

    @Test
    fun `findInTransport 2 available at 2 different tiles`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(available, Operator.Set, Type.Transport, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Transport, Wood),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))
        d.mapManager.resetTouched()

        val foundAt = d.nextItemWithAccessFinder.findInTransport(TransportRequest(requester, Wood))

        assertEquals(Coordinates(1, 1), foundAt)
    }
}