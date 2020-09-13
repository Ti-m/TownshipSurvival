package com.example.settlers

import com.example.settlers.util.Logger

open class MapManager(
    private val cells: Map<Coordinates, Cell>,
    private val log: Logger
) {
    //This should be never called with coordinates outside of the map
    fun queryInStorage(at: Coordinates): List<Resource> {
        return findSpecificCell(at)!!.storage
    }

    //This should be never called with coordinates outside of the map
    fun queryInTransport(at: Coordinates): List<Resource> {
        return findSpecificCell(at)!!.transport
    }

    //This should be never called with coordinates outside of the map
    fun queryInProduction(at: Coordinates): List<Resource> {
        return findSpecificCell(at)!!.production
    }

    fun queryBuilding(at: Coordinates): Building? {
        //This is queried sometimes of the map, in case its calculating the neighbours of cells
        return findSpecificCell(at)?.building
    }

    fun isRoad(at: Coordinates): Boolean {
        //This is queried sometimes of the map, in case its calculating the neighbours of cells
        return queryBuilding(at) is Road
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

    fun findSpecificCell(coordinates: Coordinates): Cell? {
        try {
            return cells.getValue(coordinates)
        } catch (e: NoSuchElementException) {
            return null
        }
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, destination: Coordinates, ignoreObstacles: Boolean = true): List<Coordinates> {
        return listOf(
            getNeighboursOfCellDoubleCoords(coords, destination,0, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords, destination,1, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords, destination,2, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords, destination,3, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords, destination,4, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords, destination,5, ignoreObstacles)
        ).filterNotNull()
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, destination: Coordinates, direction: Int, ignoreObstacles: Boolean): Coordinates? {
        val doubleHeightDirections = arrayOf(
            arrayOf(+1, +1), arrayOf(-1, +1), arrayOf(-2, 0),
            arrayOf(-1, -1), arrayOf(+1, -1), arrayOf(+2, 0)
        )

        val dir = doubleHeightDirections[direction]
        val neighbour = Coordinates(coords.x + dir[0], coords.y + dir[1])
        if (neighbour == destination) { return neighbour }
        if (ignoreObstacles) { return neighbour }
        if (isRoad(neighbour)) {
        //if (queryBuilding(at = coords) != null) { //Refactor to roads
            return neighbour
        } else {
            return null
        }
    }

    fun getRequests(): Collection<TransportRequestNew> {
        val requests = mutableListOf<TransportRequestNew>()
        cells.forEach { cell ->
            cell.value.requires.forEach { item ->
                //If the item is already in storage, it will be moved to production in next round
                if (!cell.value.storage.contains(item)) {
                    requests.add(TransportRequestNew(cell.key, item))
                }
            }
        }
        return requests
    }

    //Figures out were a transportation is ready to move to storage
    //This only does a single step each tick
    //It looks better for animations and otherwise I need ti allocate a lot of memory to create
    // copys of the requires and transport lists
    fun matchTransportToStorage(): List<GameState> {
        val matched = mutableListOf<GameState>()
        getCellsWhichRequireStuff().forEach { cell ->
            cell.value.requires.forEach { required ->
                if (cell.value.transport.contains(required)) {
                    matched.add(GameState(cell.key, Operator.Set, Type.Storage, required))
                    matched.add(GameState(cell.key, Operator.Remove, Type.Transport, required))
                    //matched.add(GameState(cell.key, Operator.Remove, Type.Required, required))
                    return matched //Only do a single loop
                }
            }
        }
        return matched
    }

    fun getCellsWhichRequireStuff(): Map<Coordinates, Cell> {
        return cells.filterValues { it.requires.count() > 0 }
    }

    //This only does a single step each tick
    fun convertStorageToProduction(cell: Cell): List<GameState> {
        val matched = mutableListOf<GameState>()
        cell.requires.forEach { required ->
            if (cell.storage.contains(required)) {
                matched.add(GameState(cell.coordinates, Operator.Set, Type.Production, required))
                matched.add(GameState(cell.coordinates, Operator.Remove, Type.Storage, required))
                matched.add(GameState(cell.coordinates, Operator.Remove, Type.Required, required))
                return matched //Only do a single loop
            }
        }
        return matched
    }
}