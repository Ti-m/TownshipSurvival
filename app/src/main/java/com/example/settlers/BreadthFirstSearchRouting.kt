package com.example.settlers


data class Route(
    var current: Coordinates,//TDO this is really needed?
    val steps: MutableList<Coordinates>
)

class BreadthFirstSearchRouting(private val mapManager: MapManager) {

    fun calcRoute(start: Coordinates, destiantion: Coordinates): Route? {
        val frontier = mutableListOf(start)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (current == destiantion) {
                break
            }

            mapManager.getNeighboursOfCellDoubleCoords(current, destiantion, false).forEach { next ->
                if (!cameFrom.containsKey(next)) {
                    frontier.add(next)
                    cameFrom[next] = current
                }
            }
        }

        var current = destiantion
        val path = mutableListOf<Coordinates>()
        while (current != start) {
            path.add(current)

            val tmp = cameFrom[current]
            if (tmp == null) {
                return null
            } else {
                current = tmp
            }
        }
        path.add(start)

        val list = path.reversed().toMutableList()
        val first = list.removeFirst()
        return Route(first, list)
    }

    fun calcRouteNextStep(start: Coordinates, destiantion: Coordinates): Coordinates? {
        //Return First Step. Drop all other steps
       return calcRoute(start, destiantion)?.steps?.first()
    }

    fun calcRouteToItemInTransport(from: Coordinates, what: Resource): Coordinates? {
        val frontier = mutableListOf(from)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (mapManager.queryInTransport(current).contains(what)) {
                if (!mapManager.isTouched(current)) {
                    return current
                }
            }

            mapManager.getNeighboursOfCellDoubleCoords(
                current,
                Coordinates(1_000_000,1_000_000),//TODO unreachable, unused in this case
                false,
                true
            ).forEach { next ->
                if (!cameFrom.containsKey(next)) {
                    frontier.add(next)
                    cameFrom[next] = current
                }
            }
        }
        return null
    }

    fun calcRouteToItemInStorage(from: Coordinates, what: Resource): Coordinates? {
        val frontier = mutableListOf(from)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (mapManager.queryInStorage(current).contains(what)) {
                if (!mapManager.isTouched(current)) {
                    return current
                }
            }

            mapManager.getNeighboursOfCellDoubleCoords(
                current,
                Coordinates(1_000_000,1_000_000),//TODO unreachable, unused in this case
                false,
                true
            ).forEach { next ->
                if (!cameFrom.containsKey(next)) {
                    frontier.add(next)
                    cameFrom[next] = current
                }
            }
        }
        return null
    }
}