package com.example.settlers.unit

import com.example.settlers.Command
import com.example.settlers.Coordinates
import com.example.settlers.GameState
import com.example.settlers.Resource
import com.example.settlers.testdoubles.MapManagerTestData
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MapManagerTest {

    private lateinit var sut: MapManagerTestData
    private lateinit var coords: Coordinates

    @Before
    fun prepare() {
        sut = MapManagerTestData()
        coords = Coordinates(0,0)
    }

    @Test
    fun queryResourcesOffered() {
        sut.applyStates(listOf(GameState(coords, Command.SetResourceOffered, Resource.Wood)))
        val result = sut.queryResourcesOffered(coords)

        Assert.assertEquals(listOf(Resource.Wood), result)
    }

    @Test
    fun whereIsResourceOfferedAt() {
        sut.applyStates(listOf(GameState(coords, Command.SetResourceOffered, Resource.Wood)))
        val result = sut.whereIsResourceOfferedAt(Resource.Wood)

        Assert.assertEquals(coords, result)
    }

    @Test
    fun applyStates_SetRemoveResourceOffered() {
        //Set
        sut.applyStates(listOf(GameState(coords, Command.SetResourceOffered, Resource.Wood)))
        Assert.assertEquals(listOf(Resource.Wood), sut.queryResourcesOffered(coords))
        //Remove
        sut.applyStates(listOf(GameState(coords, Command.RemoveResourceOffered, Resource.Wood)))
        Assert.assertEquals(listOf<Resource>(), sut.queryResourcesOffered(coords))
    }

    @Test
    fun applyStates_SetRemoveResource() {
        //Set
        sut.applyStates(
            listOf(
                GameState(coords, Command.SetResource, Resource.Wood),
                GameState(coords, Command.SetResource, Resource.Wood)
            )
        )
        Assert.assertEquals(Resource.Wood, sut.queryResource1(coords))
        Assert.assertEquals(Resource.Wood, sut.queryResource2(coords))
        //Remove
        sut.applyStates(
            listOf(
                GameState(coords, Command.RemoveResource, Resource.Wood),
                GameState(coords, Command.RemoveResource, Resource.Wood)
            )
        )
        Assert.assertNull(sut.queryResource1(coords))
        Assert.assertNull(sut.queryResource2(coords))
    }

    @Test
    fun getNeighbourOfCell() {
        //TODO DOes this fit into mapmanager?
        val neighbours: List<Coordinates> = sut.getNeighboursOfCell(coords)
    }
}