package com.example.settlers

import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger

open class MapManager(
    protected val cells: Map<Coordinates, Cell>,
    private val log: Logger,
    val mapsize: Int
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

    //This should be never called with coordinates outside of the map
    fun queryRequires(at: Coordinates): List<Resource> {
        return findSpecificCell(at)!!.requires
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
        //Default is true to ignore unavailable cells
        return findSpecificCell(at)?.touched ?: true
    }

    fun findSpecificCell(coordinates: Coordinates): Cell? {
        try {
            return cells.getValue(coordinates)
        } catch (e: NoSuchElementException) {
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

    fun resetTouched() {
        cells.forEach {
            it.value.touched = false
        }
    }

    fun getCellsWhichShallRunAProduction(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
    }

    fun getCellsWithBuildings(): Map<Coordinates, Cell> {
        return cells.filterValues { it.building != null }
    }

    fun getCellsWhichShallRunAConstruction(): Map<Coordinates, Cell> {
        return getCellsWithBuildings().filterValues { !it.building!!.isConstructed() }
    }

    fun runConstruchtion(cell: Cell) {
        return cell.building!!.construct()
    }
}

class MapManagerPreparedForTest(
    cells: Map<Coordinates, Cell>,
    log: Logger,
    mapSize: Int
) : MapManager(cells, log, mapSize) {

    constructor(cells: Map<Coordinates, Cell>, mapSize: Int) : this(cells, DisabledLogger(), mapSize)
    constructor(log: Logger) : this( mapOf(
            Pair(Coordinates(0,0), Cell(coordinates = Coordinates(0,0),type = GroundType.Desert)),
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
            Pair(Coordinates(7,3), Cell(coordinates = Coordinates(5,3),type = GroundType.Desert)),
        ),
        log,
        8
    )
    constructor() : this(DisabledLogger())
}