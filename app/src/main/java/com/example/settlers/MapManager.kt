package com.example.settlers

import com.example.settlers.util.Logger

open class MapManager(
    private val cells: Map<Coordinates, Cell>,
    private val log: Logger
) {
    fun queryResourcesOffered(at: Coordinates): List<Resource> {
        return findSpecificCell(at).storage
    }

    fun queryResources(at: Coordinates): List<Resource> {
        return findSpecificCell(at).transport
    }

    fun queryBuilding(at: Coordinates): Building? {
        return findSpecificCell(at).building
    }

    //TODO refactor to closest from destination. search in spirals
    fun whereIsResourceOfferedAt(request: TransportRequestNew): Coordinates? {
        //TODO this search is kind of brute force
        return try {
            cells.entries.first { it.value.storage.contains(request.what) }.key
        } catch (e: NoSuchElementException) {
            null
        }
    }

    //TODO refactor to closest from destination. search in spirals
    fun whereIsResourceinTransportAt(request: TransportRequestNew): Coordinates? {
        //TODO this search is kind of brute force
        return try {
            cells.entries.first { it.value.transport.contains(request.what) }.key
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun findSpecificCell(coordinates: Coordinates): Cell {
        //Should never throw a NoSuchElementException, because every selected cell needs to be
        // on the map
        return cells.getValue(coordinates)
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, ignoreObstacles: Boolean = true): List<Coordinates> {
        return listOf(
            getNeighboursOfCellDoubleCoords(coords,0, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,1, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,2, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,3, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,4, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,5, ignoreObstacles)
        ).filterNotNull()
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, direction: Int, ignoreObstacles: Boolean): Coordinates? {
        val doubleHeightDirections = arrayOf(
            arrayOf(+1, +1), arrayOf(-1, +1), arrayOf(-2, 0),
            arrayOf(-1, -1), arrayOf(+1, -1), arrayOf(+2, 0)
        )

        val dir = doubleHeightDirections[direction]
        val neighbour = Coordinates(coords.x + dir[0], coords.y + dir[1])
        if (ignoreObstacles) { return neighbour }
//        if (queryBuilding(at = coords) != BuildingType.Road) { //Refactor to roads
        if (queryBuilding(at = neighbour) == null) {
            return null
        } else {
            return neighbour
        }
    }

    fun getRequests(): Collection<TransportRequestNew> {
        val requests = mutableListOf<TransportRequestNew>()
        cells.forEach { cell ->
            cell.value.requires.forEach { item ->
                requests.add(TransportRequestNew(cell.key, item))
            }
        }
        return requests
    }
}