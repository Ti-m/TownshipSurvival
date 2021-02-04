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
    fun `findNextItemWithAccessInStorage 1 available 2 away`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(available, Operator.Set, Type.Storage, Wood),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))
        d.mapManager.resetTouched()

        val foundAt = d.nextItemWithAccessFinder.findInStorage(requester, Wood)

        assertEquals(available, foundAt)
    }

    @Test
    fun `findNextItemWithAccessInStorage 2 available at 2 different tiles`() {
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

        val foundAt = d.nextItemWithAccessFinder.findInStorage(requester, Wood)

        assertEquals(Coordinates(1, 1), foundAt)
    }

    @Test
    fun `findNextItemWithAccessInTransport nothing available`() {
        val available = Coordinates(0,0)
        val requester = Coordinates(2,2)
        d.gameStateManager.applyStates(listOf(
            GameState(available, Operator.Set, Type.Building, Road()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Road()),
            GameState(requester, Operator.Set, Type.Building, Lumberjack())
        ))

        val foundAt = d.nextItemWithAccessFinder.findInTransport(requester, Wood)

        assertNull(foundAt)
    }

    @Test
    fun `findNextItemWithAccessInTransport 2 available at 2 different tiles`() {
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

        val foundAt = d.nextItemWithAccessFinder.findInTransport(requester, Wood)

        assertEquals(Coordinates(1, 1), foundAt)
    }
}