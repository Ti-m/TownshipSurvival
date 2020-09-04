package com.example.settlers


data class Route(
    var current: Coordinates,//TDO this is really needed?
    val steps: MutableList<Coordinates>
)

class BreadthFirstSearchRouting(private val mapManager: MapManager) {

    fun calcRoute(start: Coordinates, destiantion: Coordinates): Route {
        val frontier = mutableListOf(start)
        val cameFrom = mutableMapOf<Coordinates, Coordinates>()

        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (current == destiantion) {
                break
            }
            mapManager.getNeighboursOfCellDoubleCoords(current).forEach { next ->
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
            current = cameFrom[current]!!
        }
        path.add(start)

        val list = path.reversed().toMutableList()
        val first = list.removeFirst()
        return Route(first, list)
    }
}