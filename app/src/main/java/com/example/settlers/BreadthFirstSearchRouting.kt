package com.example.settlers


data class Route(
    var current: Coordinates,//TDO this is really needed?
    val steps: MutableList<Coordinates>
)

class BreadthFirstSearchRouting(
    private val mapManager: MapManager,
    private val neighbourCalculator: HexagonNeighbourCalculator
) {

    fun calcRoute(start: Coordinates, destiantion: Coordinates): Route? {
        val frontier = mutableListOf(start)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (current == destiantion) {
                break
            }

            neighbourCalculator.getNeighboursOfCellDoubleCoords(current, destiantion, false).forEach { next ->
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

    fun calcRouteFirstStep(start: Coordinates, destiantion: Coordinates): Coordinates? {
        //Return First Step. Drop all other steps
        return try {
            calcRoute(start, destiantion)?.steps?.first()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun findNextItemWithAccessInTransport(from: Coordinates, what: Resource): Coordinates? {
        return findNextItemWithAccessInner(from, what, Type.Transport)
    }

    fun findNextItemWithAccessInStorage(from: Coordinates, what: Resource): Coordinates? {
        return findNextItemWithAccessInner(from, what, Type.Storage)
    }

    //TODO is it really a good idea to use the Type enum here as parameter
    private fun findNextItemWithAccessInner(from: Coordinates, what: Resource, type: Type): Coordinates? {
        val frontier = mutableListOf(from)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (type == Type.Transport) {
                if (mapManager.queryInTransport(current).contains(what)) {
                    if (!mapManager.isTouched(current)) {
                        return current
                    }
                }
            } else if (type == Type.Storage) {
                if (mapManager.queryInStorage(current).contains(what)) {
                    if (!mapManager.isTouched(current)) {
                        return current
                    }
                }
            }

            neighbourCalculator.getNeighboursOfCellDoubleCoords(
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