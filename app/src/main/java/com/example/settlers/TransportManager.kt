package com.example.settlers

import com.example.settlers.util.Logger

data class TransportRequestNew(val destination: Coordinates, val what: Resource)
data class TransportRoute(val destination: Coordinates, val what: Resource, val route: Route)

class TransportManager(
    private val mapManager: MapManager,
    private val routing: BreadthFirstSearchRouting,
    private val log: Logger
) {

    //Requested resource is not available, therefore, the transport is pending
   // private val pendingTransports: MutableList<TransportRequestNew> = mutableListOf()
    //Currently getting delivered
    //private val activeTransports: MutableList<TransportRoute> = mutableListOf()

//    fun request(request: TransportRequestNew) {
//        log.logi("TransportManagerNew", "request $request")
//        pendingTransports.add(request)
//    }

    fun tick(): List<GameState> {
        val states = mutableListOf<GameState>()
        states.addAll(runProduction())
        states.addAll(convertTransportToProduction())
        states.addAll(convertTransportToStorage())
        states.addAll(moveResources())
        //states.addAll(matchDemand()) //TODO Implement matchDemand which cancels a requested good with a resource available in field
        return states
    }

    private fun runProduction(): Collection<GameState> {
        return emptyList()
    }

    private fun convertTransportToProduction(): Collection<GameState> {
        return emptyList()
    }

    private fun convertTransportToStorage(): Collection<GameState> {
        return emptyList()
    }

    private fun moveResources(): Collection<GameState> {
        val requests: Collection<TransportRequestNew> = mapManager.getRequests()
        val states: Collection<GameState> = handleRequests(requests)
        return states
    }

    private fun handleRequests(requests: Collection<TransportRequestNew>): Collection<GameState> {
        val states: MutableList<GameState> = mutableListOf()
        requests.forEach { request ->
            //TODO Refactor to closest
//            val closest = mapManager.whereIsResourceOfferedAt(request)
//            if (closest != null) {
//                val to = calcRouteOneStep(
//                    from = closest,
//                    to = request.destination,
//                    what = request.what
//                ) //only next step
//                states.add(GameState(closest, Operator.Remove, Type.Offered, request.what))
//                states.add(GameState(to, Operator.Set, Type.Resource, request.what))//TODO The types diverge here. Do it better?
//            } else {
//                val closest2 = mapManager.whereIsResourceinTransportAt(request)
//                if (closest2 != null) {
//                    val to = calcRouteOneStep(
//                        from = closest2,
//                        to = request.destination,
//                        what = request.what
//                    ) //only next step
//                    states.add(GameState(closest2, Operator.Remove, Type.Resource, request.what))
//                    states.add(GameState(to, Operator.Set, Type.Resource, request.what))
//                }
//            }
            val closest = mapManager.whereIsResourceinTransportAt(request)
            if (closest != null) {
                val to = calcRouteOneStep(
                    from = closest,
                    to = request.destination,
                    what = request.what
                ) //only next step
                states.add(GameState(closest, Operator.Remove, Type.Resource, request.what))
                states.add(GameState(to, Operator.Set, Type.Resource, request.what))//TODO The types diverge here. Do it better?
            } else {
                val closest2 = mapManager.whereIsResourceOfferedAt(request)
                if (closest2 != null) {
                    val to = calcRouteOneStep(
                        from = closest2,
                        to = request.destination,
                        what = request.what
                    ) //only next step
                    states.add(GameState(closest2, Operator.Remove, Type.Offered, request.what))
                    states.add(GameState(to, Operator.Set, Type.Resource, request.what))
                }
            }
        }
        return states
    }

    private fun calcRouteOneStep(from: Coordinates, to: Coordinates, what: Resource): Coordinates {
        return routing.calcRouteNextStep(from, to)
    }

    private fun calcRoute(from: Coordinates, to: Coordinates, what: Resource) : TransportRoute {
        val route = routing.calcRoute(from, to)
        return TransportRoute(destination = to, what = what, route = route)
    }

}