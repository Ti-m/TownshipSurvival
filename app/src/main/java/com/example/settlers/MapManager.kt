package com.example.settlers

import com.example.settlers.util.Logger

open class MapManager(
    private val cells: Map<Coordinates, Cell>,
    private val log: Logger
) {

    fun applyStates(newStates: List<GameState>) {
        newStates.forEach { state ->
            log.logi("MapManager", "apply State: $state")
            val selected = findSpecificCell(state.coordinates)
            when (state.operator) {
                Operator.Set -> {
                    when (state.type) {
                        Type.Resource -> {
                            val res = state.data as Resource
                            if (selected.resource1 == null) {
                                selected.resource1 = res
                            } else if (selected.resource2 == null) {
                                selected.resource2 = res
                            } else {
                                throw IllegalStateException()
                            }
                            selected.redraw = true
                        }
                        Type.Offered -> {
                            selected.offers.add(state.data as Resource)
                        }
                        Type.Building -> TODO()
                    }
                }
                Operator.Remove -> {
                    when (state.type) {
                        Type.Resource -> {
                            val res = state.data as Resource
                            if (selected.resource2 == res) {
                                selected.resource2 = null
                            } else if (selected.resource1 == res) {
                                selected.resource1 = null
                            } else {
                                throw IllegalStateException()
                            }
                            selected.redraw = true
                        }
                        Type.Offered -> {
                            selected.offers.remove(state.data as Resource)
                        }
                        Type.Building -> TODO()
                    }
                }
            }
        }
    }

    fun queryResourcesOffered(at: Coordinates): List<Resource> {
        return findSpecificCell(at).offers
    }

    fun queryResource1(at: Coordinates): Resource? {
        return findSpecificCell(at).resource1
    }

    fun queryResource2(at: Coordinates): Resource? {
        return findSpecificCell(at).resource2
    }

    fun queryBuilding(at: Coordinates): Building? {
        return findSpecificCell(at).building
    }

    fun whereIsResourceOfferedAt(what: Resource): Coordinates? {
        //TODO this search is kind of brute force
        return try {
            cells.entries.first { it.value.offers.contains(what) }.key
        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun findSpecificCell(coordinates: Coordinates): Cell {
        //Should never throw a NoSuchElementException, because every selected cell needs to be
        // on the map
        return cells.getValue(coordinates)
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, ignoreObstacles: Boolean = true): List<Coordinates> {
        return listOf(
            getNeighboursOfCellDoubleCoords(coords,0, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,1, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,2, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,3, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,4, ignoreObstacles),
            getNeighboursOfCellDoubleCoords(coords,5, ignoreObstacles)
        ).filterNotNull()
    }

    fun getNeighboursOfCellDoubleCoords(coords: Coordinates, direction: Int, ignoreObstacles: Boolean): Coordinates? {
        val doubleHeightDirections = arrayOf(
            arrayOf(+1, +1), arrayOf(-1, +1), arrayOf(-2, 0),
            arrayOf(-1, -1), arrayOf(+1, -1), arrayOf(+2, 0)
        )

        val dir = doubleHeightDirections[direction]
        val neighbour = Coordinates(coords.x + dir[0], coords.y + dir[1])
        if (ignoreObstacles) { return neighbour }
//        if (queryBuilding(at = coords) != BuildingType.Road) { //Refactor to roads
        if (queryBuilding(at = neighbour) == null) {
            return null
        } else {
            return neighbour
        }
    }
}