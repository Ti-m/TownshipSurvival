package com.example.settlers

data class TransportRequest(val destination: Coordinates, val what: Resource)

open class TransportManager(
    private val mapManager: MapManager,
    private val routing: BreadthFirstSearchRouting,
    private val emptyCellFinder: EmptyCellFinder,
    private val nearbyWorldResourceFinder: NearbyWorldResourceFinder,
    private val towerTargetFinder: TowerTargetFinder,
    private val zombieTargetFinder: ZombieTargetFinder,
    private val nextItemWithAccessFinder: NextItemWithAccessFinder
) {
    fun moveResources(cell: Cell): Collection<GameState> {
        return handleRequests(TransportRequest(cell.coordinates, cell.requires.first()))
    }

    private fun handleRequests(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        states.addAll(handleRequestsInTransport(request))
        if (states.isEmpty()) {
            states.addAll(handleRequestsInStorage(request))
        }
        return states
    }

    private fun handleRequestsInTransport(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        nextItemWithAccessFinder.findInTransport(request)?.let { closest ->
            val to = validRouteNextStep(closest, request.destination)?: return states

            states.add(GameState(closest, Operator.Remove, Type.Transport, request.what))
            states.add(GameState(to, Operator.Set, Type.Transport, request.what))

        }
        return states
    }

    //Move from storage to transport
    private fun handleRequestsInStorage(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        nextItemWithAccessFinder.findInStorage(request)?.let { closest ->

            val destinationCell = mapManager.findSpecificCell(closest)!!
            if (destinationCell.transport.count() == 2) { return emptyList() }

            states.add(GameState(closest, Operator.Remove, Type.Storage, request.what))
            states.add(GameState(closest, Operator.Set, Type.Transport, request.what))
        }
        return states
    }

    private fun validRouteNextStep(from: Coordinates, to: Coordinates): Coordinates? {
        val step = calcRouteFirstStep(
            from = from,
            to = to
        )?: return null

        val destinationCell = mapManager.findSpecificCell(step)!!
        if (destinationCell.transport.count() == 2) { return null } // Already fully occupies
        return step
    }

    // only calls into routing
    private fun calcRouteFirstStep(from: Coordinates, to: Coordinates, ignoreObstacles: Boolean = false): Coordinates? {
        return routing.calcRouteFirstStep(from, to, ignoreObstacles)
    }

    fun move(start: Coordinates): List<GameState> {
        val target = zombieTargetFinder.find(start) ?: return emptyList()
        val step = calcRouteFirstStep(from = start, to = target, ignoreObstacles = true) ?: return emptyList()
        return listOf(
            GameStateCreator.removeZombie(start),
            GameStateCreator.createZombie(step)
        )
    }

    fun shootWithTowerCalculatePath(start: Coordinates, range: Int): TargetCoordinates? {
        val destination = towerTargetFinder.find(start, range) ?: return null
        val path = routing.calcRoute(start, destination, ignoreObstacles = true)!!.steps //If there is a target, there should be a path in all cases.
        if (path.count() == 0) return null //Mob is inside Tower, to late for shooting
        path.removeLast()
        return TargetCoordinates(start, path, destination)
    }

    fun cellHasArrow(cell: Cell): Boolean {
        return cell.production.contains(Arrow)
    }

    private fun getCoordinatesForWorldResourceInRange(it: Cell, worldResource: WorldResource): Coordinates? {
        return nearbyWorldResourceFinder.find(it.coordinates, 3, worldResource)
    }

    fun isWorldResourceInRange(it: Cell, worldResource: WorldResource): Boolean {
        return getCoordinatesForWorldResourceInRange(it, worldResource) != null
    }

    fun removeWorldResourceInRange(cell: Cell, worldResource: WorldResource): Collection<GameState> {
        getCoordinatesForWorldResourceInRange(cell, worldResource)?.let {
            return listOf(GameState(it, Operator.Remove, Type.WorldResource, worldResource))
        }
        return emptyList()
    }

    fun isSpaceAvailableForWorldResource(start: Coordinates): Boolean {
        return findEmptyCellInRange(start) != null
    }

    private val produceWorldResourceRange = 3

    private fun findEmptyCellInRange(start: Coordinates): Coordinates? {
        return emptyCellFinder.find(start, produceWorldResourceRange)
    }

    fun addWorldResourceInRange(start: Coordinates, produceCreatesWorldResource: WorldResource): Collection<GameState> {
        return listOf(GameState(emptyCellFinder.find(start, produceWorldResourceRange)!!, Operator.Set, Type.WorldResource, produceCreatesWorldResource))
    }

    fun nextSpawnerLocation(southEastEdge: Coordinates): Coordinates? {
        if (mapManager.isBuilding(southEastEdge)) {
            return emptyCellFinder.find(start = southEastEdge, range = 10)
        }
        return southEastEdge
    }
}