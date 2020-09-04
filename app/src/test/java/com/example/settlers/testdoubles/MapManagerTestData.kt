package com.example.settlers.testdoubles

import com.example.settlers.Cell
import com.example.settlers.Coordinates
import com.example.settlers.GroundType
import com.example.settlers.MapManager
import com.example.settlers.util.DisabledLogger

class MapManagerTestData(
    cells: Map<Coordinates, Cell> = mapOf(
        Pair(Coordinates(0,0), Cell(coordinates = Coordinates(0,0),type = GroundType.Desert)),
        Pair(Coordinates(2,0), Cell(coordinates = Coordinates(2,0),type = GroundType.Desert)),
        Pair(Coordinates(4,0), Cell(coordinates = Coordinates(4,0),type = GroundType.Desert)),
        Pair(Coordinates(1,1), Cell(coordinates = Coordinates(1,1),type = GroundType.Desert)),
        Pair(Coordinates(3,1), Cell(coordinates = Coordinates(3,1),type = GroundType.Desert)),
        Pair(Coordinates(5,1), Cell(coordinates = Coordinates(5,1),type = GroundType.Desert)),
        Pair(Coordinates(0,2), Cell(coordinates = Coordinates(0,2),type = GroundType.Desert)),
        Pair(Coordinates(2,2), Cell(coordinates = Coordinates(2,2),type = GroundType.Desert)),
        Pair(Coordinates(4,2), Cell(coordinates = Coordinates(4,2),type = GroundType.Desert))
    ),
    log: DisabledLogger = DisabledLogger()
) : MapManager(cells, log)