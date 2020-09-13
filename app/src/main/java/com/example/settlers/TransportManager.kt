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

    private fun handleRequestsInTransport(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        val closest = mapManager.whereIsResourceinTransportAt(request)
        if (closest != null) {
            val to = calcRouteOneStep(
                from = closest,
                to = request.destination,
                what = request.what
            )?: return states
            val destinationCell = mapManager.findSpecificCell(to)!!
            if (destinationCell.transport.count() == 2) { return states } // Already fully occupies
            //if (destinationCell.touched) { return states }
//            val sourceCell = mapManager.findSpecificCell(closest)!!
//            if (sourceCell.touched) { return states }
            destinationCell.touched = true//TODO move this to GameSTateManager?
            //sourceCell.touched = true

            states.add(GameState(closest, Operator.Remove, Type.Transport, request.what))
            states.add(GameState(to, Operator.Set, Type.Transport, request.what))

        }
        return states
    }

    private fun handleRequestsInStorage(request: TransportRequest): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        val closest2 = mapManager.whereIsResourceOfferedAt(request)
        if (closest2 != null) {
            val to = calcRouteOneStep(
                from = closest2,
                to = request.destination,
                what = request.what
            )?: return states
            val destinationCell = mapManager.findSpecificCell(to)!!
            if (destinationCell.transport.count() == 2) { return states } // Already fully occupies
            //if (destinationCell.touched) { return states }
//            val sourceCell = mapManager.findSpecificCell(closest2)!!
//            if (sourceCell.touched) { return states }
            //sourceCell.touched = true
            destinationCell.touched = true//TODO move this to GameSTateManager?

            states.add(GameState(closest2, Operator.Remove, Type.Storage, request.what))
            states.add(GameState(to, Operator.Set, Type.Transport, request.what))
        }
        return states
    }

    private fun calcRouteOneStep(from: Coordinates, to: Coordinates, what: Resource): Coordinates? {
        return routing.calcRouteNextStep(from, to)
    }

//    private fun calcRoute(from: Coordinates, to: Coordinates, what: Resource) : TransportRoute {
//        val route = routing.calcRoute(from, to)
//        return TransportRoute(destination = to, what = what, route = route)
//    }

}