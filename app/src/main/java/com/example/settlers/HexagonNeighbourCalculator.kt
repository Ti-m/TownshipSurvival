package com.example.settlers

enum class RoadConnections {
    NorthWest, North, NorthEast, East, SouthEast, South, SouthWest, West
}

class HexagonNeighbourCalculator(
    private val mapManager: MapManager
) {
    companion object {
        private val doubleHeightDirections = arrayOf(
            arrayOf(+1, +1), arrayOf(-1, +1), arrayOf(-2, 0),
            arrayOf(-1, -1), arrayOf(+1, -1), arrayOf(+2, 0)
        )
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, destination: Coordinates? = null, ignoreObstacles: Boolean = true, allowAnyBuilding: Boolean = false): List<Coordinates> {
        return listOf(
            getNeighboursOfCellDoubleCoords(coords, destination,0, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,1, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,2, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,3, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,4, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,5, ignoreObstacles, allowAnyBuilding)
        ).filterNotNull()
    }

    private fun getNeighboursOfCellDoubleCoords(
        coords: Coordinates,
        //destination is only used to determine if the neighbour is already the destination.
        // In the other usecases of the function, it can be set to null
        destination: Coordinates? = null,
        direction: Int,
        ignoreObstacles: Boolean,
        // allowAnyBuilding is used to find the resources for transport
        allowAnyBuilding: Boolean
    ): Coordinates? {

        val dir = doubleHeightDirections[direction]
        val neighbour = Coordinates(coords.x + dir[0], coords.y + dir[1])
        if (mapManager.isTouched(neighbour)) return null
        if (neighbour.x < 0 || neighbour.y < 0) { return null }
        if (neighbour.x > mapManager.mapsize - 1 || neighbour.y > (mapManager.mapsize - 1) / 2) { return null }
        if (neighbour == destination) { return neighbour }
        if (ignoreObstacles) { return neighbour }
        if (allowAnyBuilding && mapManager.isBuilding(neighbour)) { return neighbour }
        return if (mapManager.isRoad(neighbour)) {
            neighbour
        } else {
            null
        }
    }

    fun getRoadConnections(coords: Coordinates) : List<RoadConnections> {
        return listOf(
            RoadConnections.South,
            RoadConnections.SouthWest,
            RoadConnections.East,
            RoadConnections.North
        )
    }
}