package com.example.settlers

import com.example.settlers.util.Logger

data class TransportRequest(val destination: Coordinates, val what: Resource)
data class TransportRoute(val destination: Coordinates, val what: Resource, val route: Route)

class TransportManager(
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

    //TODO remove duplication
    private fun handleRequestsInTransport(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        val closest = whereIsNextResourceInTransportWithAccess(request)
        if (closest != null) {
            val to = calcRouteFirstStep(
                from = closest,
                to = request.destination
            )?: return states

            val destinationCell = mapManager.findSpecificCell(to)!!
            if (destinationCell.transport.count() == 2) { return states } // Already fully occupies
            destinationCell.touched = true//TODO move this to GameSTateManager?

            states.add(GameState(closest, Operator.Remove, Type.Transport, request.what))
            states.add(GameState(to, Operator.Set, Type.Transport, request.what))

        }
        return states
    }

    //TODO remove duplication
    private fun handleRequestsInStorage(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        val closest2 = whereIsNextResourceInStorageWithAccess(request)
        if (closest2 != null) {
            val to = calcRouteFirstStep(
                from = closest2,
                to = request.destination
            )?: return states

            val destinationCell = mapManager.findSpecificCell(to)!!
            if (destinationCell.transport.count() == 2) { return states } // Already fully occupies
            destinationCell.touched = true//TODO move this to GameSTateManager?

            states.add(GameState(closest2, Operator.Remove, Type.Storage, request.what))
            states.add(GameState(to, Operator.Set, Type.Transport, request.what))
        }
        return states
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