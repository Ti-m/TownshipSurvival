package com.example.settlers

import android.util.Log

enum class Command { SetResource, RemoveResource, SetResourceOffered, RemoveResourceOffered }
data class GameState(val coordinates: Coordinates, val command: Command, val what: Resource)

class TransportManagerNew(private val mapManager: MapManager) {

    //Requested resource is not available, therefore, the transport is pending
    private val pendingTransports: MutableList<TransportRequestNew> = mutableListOf()
    //Currently getting delivered
    private val activeTransports: MutableList<TransportRoute> = mutableListOf()

    fun request(request: TransportRequestNew) {
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
            val step = it.route.removeAt(0)
            new.add(
                GameState(
                    coordinates = step.start,
                    command = Command.RemoveResource,
                    what = it.what
                )
            )
            new.add(
                GameState(
                    coordinates = step.end,
                    command = Command.SetResource,
                    what = it.what
                )
            )
        }
        deleteFinishedTransports()
        return new
    }

    private fun deleteFinishedTransports() {
        activeTransports.removeIf { it.route.isEmpty() }
    }

    private fun createActiveTransports(): List<GameState> {
        val new = mutableListOf<GameState>()
        val markForRemovel = mutableListOf<TransportRequestNew>() //Prevent ConcurrentModificationException if the item is removed in the foreach
        pendingTransports.forEach {
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
        val route = mutableListOf<RouteElement>()
        //TODO THis moves a single tile to the right
        route.add(RouteElement(from, Coordinates(from.x + 1, from.y)))
        //and back
        //route.add(RouteElement(Coordinates(from.x + 1, from.y), from))

        //TODO This is tricky. Best would be something like a* algorithm is there something simpler?
//        if (isAllowed(from.north)) {
//
//        } else if (isAllowed(from.northEast))
//        if (from.y < to.y && isAllowed(Coordinates(start.x, start.y + 1), destination)) {//TODO Check for road
//            val coords = Coordinates(start.x, start.y + 1)
//            route.add(coords)
//            calcRoute(coords, end, destination, route)
//        } else if (start.y > end.y && isAllowed(Coordinates(start.x, start.y - 1), destination)) {
//            val coords = Coordinates(start.x, start.y - 1)
//            route.add(coords)
//            calcRoute(coords, end, destination, route)
//        } else if (start.x < end.x && isAllowed(Coordinates(start.x + 1, start.y), destination)) {
//            val coords = Coordinates(start.x + 1, start.y)
//            route.add(coords)
//            calcRoute(coords, end, destination, route)
//        } else if (start.x > end.x && isAllowed(Coordinates(start.x - 1, start.y), destination)) {
//            val coords = Coordinates(start.x - 1, start.y)
//            route.add(coords)
//            calcRoute(coords, end, destination, route)
//        }
        return TransportRoute(destination = to, what = what, route = route)
    }

}
/////////////////////////////////////////////////////////////////////////////////////////////
class TransportManager(private val cells: List<Cell>) { //TODO rely here on Cells is bad?
    //Currently getting delivered
    private val activeTransports: MutableList<Transport> = mutableListOf()
    //Requested ressource is not available, therefore, the transport is pending
    private val pendingTransports: MutableList<TransportRequest> = mutableListOf()

    fun requestTransport(request: TransportRequest) {
        pendingTransports.add(request)
    }

    private fun isRoad(coordinates: Coordinates): Boolean {
        return cells.first { coordinates == it.coordinates }.building?.type == BuildingType.Road
    }

