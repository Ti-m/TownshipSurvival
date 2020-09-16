package com.example.settlers

class HexagonNeighbourCalculator(
    private val mapManager: MapManager
) {
    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, destination: Coordinates, ignoreObstacles: Boolean = true, allowAnyBuilding: Boolean = false): List<Coordinates> {
        return listOf(
            getNeighboursOfCellDoubleCoords(coords, destination,0, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,1, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,2, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,3, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,4, ignoreObstacles, allowAnyBuilding),
            getNeighboursOfCellDoubleCoords(coords, destination,5, ignoreObstacles, allowAnyBuilding)
        ).filterNotNull()
    }

    //TODO refactor this to use less flags ;-)
    // allowAnyBuilding is used to find the resources for transport
    private fun getNeighboursOfCellDoubleCoords(
        coords: Coordinates,
        destination: Coordinates,
        direction: Int,
        ignoreObstacles: Boolean,
        allowAnyBuilding: Boolean
    ): Coordinates? {
        val doubleHeightDirections = arrayOf(
            arrayOf(+1, +1), arrayOf(-1, +1), arrayOf(-2, 0),
            arrayOf(-1, -1), arrayOf(+1, -1), arrayOf(+2, 0)
        )

        val dir = doubleHeightDirections[direction]
        val neighbour = Coordinates(coords.x + dir[0], coords.y + dir[1])
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
}