package com.example.settlers

import android.util.Log
import com.example.settlers.util.Logger

enum class Command { SetResource, RemoveResource, SetResourceOffered, RemoveResourceOffered }
data class GameState(val coordinates: Coordinates, val command: Command, val what: Resource)

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
            new.add(
                GameState(
                    coordinates = it.route.current,
                    command = Command.RemoveResource,
                    what = it.what
                )
            )
            new.add(
                GameState(
                    coordinates = step,
                    command = Command.SetResource,
                    what = it.what
                )
            )
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
        pendingTransports.forEach {
            log.logi("TransportManagerNew", "createActiveTransports $it")
            val coordinates = mapManager.whereIsResourceOfferedAt(what = it.what)
            if (coordinates != null) {
                new.add(
                    GameState(
                        coordinates = coordinates,
                        command = Command.RemoveResourceOffered,
                        what = it.what
                    )
                )
                new.add(
                    GameState(
                        coordinates = coordinates,
                        command = Command.SetResource,
                        what = it.what
                    )
                )
                val route = calcRoute(from = coordinates, to = it.destination, what = it.what)
                activeTransports.add(route)
                //TODO at the route elements here to the Transport. Create TransportNew class?
                markForRemovel.add(it)
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