    private fun isAllowed(coordinates: Coordinates, destination: Coordinates): Boolean {
        return coordinates == destination || isRoad(coordinates)
    }
//TODO das funktioniert so noch nicht komplett. Sollte eine Straße ein Graph sein?
    private fun calcRoute(start: Coordinates, end: Coordinates, destination: Coordinates, route: MutableList<Coordinates>) {
        if (start.y < end.y && isAllowed(Coordinates(start.x, start.y + 1), destination)) {//TODO Check for road
            val coords = Coordinates(start.x, start.y + 1)
            route.add(coords)
            calcRoute(coords, end, destination, route)
        } else if (start.y > end.y && isAllowed(Coordinates(start.x, start.y - 1), destination)) {
            val coords = Coordinates(start.x, start.y - 1)
            route.add(coords)
            calcRoute(coords, end, destination, route)
        } else if (start.x < end.x && isAllowed(Coordinates(start.x + 1, start.y), destination)) {
            val coords = Coordinates(start.x + 1, start.y)
            route.add(coords)
            calcRoute(coords, end, destination, route)
        } else if (start.x > end.x && isAllowed(Coordinates(start.x - 1, start.y), destination)) {
            val coords = Coordinates(start.x - 1, start.y)
            route.add(coords)
            calcRoute(coords, end, destination, route)
        }//TODO currently moves through obstacles
    }

    fun tick() {
        pendingTransports.forEach {
            tickForPending(it)
        }
        pendingTransports.removeIf { it.markForRemovel }

        activeTransports.removeIf { it.route.count() < 2 }
        activeTransports.forEach {
            tickForActive(it)
        }
    }
    private fun tickForPending(request: TransportRequest) {
        try {
            val startCoordinates = cells.first { it.building?.offers?.contains(request.what) ?: false }
            startCoordinates.building!!.markRequested(request.what)
            val new = Transport(request.destination, request.what)
            new.route.add(startCoordinates.coordinates)
            calcRoute(startCoordinates.coordinates,request.destination, request.destination, new.route)
            activeTransports.add(new)
//            pendingTransports.remove(request)
            request.markForRemovel = true
        } catch  (e: NoSuchElementException) {
            Log.i("TransportManager", "Not enough ressources")
        }
    }

    private fun tickForActive(transport: Transport) {
        val start = transport.route.get(0)
        val end = transport.route.get(1)
        val cellStart = cells.first { it.coordinates == start }
        val cellEnd = cells.first { it.coordinates == end }

        if (cellStart.resource1 == transport.what) {
            if (cellEnd.resource1 == null) {
                cellEnd.resource1 = transport.what
                cellStart.resource1 = null
                transport.route.removeAt(0)
            } else if (cellEnd.resource2 == null) {
                cellEnd.resource2 = transport.what
                cellStart.resource1 = null
                transport.route.removeAt(0)
            }
        } else if (cellStart.resource2 == transport.what) {
            if (cellEnd.resource1 == null) {
                cellEnd.resource1 = transport.what
                cellStart.resource2 = null
                transport.route.removeAt(0)
            } else if (cellEnd.resource2 == null) {
                cellEnd.resource2 = transport.what
                cellStart.resource1 = null
                transport.route.removeAt(0)
            }
        } else if (cellStart.building!!.requested.contains(transport.what)) {
            if (cellStart.resource1 == null) {
                cellStart.building!!.requested.remove(transport.what)
                cellStart.resource1 = transport.what
            } else if (cellEnd.resource2 == null) {
                cellStart.building!!.requested.remove(transport.what)
                cellStart.resource2 = transport.what
            }
        }

        if (transport.route.count() == 1) {
            transport.route.clear()
        }
        cellStart.redraw = true
        cellEnd.redraw = true
    }
}

data class Transport(val destination: Coordinates, val what: Resource) {
    val route: MutableList<Coordinates> = mutableListOf()
}

data class TransportRequest(val destination: Coordinates, val what: Resource, var markForRemovel: Boolean = false)
data class TransportRequestNew(val destination: Coordinates, val what: Resource)

data class TransportRequestInner(val destination: Coordinates, val what: Resource, var markForRemovel: Boolean = false)
data class TransportRoute(val destination: Coordinates, val what: Resource, val route: MutableList<RouteElement> = mutableListOf())
data class RouteElement(val start: Coordinates, val end: Coordinates)