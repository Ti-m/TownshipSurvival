package com.example.settlers

import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger

data class TransportRequest(val destination: Coordinates, val what: Resource)
data class TransportRoute(val destination: Coordinates, val what: Resource, val route: Route)

open class TransportManager(
    private val mapManager: MapManager,
    private val routing: BreadthFirstSearchRouting,
    private val log: Logger
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
        whereIsNextResourceInTransportWithAccess(request)?.let { closest ->
            val to = validRouteNextStep(closest, request.destination)?: return states

            states.add(GameState(closest, Operator.Remove, Type.Transport, request.what))
            states.add(GameState(to, Operator.Set, Type.Transport, request.what))

        }
        return states
    }

    //Move from storage to transport
    private fun handleRequestsInStorage(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        whereIsNextResourceInStorageWithAccess(request)?.let { closest ->

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

    // only calls into routing
    fun whereIsNextResourceInStorageWithAccess(request: TransportRequest): Coordinates? {
        return routing.findNextItemWithAccessInStorage(request.destination, request.what)
    }

    // only calls into routing
    fun whereIsNextResourceInTransportWithAccess(request: TransportRequest): Coordinates? {
        return routing.findNextItemWithAccessInTransport(request.destination, request.what)
    }
//    private fun calcRoute(from: Coordinates, to: Coordinates, what: Resource) : TransportRoute {
//        val route = routing.calcRoute(from, to)
//        return TransportRoute(destination = to, what = what, route = route)
//    }

    // only calls into routing
    private fun findTargetForZombie(start: Coordinates): Coordinates? {
        return routing.findTargetForZombie(start)
    }

    fun move(start: Coordinates): List<GameState> {
        val target = findTargetForZombie(start) ?: return emptyList()
        val step = calcRouteFirstStep(from = start, to = target, ignoreObstacles = true) ?: return emptyList()
        return listOf(
            GameState(start, Operator.Remove, Type.MovingObject, Zombie),
            GameState(step, Operator.Set, Type.MovingObject, Zombie)
        )
    }

    fun shootWithTowerCalculatePath(start: Coordinates, range: Int): TargetCoordinates? {
        val destination = routing.findTargetForTower(start, range) ?: return null
        val path = routing.calcRoute(start, destination, ignoreObstacles = true)!!.steps //If there is a target, there should be a path in all cases.
        if (path.count() == 0) return null //Mob is inside Tower, to late for shooting
        path.removeLast()
        return TargetCoordinates(start, path, destination)
    }

    fun cellHasArrow(cell: Cell): Boolean {
        return cell.production.contains(Arrow)
    }

    private fun getCoordinatesForWorldResourceInRange(it: Cell, worldResource: WorldResource): Coordinates? {
        return routing.findWorldResourceNearby(it.coordinates, 3, worldResource)
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
}

class TransportManagerPreparedForTest(
    mapManager: MapManager,
    routing: BreadthFirstSearchRouting,
    log: DisabledLogger,
) : TransportManager(mapManager, routing, log) {
    constructor(mapManager: MapManager, routing: BreadthFirstSearchRouting) : this(
        mapManager,
        routing,
        DisabledLogger()
    )
    constructor(mapManager: MapManager) : this(
        mapManager,
        BreadthFirstSearchRouting(mapManager, HexagonNeighbourCalculator((mapManager))),
        DisabledLogger()
    )
    constructor() : this(MapManagerPreparedForTest())

}