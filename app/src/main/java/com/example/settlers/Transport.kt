package com.example.settlers

import android.util.Log

class TransportManager(private val cells: List<Cell>) {
    //Currently getting delivered
    private val activeTransports: MutableList<Transport> = mutableListOf()
    //Requested ressource is not available, therefore, the transport is pending
    private val pendingTransports: MutableList<TransportRequest> = mutableListOf()

    fun requestTransport(request: TransportRequest) {
        pendingTransports.add(request)
    }

    private fun calcRoute(start: Coordinates, end: Coordinates, route: MutableList<Coordinates>) {
        if (start.y < end.y) {
            val coords = Coordinates(start.x, start.y + 1)
//            if (cells.first ({it.coordinates == coords}.building != BuildingType.Road ))
            route.add(coords)
            calcRoute(coords, end, route)
        } else if (start.y > end.y) {
            val coords = Coordinates(start.x, start.y - 1)
            route.add(coords)
            calcRoute(coords, end, route)
        } else if (start.x < end.x) {
            val coords = Coordinates(start.x + 1, start.y)
            route.add(coords)
            calcRoute(coords, end, route)
        } else if (start.x > end.x) {
            val coords = Coordinates(start.x - 1, start.y)
            route.add(coords)
            calcRoute(coords, end, route)
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
            val new = Transport(request.what)
            new.route.add(startCoordinates.coordinates)
            calcRoute(startCoordinates.coordinates,request.destination, new.route)
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

        if (cellStart.ressource1 == transport.what) {
            if (cellEnd.ressource1 == null) {
                cellEnd.ressource1 = transport.what
                cellStart.ressource1 = null
                transport.route.removeAt(0)
            } else if (cellEnd.ressource2 == null) {
                cellEnd.ressource2 = transport.what
                cellStart.ressource1 = null
                transport.route.removeAt(0)
            }
        } else if (cellStart.ressource2 == transport.what) {
            if (cellEnd.ressource1 == null) {
                cellEnd.ressource1 = transport.what
                cellStart.ressource2 = null
                transport.route.removeAt(0)
            } else if (cellEnd.ressource2 == null) {
                cellEnd.ressource2 = transport.what
                cellStart.ressource1 = null
                transport.route.removeAt(0)
            }
        } else if (cellStart.building!!.requested.contains(transport.what)) {
            if (cellStart.ressource1 == null) {
                cellStart.building!!.requested.remove(transport.what)
                cellStart.ressource1 = transport.what
            } else if (cellEnd.ressource2 == null) {
                cellStart.building!!.requested.remove(transport.what)
                cellStart.ressource2 = transport.what
            }
        }

        if (transport.route.count() == 1) {
            transport.route.clear()
        }
        cellStart.redraw = true
        cellEnd.redraw = true
    }
}

data class Transport(val what: Ressource) {
    val route: MutableList<Coordinates> = mutableListOf()
}

data class TransportRequest(val destination: Coordinates, val what: Ressource, var markForRemovel: Boolean = false)