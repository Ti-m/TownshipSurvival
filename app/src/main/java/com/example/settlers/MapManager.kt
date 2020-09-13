package com.example.settlers

import com.example.settlers.MainActivity.Companion.tileGridSize
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

    fun isBuilding(at: Coordinates): Boolean {
        //This is queried sometimes of the map, in case its calculating the neighbours of cells
        return queryBuilding(at) != null
    }

    fun isTouched(at: Coordinates): Boolean {
        return findSpecificCell(at)!!.touched
    }

    fun findSpecificCell(coordinates: Coordinates): Cell? {
        try {
            return cells.getValue(coordinates)
        } catch (e: NoSuchElementException) {
            return null
        }
    }

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
    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, destination: Coordinates, direction: Int, ignoreObstacles: Boolean, allowAnyBuilding: Boolean): Coordinates? {
        val doubleHeightDirections = arrayOf(
            arrayOf(+1, +1), arrayOf(-1, +1), arrayOf(-2, 0),
            arrayOf(-1, -1), arrayOf(+1, -1), arrayOf(+2, 0)
        )

        val dir = doubleHeightDirections[direction]
        val neighbour = Coordinates(coords.x + dir[0], coords.y + dir[1])
        if (neighbour.x < 0 || neighbour.y < 0) { return null }
        //TODO Add injectable tilesize to not need to comment in test :D
        if (neighbour.x > tileGridSize - 1 || neighbour.y > (tileGridSize - 1) / 2) { return null }
        //if (neighbour.x > 6 - 1 || neighbour.y > (6 - 1) / 2) { return null }
        if (neighbour == destination) { return neighbour }
        if (ignoreObstacles) { return neighbour }
        if (isRoad(neighbour) || allowAnyBuilding) {
        //if (queryBuilding(at = coords) != null) { //Refactor to roads
            return neighbour
        } else {
            return null
        }
    }

    //This only does a single step each tick
    //It looks better for animations and otherwise I need ti allocate a lot of memory to create
    // copys of the requires and transport lists
    fun convertTransportToStorage(cell: Cell): List<GameState> {
        val matched = mutableListOf<GameState>()
        cell.requires.forEach { required ->
            if (cell.transport.contains(required)) {
                matched.add(GameState(cell.coordinates, Operator.Set, Type.Storage, required))
                matched.add(GameState(cell.coordinates, Operator.Remove, Type.Transport, required))
                //matched.add(GameState(cell.key, Operator.Remove, Type.Required, required))
                return matched //Only do a single loop
            }
        }
        return matched
    }

    fun getCellsWhichRequireStuff(): Map<Coordinates, Cell> {
        return cells.filterValues { it.requires.count() > 0 }
    }

    fun getCellsWhichRequireStuffWhichIsNotInStorage(): Map<Coordinates, Cell> {
        return getCellsWhichRequireStuff().filterValues { cell ->
            var tmp = true
            cell.requires.forEach  { item ->
                //If the item is already in storage, it will be moved to production in next round
                if (cell.storage.contains(item)) {
                    tmp = false
                }
            }
            tmp
        }
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