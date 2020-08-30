package com.example.settlers

import android.util.Log

open class MapManager (private val cells: List<Cell>) {

    fun applyStates(newStates: List<GameState>) {
        newStates.forEach { state ->
            Log.i("MapManager", "apply State: $state")
            val selected = findSpecificCell(state.coordinates)
            when (state.command) {
                Command.SetResource -> {
                    if (selected.resource1 == null) {
                        selected.resource1 = state.what
                    } else if (selected.resource2 == null) {
                        selected.resource2 = state.what
                    } else {
                        throw IllegalStateException()
                    }
                    selected.redraw = true
                }
                Command.RemoveResource -> {
                    if (selected.resource2 == state.what) {
                        selected.resource2 = null
                    } else if (selected.resource1 == state.what) {
                        selected.resource1 = null
                    } else {
                        throw IllegalStateException()
                    }
                    selected.redraw = true
                }
                Command.SetResourceOffered -> {
                    selected.offers.add(state.what)
                }
                Command.RemoveResourceOffered -> {
                    selected.offers.remove(state.what)
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

    fun whereIsResourceOfferedAt(what: Resource): Coordinates? {
        //TODO this search is kind of brute force
        return try {
            cells.first { it.offers.contains(what) }.coordinates
        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun findSpecificCell(coordinates: Coordinates): Cell {
        //Should never throw a NoSuchElementException, because every selected cell needs to be
        // on the map
        //TODO this search is kind of brute force
        return cells.first { it.coordinates == coordinates }
    }
    fun getNeighboursOfCellEvenQ(coords: Coordinates): List<Coordinates> {
        return listOf(
            getNeighboursOfCellEvenQ(coords,0),
            getNeighboursOfCellEvenQ(coords,1),
            getNeighboursOfCellEvenQ(coords,2),
            getNeighboursOfCellEvenQ(coords,3),
            getNeighboursOfCellEvenQ(coords,4),
            getNeighboursOfCellEvenQ(coords,5)
        )
    }
    fun getNeighboursOfCellEvenQ(coords: Coordinates, direction: Int): Coordinates {
        val evenq_directions = arrayOf(
            arrayOf(
                arrayOf(+1, +1), arrayOf(+1,  0), arrayOf( 0, -1),
                arrayOf(-1,  0), arrayOf(-1, +1), arrayOf( 0, +1)
            ),
            arrayOf(
                arrayOf(+1,  0), arrayOf(+1, -1), arrayOf( 0, -1),
                arrayOf(-1, -1), arrayOf(-1,  0), arrayOf( 0, +1)
            )
        )
        val parity = coords.x and 1
        val dir = evenq_directions[parity][direction]
        return Coordinates(coords.x + dir[0], coords.y + dir[1])
    }

//    fun getCubeCoordsFromEvenQ(coords: Coordinates): CubeCoords {
//        val x = coords.x
//        val z = hex
//        return CubeCoords(0,0,0)
//    }
}

data class CubeCoords(val x: Int, val y: Int, val z: Int)