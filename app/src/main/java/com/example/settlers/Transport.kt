package com.example.settlers


class Transport(val start: Coordinates, val end: Coordinates, val what: Ressource) {

    val route: MutableList<Coordinates> = mutableListOf<Coordinates>()
    init {
        route.add(start)
        calcRoute(start, route)
        //route.add(end)
    }

    private fun calcRoute(start: Coordinates, route: MutableList<Coordinates>) {
        if (start.y < end.y) {
            val coords = Coordinates(start.x, start.y + 1)
            route.add(coords)
            calcRoute(coords, route)
        } else if (start.y > end.y) {
            val coords = Coordinates(start.x, start.y - 1)
            route.add(coords)
            calcRoute(coords, route)
        } else if (start.x < end.x) {
            val coords = Coordinates(start.x + 1, start.y)
            route.add(coords)
            calcRoute(coords, route)
        } else if (start.x > end.x) {
            val coords = Coordinates(start.x - 1, start.y)
            route.add(coords)
            calcRoute(coords, route)
        }//TODO currently moves through obstacles
    }

    fun tick(cells: List<Cell>) {
        val start = route.get(0)
        val end = route.get(1)
        val cellStart = cells.first { it.coordinates == start }
        val cellEnd = cells.first { it.coordinates == end }

        if (cellStart.ressource1 == what) {
            if (cellEnd.ressource1 == null) {
                cellEnd.ressource1 = what
                cellStart.ressource1 = null
                route.removeAt(0)
            } else if (cellEnd.ressource2 == null) {
                cellEnd.ressource2 = what
                cellStart.ressource1 = null
                route.removeAt(0)
            }
        } else if (cellStart.ressource2 == what) {
            if (cellEnd.ressource1 == null) {
                cellEnd.ressource1 = what
                cellStart.ressource2 = null
                route.removeAt(0)
            } else if (cellEnd.ressource2 == null) {
                cellEnd.ressource2 = what
                cellStart.ressource1 = null
                route.removeAt(0)
            }
        } else if (cellStart.building!!.requested.contains(what)) {
            if (cellStart.ressource1 == null) {
                cellStart.building!!.requested.remove(what)
                cellStart.ressource1 = what
            } else if (cellEnd.ressource2 == null) {
                cellStart.building!!.requested.remove(what)
                cellStart.ressource2 = what
            }
        }

        if (route.count() == 1) {
            route.clear()
        }
        cellStart.redraw = true
        cellEnd.redraw = true
    }
}
