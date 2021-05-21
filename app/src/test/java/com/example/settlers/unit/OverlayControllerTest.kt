package com.example.settlers.unit

import com.example.settlers.Coordinates
import com.example.settlers.OverlayController
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class OverlayControllerTest {

    private lateinit var sut: OverlayController

    @Before
    fun setup() {
        sut = OverlayController()
    }

    @Test
    fun `updateOverlay - single Element - is in list`() {
        val coords = Coordinates(1,1)

        sut.updateOverlay(coords)

        //Checks thats in the list
        assertTrue(sut.shallDrawOverlayForCoordinates(coords))
    }

    @Test
    fun `updateOverlay - single Element - not in list`() {
        val coords = Coordinates(1,1)
        val otherCoords = Coordinates(0,0)

        sut.updateOverlay(coords)

        //Checks thats in the list
        assertFalse(sut.shallDrawOverlayForCoordinates(otherCoords))
    }

    @Test
    fun `updateOverlay - multiple Elements - is in list`() {
        val coords = Coordinates(1,1)
        val otherCoords = Coordinates(0,0)

        sut.updateOverlay(listOf(coords, otherCoords))

        //Checks thats in the list
        assertTrue(sut.shallDrawOverlayForCoordinates(coords))
        assertTrue(sut.shallDrawOverlayForCoordinates(otherCoords))
    }

    @Test
    fun `updateOverlay - multiple Elements - only one is in list`() {
        val coords = Coordinates(1,1)
        val otherCoords = Coordinates(0,0)

        sut.updateOverlay(listOf(coords))

        //Checks thats in the list
        assertTrue(sut.shallDrawOverlayForCoordinates(coords))
        assertFalse(sut.shallDrawOverlayForCoordinates(otherCoords))
    }

    @Test
    fun clearOverlay() {
        val coords = Coordinates(1,1)

        sut.updateOverlay(coords)
        sut.clearOverlay()

        //Checks that the list is cleared
        assertFalse(sut.shallDrawOverlayForCoordinates(coords))
    }

}