package com.example.settlers

open class MapManager (private val cells: List<Cell>) {

    fun applyStates(newStates: List<GameState>) {
        newStates.forEach { state ->
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
                }
                Command.RemoveResource -> {
                    if (selected.resource2 == state.what) {
                        selected.resource2 = null
                    } else if (selected.resource1 == state.what) {
                        selected.resource1 = null
                    } else {
                        throw IllegalStateException()
                    }
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

    fun getNeighboursOfCell(coords: Coordinates): List<Coordinates> {
        NotImplementedError()
        return emptyList()
    }
}