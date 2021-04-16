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

    fun queryWorldResource(at: Coordinates): WorldResource? {
        return findSpecificCell(at)?.worldResource
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

    fun isWorldResource(at: Coordinates, resource: WorldResource): Boolean {
        return queryWorldResource(at) == resource
    }

    fun isWater(coords: Coordinates): Boolean {
        return findSpecificCell(coords)!!.type == GroundType.Water
    }

    fun isTouched(at: Coordinates): Boolean {
        //Default is true to ignore unavailable cells
        return findSpecificCell(at)?.touched ?: true
    }

    fun findSpecificCell(coordinates: Coordinates): Cell? {
        return try {
            cells.getValue(coordinates)
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun getCellsWhichRequireStuff(): Map<Coordinates, Cell> {
        return cells.filterValues { it.requires.count() > 0 }.toSortedMap(comparator = compareBy<Coordinates> { it.x }.thenBy { it.y })
    }

    fun getCellsWhichRequireStuffWhichIsNotInStorage(): Map<Coordinates, Cell> {
        return getCellsWhichRequireStuff()
            .filterValues { it.building?.stopDelivery == false }
            .filterValues { cell ->
                isRequiredListInAvailableList(cell.requires, cell.storage).not()
        }
    }

    fun resetTouched() {
        cells.forEach {
            it.value.touched = false
        }
    }

    fun getCellsWhichShallContinueAProduction(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForProductionBuildings()
            .filterForProductionStarted()
    }

    fun getCellsWhichShallRunAProduction(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForProductionBuildings()
            .filterForProductionNOTStarted()
            .filterForProductionNOTBlocked()
            .filterForAllProductionMaterialsAvailable()
            .filterForStorageNotFull()
    }

    fun getCellsWhichShallRunAProductionWithConsumingOutsideResources(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForStorageNotFull()
            .filterForOutsideResourcesConsumingProductionBuildings()
        //isWorldResourceInRange is not checked here, because the routing algirithm is not available to the MapManager
    }

    fun getCellsWhichShallRunAProductionWithProducingOutsideResources(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForOutsideResourcesCreatingProductionBuildings()
            //isSpaceAvailableForWorldResource is not checked here, because the routing algirithm is not available to the MapManager
    }

    fun getCellsWithMovingObjects(): Map<Coordinates, Cell> {
        return cells.filterValues { it.movingObject != null }
    }

    fun Map<Coordinates, Cell>.filterForFinishedConstruction(): Map<Coordinates, Cell> {
        return filterValues { it.building?.isConstructed() ?: false }
    }

    inline fun <reified T> getFinishedBuildingsOfType(type: T): Map<Coordinates, Cell> {
        return getAllBuildingsOfType(type).filterForFinishedConstruction()
    }

    inline fun <reified T> getFinishedBuildingsOfTypeCount(type: T): Int {
        return getFinishedBuildingsOfType(type).count()
    }

    inline fun <reified T> getUnfinishedBuildingsOfType(type: T): Map<Coordinates, Cell> {
        return getAllBuildingsOfType(type).filterForUnfinishedConstruction()
    }

    inline fun <reified T> getUnfinishedBuildingsOfTypeCount(type: T): Int {
        return getUnfinishedBuildingsOfType(type).count()
    }

    inline fun <reified T> Map<Coordinates, Cell>.filterForBuildingType(type: T): Map<Coordinates, Cell> {
        return filterValues { it.building is T }
    }

    inline fun <reified T> getAllBuildingsOfType(type: T): Map<Coordinates, Cell> {
        return getCellsWithBuildings().filterForBuildingType(type)
    }

    fun getCellsWithBuildings(): Map<Coordinates, Cell> {
        return cells.filterValues { it.building != null }
    }

    fun getCellsWhichShallRunAConstruction(): Map<Coordinates, Cell> {
        val buildings = getCellsWithBuildings().filterForUnfinishedConstruction()
        val ret = buildings.filterForAllConstructionMaterialsAvailable().toMutableMap()
        ret.putAll(buildings.filterForConstructionStarted())
        return ret
    }

    fun Map<Coordinates, Cell>.filterForUnfinishedConstruction(): Map<Coordinates, Cell> {
        return filterValues { it.building != null && !it.building!!.isConstructed() }
    }

    private fun Map<Coordinates, Cell>.filterForConstructionStarted(): Map<Coordinates, Cell> {
        return filterValues { it.building != null && it.building!!.isConstructionInProgress() }
    }

    private fun Map<Coordinates, Cell>.filterForAllConstructionMaterialsAvailable(): Map<Coordinates, Cell> {
        return filterValues {
            isRequiredListInAvailableList(it.building!!.requiresConstruction, it.production)
        }
    }

    private fun Map<Coordinates, Cell>.filterForAllProductionMaterialsAvailable(): Map<Coordinates, Cell> {
        return filterValues {
            isRequiredListInAvailableList(it.building!!.requiresProduction, it.production)
        }
    }

    private fun isRequiredListInAvailableList(requires: List<Resource>, hasAvailable: List<Resource>) : Boolean {
        val available = hasAvailable.toMutableList()
        return if (requires.count() > available.count()) {
            false
        } else {
            var allRequiredResourcesAvailable = true
            requires.forEach { resource ->
                if (!available.remove(resource)) {// returns false, if not in the list
                    allRequiredResourcesAvailable = false
                }
            }
            allRequiredResourcesAvailable
        }
    }

    private fun Map<Coordinates, Cell>.filterForStorageNotFull(): Map<Coordinates, Cell> {
        return filterValues { it.storage.count() < 3 }
    }

    private fun Map<Coordinates, Cell>.filterForProductionStarted(): Map<Coordinates, Cell> {
        return filterValues { it.building != null && it.building!!.isProductionInProgress() }
    }

    private fun Map<Coordinates, Cell>.filterForProductionNOTStarted(): Map<Coordinates, Cell> {
        return filterValues { it.building != null && it.building!!.isProductionInProgress().not() }
    }

    private fun Map<Coordinates, Cell>.filterForProductionNOTBlocked(): Map<Coordinates, Cell> {
        return filterValues { it.building != null && it.building!!.isProductionBlocked.not() }
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

    fun getCellsWhichNeedToUpdateProductionRequirements(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForRequiredIsEmpty()
            .filterForNotEverythingRequiredIsInProductionTransportOrStorage()
            //.filterForProductionStorageIsEmpty()
    }

    private fun Map<Coordinates, Cell>.filterForProductionBuildings(): Map<Coordinates, Cell> {
        return filterValues { it.building != null && it.building!!.isProductionBuilding() }
    }

    private fun Map<Coordinates, Cell>.filterForNotEverythingRequiredIsInProductionTransportOrStorage(): Map<Coordinates, Cell> {
        return filterValues {
            val available = mutableListOf<Resource>()
            available.addAll(it.production)
            available.addAll(it.transport)
            available.addAll(it.storage)

            isRequiredListInAvailableList(it.building!!.requiresProduction, available).not()
        }
    }

    private fun Map<Coordinates, Cell>.filterForRequiredIsEmpty(): Map<Coordinates, Cell> {
        return filterValues { it.requires.isEmpty() }
    }

    private fun Map<Coordinates, Cell>.filterForOutsideResourcesConsumingProductionBuildings(): Map<Coordinates, Cell> {
        return filterValues { it.building!!.isWorldResourceConsumingProductionBuilding() }
    }

    private fun Map<Coordinates, Cell>.filterForOutsideResourcesCreatingProductionBuildings(): Map<Coordinates, Cell> {
        return filterValues { it.building!!.isWorldResourceCreatingProductionBuilding() }
    }

    fun getHousingDemand(): HousingDemand {
        val buildings = getCellsWithBuildings().filterForFinishedConstruction()
        var lvl1 = 0
        var lvl2 = 0
        var lvl3 = 0
        buildings.forEach { (_, cell) ->
            when (cell.building!!.housingLevel) {
                1 -> lvl1++
                2 -> lvl2++
                3 -> lvl3++
                null -> {}//nothing
                else -> if (BuildConfig.DEBUG) {
                    error("Assertion failed")
                }
            }
        }

        return HousingDemand(lvl1, lvl2, lvl3)
    }
}

data class HousingDemand(val lvl1: Int, val lvl2: Int, val lvl3: Int)

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