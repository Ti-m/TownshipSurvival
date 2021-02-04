package com.example.settlers


data class Route(
    var current: Coordinates,//TDO this is really needed?
    val steps: MutableList<Coordinates>
)

class BreadthFirstSearchRouting(
    private val mapManager: MapManager,
    private val neighbourCalculator: HexagonNeighbourCalculator
) {

    fun calcRoute(start: Coordinates, destiantion: Coordinates, ignoreObstacles: Boolean = false): Route? {
        val frontier = mutableListOf(start)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            if (current == destiantion) {
                break
            }

            neighbourCalculator.getNeighboursOfCellDoubleCoords(
                coords = current,
                destination = destiantion,
                ignoreObstacles = ignoreObstacles
            ).forEach { next ->
                if (!cameFrom.containsKey(next)) {
                    frontier.add(next)
                    cameFrom[next] = current
                }
            }
        }

        var current = destiantion
        val path = mutableListOf<Coordinates>()
        while (current != start) {
            path.add(current)

            val tmp = cameFrom[current]
            if (tmp == null) {
                return null
            } else {
                current = tmp
            }
        }
        path.add(start)

        val list = path.reversed().toMutableList()
        val first = list.removeFirst()
        return Route(first, list)
    }

    fun calcRouteFirstStep(start: Coordinates, destiantion: Coordinates, ignoreObstacles: Boolean = false): Coordinates? {
        //Return First Step. Drop all other steps
        return try {
            calcRoute(start, destiantion, ignoreObstacles)?.steps?.first()
        } catch (e: NoSuchElementException) {
            null
        }
    }
}

class NextItemWithAccessFinder(
    private val mapManager: MapManager,
    neighbourCalculator: HexagonNeighbourCalculator
) : BaseFinder(neighbourCalculator) {

    override val doRangeCheck: Boolean = false

    fun findInTransport(request: TransportRequest): Coordinates? {
        return find(start = request.destination, what = request.what, type = Type.Transport)
    }

    fun findInStorage(request: TransportRequest): Coordinates? {
        return find(start = request.destination, what = request.what, type = Type.Storage)
    }

    override fun selector(
        current: Coordinates,
        worldResource: WorldResource?,
        type: Type?,
        what: Resource?
    ): Boolean {
        if (type == Type.Transport) {
            if (mapManager.queryInTransport(current).contains(what)) {
                if (!mapManager.isTouched(current)) {
                    return true
                }
            }
        } else if (type == Type.Storage) {
            if (mapManager.queryInStorage(current).contains(what)) {
                if (!mapManager.isTouched(current)) {
                    return true
                }
            }
        }
        return false
    }
}

abstract class BaseFinder(
    private val neighbourCalculator: HexagonNeighbourCalculator
) {
    /**
     * @param range: Search only in this range. Only used in some cases.
     * @param worldResource: Search for a WorldResource. Only used in some cases.
     * @param type: Search for which transport type. Only used in some cases.
     * @param what: Search for which Resource. Only used in some cases.
     */
    fun find(
        start: Coordinates,
        range: Int = -1,
        worldResource: WorldResource? = null,
        type: Type? = null,
        what: Resource? = null
    ): Coordinates? {
        val frontier = mutableListOf(start)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (frontier.isNotEmpty()) {
            val current = frontier.removeFirst()
            if (doRangeCheck) {
                if (DoubleCoordsDistance.distance(start, current) > range) return null
            }

            if (selector(current, worldResource, type, what)) {
                return current
            }

            neighbourCalculator.getNeighboursOfCellDoubleCoords(
                coords = current,
                ignoreObstacles = true,
                allowAnyBuilding = true
            ).forEach { next ->
                if (!cameFrom.containsKey(next)) {
                    frontier.add(next)
                    cameFrom[next] = current
                }
            }
        }
        return null
    }

    /*
    * @param current: Search from these coordinates
    * @param worldResource: Search for a WorldResource. Only used in some cases.
    * @param type: Search for which transport type. Only used in some cases.
    * @param what: Search for which Resource. Only used in some cases.
    * */
    abstract fun selector(
        current: Coordinates,
        worldResource: WorldResource?,
        type: Type?,
        what: Resource?
    ): Boolean

    // override to deactivate range check
    open val doRangeCheck: Boolean = true
}

class ZombieTargetFinder(
    private val mapManager: MapManager,
    neighbourCalculator: HexagonNeighbourCalculator
) : BaseFinder(neighbourCalculator) {

    override val doRangeCheck: Boolean = false

    override fun selector(
        current: Coordinates,
        worldResource: WorldResource?,
        type: Type?,
        what: Resource?
    ): Boolean {
        if (mapManager.isBuilding(current)) {
            val building = mapManager.findSpecificCell(current)!!.building
            if (building !is Spawner && building !is Road) {
                if (!mapManager.isTouched(current)) {//TODO ignore touched?
                    return true
                }
            }
        }
        return false
    }
}

class TowerTargetFinder(
    private val mapManager: MapManager,
    neighbourCalculator: HexagonNeighbourCalculator
) : BaseFinder(neighbourCalculator) {

    override fun selector(
        current: Coordinates,
        worldResource: WorldResource?,
        type: Type?,
        what: Resource?
    ): Boolean {
        return mapManager.isMovingObject(current)
    }
}

class NearbyWorldResourceFinder(
    private val mapManager: MapManager,
    neighbourCalculator: HexagonNeighbourCalculator
) : BaseFinder(neighbourCalculator) {

    override fun selector(
        current: Coordinates,
        worldResource: WorldResource?,
        type: Type?,
        what: Resource?
    ): Boolean {
        return mapManager.isWorldResource(current, worldResource!!)
    }
}

class EmptyCellFinder(
    private val mapManager: MapManager,
    neighbourCalculator: HexagonNeighbourCalculator
) : BaseFinder(neighbourCalculator) {

    override fun selector(
        current: Coordinates,
        worldResource: WorldResource?,
        type: Type?,
        what: Resource?
    ): Boolean {
        return mapManager.queryWorldResource(current) == null && !mapManager.isBuilding(current)
    }
}