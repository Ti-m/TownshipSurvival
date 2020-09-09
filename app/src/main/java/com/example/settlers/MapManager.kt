package com.example.settlers

import com.example.settlers.util.Logger

open class MapManager(
    private val cells: Map<Coordinates, Cell>,
    private val log: Logger
) {
    fun queryInStorage(at: Coordinates): List<Resource> {
        return findSpecificCell(at).storage
    }

    fun queryInTransport(at: Coordinates): List<Resource> {
        return findSpecificCell(at).transport
    }

    fun queryInProduction(at: Coordinates): List<Resource> {
        return findSpecificCell(at).production
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
    fun matchTansportToStorage(): List<GameState> {
        val matched = mutableListOf<GameState>()
        cells.filterValues { it.requires.count() > 0 }.forEach { cell ->
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

    //Figures out were an item in storage is ready to move to production
    //This only does a single step each tick
    fun matchStorageToProduction(): List<GameState> {
        val matched = mutableListOf<GameState>()
        cells.filterValues { it.requires.count() > 0 }.forEach { cell ->
            cell.value.requires.forEach { required ->
                if (cell.value.storage.contains(required)) {
                    matched.add(GameState(cell.key, Operator.Set, Type.Production, required))
                    matched.add(GameState(cell.key, Operator.Remove, Type.Storage, required))
                    matched.add(GameState(cell.key, Operator.Remove, Type.Required, required))
                    return matched //Only do a single loop
                }
            }
        }
        return matched
    }
}