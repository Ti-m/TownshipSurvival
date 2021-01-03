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

    fun queryAnimation(at: Coordinates): Animation? {
        return findSpecificCell(at)?.animation
    }

    fun isRoad(at: Coordinates): Boolean {
        //This is queried sometimes of the map, in case its calculating the neighbours of cells
        return queryBuilding(at) is Road
    }

    fun isBuilding(at: Coordinates): Boolean {
        //This is queried sometimes of the map, in case its calculating the neighbours of cells
        return queryBuilding(at) != null
    }

    fun isMovingObject(at: Coordinates): Boolean {
        return findSpecificCell(at)?.movingObject != null
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
        return getCellsWithBuildings().filterForFinishedConstruction()
    }

    fun getCellsWithMovingObjects(): Map<Coordinates, Cell> {
        return cells.filterValues { it.movingObject != null }
    }

    private fun Map<Coordinates, Cell>.filterForFinishedConstruction(): Map<Coordinates, Cell> {
        return filterValues { it.building?.isConstructed() ?: false }
    }

    fun getCellsWithBuildings(): Map<Coordinates, Cell> {
        return cells.filterValues { it.building != null }
    }

    fun getCellsWhichShallRunAConstruction(): Map<Coordinates, Cell> {
        return getCellsWithBuildings().filterForUnfinishedConstruction().filterForAllConstructionMaterialsAvailable()
    }

    private fun Map<Coordinates, Cell>.filterForUnfinishedConstruction(): Map<Coordinates, Cell> {
        return filterValues { it.building != null && !it.building!!.isConstructed() }
    }

    private fun Map<Coordinates, Cell>.filterForAllConstructionMaterialsAvailable(): Map<Coordinates, Cell> {
        return filterValues {
            val requires = it.building!!.requires.toMutableList()
            val production = it.production.toMutableList()
            //If the list size is not the same, the element is missing materials
            if (requires.count() != production.count()) {
                false
            } else {
                var allRequiredResourcesAvailable = true
                requires.forEach { resource ->
                    if (!production.remove(resource)) {// returns false, if not in the list
                        allRequiredResourcesAvailable = false
                    }
                }
                allRequiredResourcesAvailable
            }
        }
    }

    fun runConstruction(cell: Cell) : Collection<GameState> {
        if (cell.building!!.construct()) {
            return removeItemsFromProduction(cell)
        }
        return emptyList()
    }

    private fun removeItemsFromProduction(cell: Cell) : Collection<GameState> {
        return cell.building!!.requires.map {
            GameState(coordinates = cell.coordinates, operator = Operator.Remove, type = Type.Production, data = it)
        }
    }

    //https://www.redblobgames.com/grids/hexagons/#coordinates-doubled
    //Columns are simply incremented with double coordinates
    fun getMaxColumn() = mapsize - 1
    //The row index is doubled for each consecutive row
    fun getMaxRow(): Int {
        if (getMaxColumn() % 2 == 0) {
           return (mapsize - 1)  * 2
        } else {
           return mapsize * 2 - 1
        }
    }

    fun getSouthEastEdge(): Coordinates {
        return Coordinates(getMaxRow(), getMaxColumn())
    }

    fun getCellsWhichShallRunAnAnimation(): Map<Coordinates, Cell> {
        return cells.filterValues { it.animation != null }
    }

    fun getCellsWithTowers(): Map<Coordinates, Cell> {
        return getCellsWithBuildings().filterValues { it.building is Tower }
    }

    fun getCellsWithFinishedTowers(): Map<Coordinates, Cell> {
        return getCellsWithTowers().filterForFinishedConstruction()
    }
}

class MapManagerPreparedForTest(
    cells: Map<Coordinates, Cell>,
    log: Logger,
    mapSize: Int
) : MapManager(cells, log, mapSize) {

    constructor(cells: Map<Coordinates, Cell>, mapSize: Int) : this(cells, DisabledLogger(), mapSize)
    constructor(log: Logger) : this( mapOf( //4x4 grid
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
            Pair(Coordinates(7,3), Cell(coordinates = Coordinates(7,3),type = GroundType.Desert)),
        ),
        log,
        4
    )
    constructor() : this(DisabledLogger())
}