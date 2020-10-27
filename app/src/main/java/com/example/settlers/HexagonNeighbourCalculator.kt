package com.example.settlers

enum class RoadConnections {
    NorthWest, North, NorthEast, SouthEast, South, SouthWest
}

class HexagonNeighbourCalculator(
    private val mapManager: MapManager
) {
    companion object {
        private val doubleHeightDirections = arrayOf(
            arrayOf(+1, +1),
            arrayOf(-1, +1),
            arrayOf(-2, 0),
            arrayOf(-1, -1),
            arrayOf(+1, -1),
            arrayOf(+2, 0)
        )
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, destination: Coordinates? = null, ignoreObstacles: Boolean = true, allowAnyBuilding: Boolean = false): List<Coordinates> {
        return listOfNotNull(
            getNeighboursOfCellDoubleCoords(coords, destination,0, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,1, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,2, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,3, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,4, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,5, ignoreObstacles, allowAnyBuilding)
        )
    }

    private fun getNeighboursOfCellDoubleCoords(
        coords: Coordinates,
        //destination is only used to determine if the neighbour is already the destination.
        // In the other usecases of the function, it can be set to null
        destination: Coordinates? = null,
        //which neighbour is checked in the call
        direction: Int,
        ignoreObstacles: Boolean,
        // allowAnyBuilding is used to find the resources for transport
        allowAnyBuilding: Boolean,
        //Used for evaluation of the road connections
        ignoreTouched: Boolean = false
    ): Coordinates? {

        val dir = doubleHeightDirections[direction]
        val neighbour = Coordinates(coords.x + dir[0], coords.y + dir[1])
        if (!ignoreTouched && mapManager.isTouched(neighbour)) return null
        if (neighbour.x < 0 || neighbour.y < 0) { return null }
        if (neighbour.x > mapManager.getMaxRow() || neighbour.y > mapManager.getMaxColumn()) { return null }
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
        return listOfNotNull(
            if (getNeighboursOfCellDoubleCoords(coords = coords, direction = 0, ignoreObstacles = false, allowAnyBuilding = true, ignoreTouched = true) != null) RoadConnections.SouthEast else null,
            if (getNeighboursOfCellDoubleCoords(coords = coords, direction = 1, ignoreObstacles = false, allowAnyBuilding = true, ignoreTouched = true) != null) RoadConnections.NorthEast else null,
            if (getNeighboursOfCellDoubleCoords(coords = coords, direction = 2, ignoreObstacles = false, allowAnyBuilding = true, ignoreTouched = true) != null) RoadConnections.North else null,
            if (getNeighboursOfCellDoubleCoords(coords = coords, direction = 3, ignoreObstacles = false, allowAnyBuilding = true, ignoreTouched = true) != null) RoadConnections.NorthWest else null,
            if (getNeighboursOfCellDoubleCoords(coords = coords, direction = 4, ignoreObstacles = false, allowAnyBuilding = true, ignoreTouched = true) != null) RoadConnections.SouthWest else null,
            if (getNeighboursOfCellDoubleCoords(coords = coords, direction = 5, ignoreObstacles = false, allowAnyBuilding = true, ignoreTouched = true) != null) RoadConnections.South else null
        )
    }
}