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
    private val pendingTransports: MutableList<TransportRequestNew> = mutableListOf()
    //Currently getting delivered
    private val activeTransports: MutableList<TransportRoute> = mutableListOf()

    fun request(request: TransportRequestNew) {
        log.logi("TransportManagerNew", "request $request")
        pendingTransports.add(request)
    }

    fun tick(): List<GameState> {
        val states = mutableListOf<GameState>()
        states.addAll(createActiveTransports())
        states.addAll(moveActiveTransports())
        return states
    }

    private fun moveActiveTransports(): List<GameState> {
        val new = mutableListOf<GameState>()

        activeTransports.forEach {
            log.logi("TransportManagerNew", "moveActiveTransports $it")
            val step = it.route.steps.removeAt(0)
            if (it.what == Wood) {
                new.add(GameState(it.route.current, Operator.Remove, Type.Resource, Wood))
                new.add(GameState(step, Operator.Set, Type.Resource, Wood))
            } else if (it.what == Stone) {
                new.add(GameState(it.route.current, Operator.Remove, Type.Resource, Stone))
                new.add(GameState(step, Operator.Set, Type.Resource, Stone))
            }
            it.route.current = step
        }
        deleteFinishedTransports()
        return new
    }

    private fun deleteFinishedTransports() {
        activeTransports.removeIf { it.route.steps.isEmpty() }
    }

    private fun createActiveTransports(): List<GameState> {
        val new = mutableListOf<GameState>()
        val markForRemovel = mutableListOf<TransportRequestNew>() //Prevent ConcurrentModificationException if the item is removed in the foreach
        pendingTransports.forEach { request ->
            log.logi("TransportManagerNew", "createActiveTransports $request")
            mapManager.whereIsResourceOfferedAt(what = request.what)?.let { coords ->
                if (request.what == Wood) {
                    new.add(GameState(coords, Operator.Remove, Type.Offered, Wood))
                    new.add(GameState(coords, Operator.Set, Type.Resource, Wood))
                } else  if (request.what == Stone) {
                    new.add(GameState(coords, Operator.Remove, Type.Offered, Stone))
                    new.add(GameState(coords, Operator.Set, Type.Resource, Stone))
                }

                val route = calcRoute(from = coords, to = request.destination, what = request.what)
                activeTransports.add(route)

                markForRemovel.add(request)
            }
        }
        pendingTransports.removeAll(markForRemovel)//Remove newly active transports
        return new
    }

    private fun calcRoute(from: Coordinates, to: Coordinates, what: Resource) : TransportRoute {
        val route = routing.calcRoute(from, to)
        return TransportRoute(destination = to, what = what, route = route)
    }

}