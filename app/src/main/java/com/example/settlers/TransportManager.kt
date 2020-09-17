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

    fun runProduction(): Collection<GameState> {
        return emptyList()
    }

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

    private fun handleRequestsInStorage(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        whereIsNextResourceInStorageWithAccess(request)?.let { closest ->
            val to = validRouteNextStep(closest, request.destination)?: return states

            states.add(GameState(closest, Operator.Remove, Type.Storage, request.what))
            states.add(GameState(to, Operator.Set, Type.Transport, request.what))
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

    private fun calcRouteFirstStep(from: Coordinates, to: Coordinates): Coordinates? {
        return routing.calcRouteFirstStep(from, to)
    }

    fun whereIsNextResourceInStorageWithAccess(request: TransportRequest): Coordinates? {
        return routing.findNextItemWithAccessInStorage(request.destination, request.what)
    }

    fun whereIsNextResourceInTransportWithAccess(request: TransportRequest): Coordinates? {
        return routing.findNextItemWithAccessInTransport(request.destination, request.what)
    }
//    private fun calcRoute(from: Coordinates, to: Coordinates, what: Resource) : TransportRoute {
//        val route = routing.calcRoute(from, to)
//        return TransportRoute(destination = to, what = what, route = route)
//    }

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