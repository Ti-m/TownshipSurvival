package com.example.settlers.testdoubles

import com.example.settlers.Cell
import com.example.settlers.Coordinates
import com.example.settlers.GroundType
import com.example.settlers.MapManager
import com.example.settlers.util.DisabledLogger

class MapManagerTestData(
    cells: List<Cell> = listOf(
        Cell(coordinates = Coordinates(0,0),type = GroundType.Desert),
        Cell(coordinates = Coordinates(1,0),type = GroundType.Desert),
        Cell(coordinates = Coordinates(2,0),type = GroundType.Desert),
        Cell(coordinates = Coordinates(0,1),type = GroundType.Desert),
        Cell(coordinates = Coordinates(1,1),type = GroundType.Desert),
        Cell(coordinates = Coordinates(2,1),type = GroundType.Desert),
        Cell(coordinates = Coordinates(0,2),type = GroundType.Desert),
        Cell(coordinates = Coordinates(1,2),type = GroundType.Desert),
        Cell(coordinates = Coordinates(2,2),type = GroundType.Desert)
    ),
    log: DisabledLogger = DisabledLogger()
) : MapManager(cells, log)