package com.example.settlers

import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger
import kotlinx.serialization.Serializable

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
            .filterForBuildingHasWorker()
            .filterForProductionStarted()
    }

    fun getCellsWhichShallRunAProduction(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForProductionBuildings()
            .filterForBuildingHasWorker()
            .filterForProductionNOTStarted()
            .filterForProductionNOTBlocked()
            .filterForAllProductionMaterialsAvailable()
            .filterForStorageNotFull()
    }

    fun getCellsWhichShallRunAProductionWithConsumingOutsideResources(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForBuildingHasWorker()
            .filterForStorageNotFull()
            .filterForOutsideResourcesConsumingProductionBuildings()
        //isWorldResourceInRange is not checked here, because the routing algirithm is not available to the MapManager
    }

    fun getCellsWhichShallRunAProductionWithProducingOutsideResources(): Map<Coordinates, Cell> {
        return getCellsWithBuildings()
            .filterForFinishedConstruction()
            .filterForBuildingHasWorker()
            .filterForOutsideResourcesCreatingProductionBuildings()
            //isSpaceAvailableForWorldResource is not checked here, because the routing algirithm is not available to the MapManager
    }

    fun getCellsWithMovingObjects(): Map<Coordinates, Cell> {
        return cells.filterValues { it.movingObject != null }
    }

    fun Map<Coordinates, Cell>.filterForFinishedConstruction(): Map<Coordinates, Cell> {
        return filterValues { it.building?.isConstructed() ?: false }
    }

    inline fun <reified T> getFinishedBuildingsOfType(): Map<Coordinates, Cell> {
        return getAllBuildingsOfType<T>().filterForFinishedConstruction()
    }

    inline fun <reified T> getFinishedBuildingsOfTypeCount(): Int {
        return getFinishedBuildingsOfType<T>().count()
    }

    inline fun <reified T> getUnfinishedBuildingsOfType(): Map<Coordinates, Cell> {
        return getAllBuildingsOfType<T>().filterForUnfinishedConstruction()
    }

    inline fun <reified T> getUnfinishedBuildingsOfTypeCount(): Int {
        return getUnfinishedBuildingsOfType<T>().count()
    }

    inline fun <reified T> Map<Coordinates, Cell>.filterForBuildingType(): Map<Coordinates, Cell> {
        return filterValues {
            it.building is T
        }
    }

    inline fun <reified T> getAllBuildingsOfType(): Map<Coordinates, Cell> {
        return getCellsWithBuildings().filterForBuildingType<T>()
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

    private fun Map<Coordinates, Cell>.filterForAllProductionMaterialsNOTAvailable(): Map<Coordinates, Cell> {
        return filterValues {
            isRequiredListInAvailableList(it.building!!.requiresProduction, it.production).not()
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

    private fun Map<Coordinates, Cell>.filterForBuildingHasWorker(): Map<Coordinates, Cell> {
        return filterValues { it.building!!.workerLivesAt != null }
    }

    private fun Map<Coordinates, Cell>.filterForBuildingHasNOWorker(): Map<Coordinates, Cell> {
        return filterValues { it.building!!.workerLivesAt == null }
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

    private fun Map<Coordinates, Cell>.filterForRequiresHousing(): Map<Coordinates, Cell> {
        return filterValues { it.building!!.housingLevel != null }
    }

    private fun Map<Coordinates, Cell>.filterForRequiresHousingWithLevel(level: Int): Map<Coordinates, Cell> {
        return filterValues { it.building!!.housingLevel == level }
    }

    private fun getCellsWhichRequireAHouse(): Map<Coordinates, Cell> {
        return getCellsWithBuildings().filterForFinishedConstruction().filterForRequiresHousing()
    }

    private fun getBuildingsWhichRequireAHouse(): List<Building> {
        return getCellsWhichRequireAHouse().map { it.value.building!! }
    }

    //TODO this is only used in tests at the moment. Obsolete?
    fun getBuildingsWithUnfulfilledHousing(): List<Building> {
        return getBuildingsWhichRequireAHouse().filter { it.workerLivesAt == null }
    }

    private fun getCellsWithUnfulfilledHousingWithLevel(level: Int): List<Cell> {
        return getCellsWhichRequireAHouse().filterForRequiresHousingWithLevel(level).filterForBuildingHasNOWorker().map { it.value }
    }

    fun getRequiredHousing(targetProduction: Coordinates): Int {
        val productionBuilding = queryBuilding(targetProduction)!!
        return getRequiredHousing(productionBuilding)
    }

    fun getRequiredHousing(targetProduction: Building): Int {
        return targetProduction.housingLevel!!
    }

    //TODO Move this to its own class?
    fun getHousingDemand(): HousingDemand {
        var lvl1 = 0
        var lvl2 = 0
        var lvl3 = 0
        var lvl4 = 0
        getCellsWhichRequireAHouse().forEach { (_, cell) ->
            when (cell.building!!.housingLevel) {
                1 -> lvl1++
                2 -> lvl2++
                3 -> lvl3++
                4 -> lvl4++
                null -> {}//nothing
                else -> if (BuildConfig.DEBUG) {
                    error("Assertion failed")
                }
            }
        }

        return HousingDemand(lvl1, lvl2, lvl3, lvl4)
    }

    fun queryHouseLuxuryDemand(coord3: Coordinates): List<Resource> {
        val house = queryBuilding(coord3)
        return if (house is House && house.isConstructed()) {
            house.requiresProduction
        } else {
            emptyList()
        }
    }

    fun getCompleteLuxuryDemand(): List<Resource> {
        val demand = mutableListOf<Resource>()
        getCellsWithBuildings().forEach { (coord, _) ->
            demand.addAll(queryHouseLuxuryDemand(coord))
        }
        return demand
    }

    fun getCellsWithHouses(): Map<Coordinates, Cell> {
        return getCellsWithBuildings().filterForBuildingType<House>()
    }

    fun getCellsWithFinishedHouses(): Map<Coordinates, Cell> {
        return getCellsWithHouses().filterForFinishedConstruction()
    }

    fun getCellsWithHousesWithoutARunningProductionAndNoMaterialsAvailable(): Map<Coordinates, Cell> {
        return getCellsWithFinishedHouses()
            .filterForProductionNOTStarted()
            .filterForAllProductionMaterialsNOTAvailable()
    }

    fun getCellsWithHousesWithoutARunningProductionAndMaterialsAvailable(): Map<Coordinates, Cell> {
        return getCellsWithFinishedHouses()
            .filterForProductionNOTStarted()
            .filterForAllProductionMaterialsAvailable()
    }

    fun removeHouseAssignmentsWithHouseAsBase(houseCell: Cell): Collection<GameState> {
        val states: MutableCollection<GameState> = mutableListOf()
        (houseCell.building as House).currentlyAssignedProductionBuildings.forEach {
            states.addAll(removeHouseAssignmentsWithProductionBuildingAsBase(findSpecificCell(it)!!))
        }
        return states
    }

    //This function has the production building as base cell
    private fun removeHouseAssignmentsWithProductionBuildingAsBase(productionCell: Cell): Collection<GameState> {
        //TODO Maybe split this into remove in the house AND remove in the destination
        val productionBuilding = queryBuilding(productionCell.coordinates)!!
        //remove worker from production building
        val coordinatesOfAssignedHouse: Coordinates = productionBuilding.workerLivesAt ?: return emptyList()

        return  mutableListOf(
            //rmeove worker from production building
            GameState(productionCell.coordinates, Operator.Remove, Type.ProductionAssignment, Assignment(coordinatesOfAssignedHouse)),
            //remove worker from house
            GameState(coordinatesOfAssignedHouse, Operator.Remove, Type.HouseAssignment, Assignment(productionCell.coordinates)),
        )
    }

    private fun setWorkerToUnfulfilled(production: Cell, house: Cell): Collection<GameState> {
        return  listOf(
            //assign to the production building
            GameState(production.coordinates, Operator.Set, Type.ProductionAssignment, Assignment(house.coordinates)),
            //assign to the house
            GameState(house.coordinates, Operator.Set, Type.HouseAssignment, Assignment(production.coordinates)),
        )
    }

    fun addLevel1HouseAssignmentsWithHouseAsBase(houseCell: Cell): Collection<GameState> {
        val house = queryBuilding(houseCell.coordinates) as House
        val availableHousing = house.currentHousingAvailable.lvl1
        val unfulfilled = getCellsWithUnfulfilledHousingWithLevel(1).toMutableList()
        return addHouseAssignmentsWithHouseAsBase(houseCell, unfulfilled, availableHousing)
    }

    fun addLevel2HouseAssignmentsWithHouseAsBase(houseCell: Cell): Collection<GameState> {
        val house = queryBuilding(houseCell.coordinates) as House
        val availableHousing = house.currentHousingAvailable.lvl2
        val unfulfilled = getCellsWithUnfulfilledHousingWithLevel(2).toMutableList()
        return addHouseAssignmentsWithHouseAsBase(houseCell, unfulfilled, availableHousing)
    }

    fun addLevel3HouseAssignmentsWithHouseAsBase(houseCell: Cell): Collection<GameState> {
        val house = queryBuilding(houseCell.coordinates) as House
        val availableHousing = house.currentHousingAvailable.lvl3
        val unfulfilled = getCellsWithUnfulfilledHousingWithLevel(3).toMutableList()
        return addHouseAssignmentsWithHouseAsBase(houseCell, unfulfilled, availableHousing)
    }

    private fun addHouseAssignmentsWithHouseAsBase(houseCell: Cell, unfulfilledProduction: MutableList<Cell>, availableHousing: Int): Collection<GameState> {
        val states: MutableCollection<GameState> = mutableListOf()
        var mutableAvailableHousing = availableHousing
        var availableProduction = unfulfilledProduction.count()
        while (mutableAvailableHousing > 0 && availableProduction > 0) {
            mutableAvailableHousing--
            availableProduction--
            states.addAll(setWorkerToUnfulfilled(unfulfilledProduction.removeFirst(), houseCell))
        }
        return states
    }

//    //TODO This is useless?
//    fun addHouseAssignmentsWithProductionBuildingAsBase(cell: Cell): Collection<GameState> {
//        //Find the required space of the production building
//        val building = queryBuilding(cell.coordinates)!!
//        val requiredHousingLevel = building.housingLevel!!
//        //Find a house with space (valid road can be omitted, because the house needs luxurys anyway)
//        //If null is returned there are no houses available
//        val coordinatesWithSpace: Coordinates = getHouseWithLevelAvailable(requiredHousingLevel) ?: return emptyList()
//
//        return  mutableListOf(
//            //assign to the production building
//            GameState(cell.coordinates, Operator.Set, Type.ProductionAssignment, Assignment(coordinatesWithSpace)),
//            //assign to the house
//            GameState(coordinatesWithSpace, Operator.Set, Type.HouseAssignment, Assignment(cell.coordinates)),
//        )
//    }

    //TODO obsolete? not used atm
    private fun getHouseWithLevelAvailable(level: Int): Coordinates? {
        return getCellsWithFinishedHouses().filter {
            when (level) {
                1 -> (it.value.building as House).currentHousingAvailable.lvl1 > 0
                2 -> (it.value.building as House).currentHousingAvailable.lvl2 > 0
                3 -> (it.value.building as House).currentHousingAvailable.lvl3 > 0
                4 -> (it.value.building as House).currentHousingAvailable.lvl4 > 0
                else -> false
            }

        }.keys.firstOrNull()
    }


//    fun areProductionRequirementsavailable(entry: Map.Entry<Coordinates, Cell>): List<GameState> {
//        Hier fehlt noch die Funktion, die speziell auf Map.Entry arbeitet.
//        Wahrscheinlich am besten direkt in "getCellsWithHousesWithoutARunningProduction" filtern?
//        Habe ich gemacht. Siehe getCellsWithHousesWithoutARunningProductionAndNoMaterialsAvailable
//    }
}

@Serializable
data class HousingDemand(var lvl1: Int, var lvl2: Int, var lvl3: Int, var lvl4: Int)

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