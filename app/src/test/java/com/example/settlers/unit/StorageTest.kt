package com.example.settlers.unit

import com.example.settlers.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class StorageTest {
    private lateinit var d: BasicTestDependencies

    @Before
    fun setup() {
        d = BasicTestDependencies()
    }

    @Test
    fun `check for saved after each tick`() {

    }

    @Test
    fun `findTargetForTower, no target`() {

        val string = CellSerializer.serializeCells(mapOf( //4x4 grid
            Pair(Coordinates(0,0), Cell(coordinates = Coordinates(0,0), type = GroundType.Desert)),
            Pair(Coordinates(2,0), Cell(coordinates = Coordinates(2,0),type = GroundType.Desert)),
            Pair(Coordinates(4,0), Cell(coordinates = Coordinates(4,0),type = GroundType.Desert)),
            Pair(Coordinates(6,0), Cell(coordinates = Coordinates(6,0),type = GroundType.Desert)),
            Pair(Coordinates(1,1), Cell(coordinates = Coordinates(1,1),type = GroundType.Desert)),
            Pair(Coordinates(3,1), Cell(coordinates = Coordinates(3,1),type = GroundType.Desert)),
            Pair(Coordinates(5,1), Cell(coordinates = Coordinates(5,1),type = GroundType.Desert)),
            Pair(Coordinates(7,1), Cell(coordinates = Coordinates(7,1),type = GroundType.Desert)),
            Pair(Coordinates(0,2), Cell(coordinates = Coordinates(0,2),type = GroundType.Desert)),
            Pair(Coordinates(2,2), Cell(coordinates = Coordinates(2,2),type = GroundType.Desert)),
            Pair(Coordinates(4,2), Cell(coordinates = Coordinates(4,2),type = GroundType.Desert)),
            Pair(Coordinates(6,2), Cell(coordinates = Coordinates(6,2),type = GroundType.Desert)),
            Pair(Coordinates(1,3), Cell(coordinates = Coordinates(1,3),type = GroundType.Desert)),
            Pair(Coordinates(3,3), Cell(coordinates = Coordinates(3,3),type = GroundType.Desert)),
            Pair(Coordinates(5,3), Cell(coordinates = Coordinates(5,3),type = GroundType.Desert)),
            Pair(Coordinates(7,3), Cell(coordinates = Coordinates(7,3),type = GroundType.Desert)),
        ))

        Assert.assertEquals(
            "[{\"x\":0,\"y\":0},{\"coordinates\":{\"x\":0,\"y\":0},\"type\":\"Desert\"},{\"x\":2,\"y\":0},{\"coordinates\":{\"x\":2,\"y\":0},\"type\":\"Desert\"},{\"x\":4,\"y\":0},{\"coordinates\":{\"x\":4,\"y\":0},\"type\":\"Desert\"},{\"x\":6,\"y\":0},{\"coordinates\":{\"x\":6,\"y\":0},\"type\":\"Desert\"},{\"x\":1,\"y\":1},{\"coordinates\":{\"x\":1,\"y\":1},\"type\":\"Desert\"},{\"x\":3,\"y\":1},{\"coordinates\":{\"x\":3,\"y\":1},\"type\":\"Desert\"},{\"x\":5,\"y\":1},{\"coordinates\":{\"x\":5,\"y\":1},\"type\":\"Desert\"},{\"x\":7,\"y\":1},{\"coordinates\":{\"x\":7,\"y\":1},\"type\":\"Desert\"},{\"x\":0,\"y\":2},{\"coordinates\":{\"x\":0,\"y\":2},\"type\":\"Desert\"},{\"x\":2,\"y\":2},{\"coordinates\":{\"x\":2,\"y\":2},\"type\":\"Desert\"},{\"x\":4,\"y\":2},{\"coordinates\":{\"x\":4,\"y\":2},\"type\":\"Desert\"},{\"x\":6,\"y\":2},{\"coordinates\":{\"x\":6,\"y\":2},\"type\":\"Desert\"},{\"x\":1,\"y\":3},{\"coordinates\":{\"x\":1,\"y\":3},\"type\":\"Desert\"},{\"x\":3,\"y\":3},{\"coordinates\":{\"x\":3,\"y\":3},\"type\":\"Desert\"},{\"x\":5,\"y\":3},{\"coordinates\":{\"x\":5,\"y\":3},\"type\":\"Desert\"},{\"x\":7,\"y\":3},{\"coordinates\":{\"x\":7,\"y\":3},\"type\":\"Desert\"}]"
            , string
        )
    }
}