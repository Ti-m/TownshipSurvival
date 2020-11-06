package com.example.settlers

import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger

open class GameStateManager(
    private val transportManager: TransportManager,
    private val mapManager: MapManager,
    private val log: Logger
) {
    fun tick() {
        //Is this to expensive to do the iteration here?
        mapManager.resetTouched()

        mapManager.getCellsWithMovingObjects().forEach { (_, cell) ->
            applyStates(destruction(cell))
        }
        mapManager.getCellsWithMovingObjects().forEach { (coordinates, _) ->
            applyStates(transportManager.move(coordinates))
        }
        //TODO applyStates(transportManager.runProduction())
        mapManager.getCellsWhichRequireStuff().forEach { (_, cell) ->
            applyStates(mapManager.convertStorageToProduction(cell))
        }
        mapManager.getCellsWhichRequireStuff().forEach { (_, cell) ->
            applyStates(mapManager.convertTransportToStorage(cell))
        }
        mapManager.getCellsWhichRequireStuffWhichIsNotInStorage().forEach { (_, cell) ->
            applyStates(transportManager.moveResources(cell))
        }

        mapManager.getCellsWhichShallRunAProduction().forEach { (_, cell) ->
            applyStates(transportManager.runProduction(cell))
        }

        mapManager.getCellsWhichShallRunAConstruction().forEach { (_, cell) ->
            mapManager.runConstruction(cell)
        }
        //applyStates(transportManager.convertStorageToProduction())
        //applyStates(transportManager.convertTransportToStorage())
        //applyStates(transportManager.moveResources())

    }

    private fun destruction(cell: Cell): Collection<GameState> {
        if (cell.movingObject is Zombie && cell.building != null) {
            return listOf(
                GameState(cell.coordinates, Operator.Remove, Type.Building, null),
                GameState(cell.coordinates, Operator.Remove, Type.MovingObject, null)
            )
        }
        return emptyList()
    }

    fun applyStates(newStates: Collection<GameState>) {
        newStates.forEach { state ->
            applyState(state)
        }
    }

    fun applyState(state: GameState){
        log.logi("GameStateManager", "apply State: $state")
        val selected = mapManager.findSpecificCell(state.coordinates)!!
        when (state.operator) {
            Operator.Set -> {
                when (state.type) {
                    Type.Transport -> {
                        val res = state.data as Resource
                        if (selected.transport.count() <= 2) {
                            selected.transport.add(res)
                        } else {
                            throw IllegalStateException("$state :: ${selected.transport}")
                        }
                        selected.redraw = true
                        selected.touched = true
                    }
                    Type.Storage -> {
                        selected.storage.add(state.data as Resource)
                        selected.redraw = true
                        selected.touched = true
                    }
                    Type.Building -> {
                        selected.building = state.data as Building
                        selected.redraw = true
                        selected.requires.clear()//Make sure old requests are deleted, in case of building replacement
                        selected.building!!.requires.forEach { needed ->
                            applyState(GameState(selected.coordinates, Operator.Set, Type.Required, needed))
//                            val transportRequest = TransportRequestNew(
//                                destination = selected.coordinates,
//                                what = needed
//                            )
//                            transportManager.request(transportRequest)
                        }
                        selected.building!!.offers.forEach { resource ->
                            applyState(GameState(selected.coordinates, Operator.Set, Type.Storage, resource))
                        }
                    }
                    Type.Required -> {
                        selected.requires.add(state.data as Resource)
                    }
                    Type.Production -> {
                        selected.production.add(state.data as Resource)
                    }
                    Type.MovingObject -> {
                        selected.movingObject = state.data as MovingObject
                        selected.redraw = true
                        selected.touched = true
                    }
                }
            }
            Operator.Remove -> {
                when (state.type) {
                    Type.Transport -> {
                        val res = state.data as Resource
                        if (!selected.transport.remove(res)) {
                            throw IllegalStateException()
                        }
                        selected.redraw = true
                    }
                    Type.Storage -> {
                        selected.storage.remove(state.data as Resource)
                    }
                    Type.Building -> {
                        selected.redraw = true
                        selected.requires.clear()
                        selected.building = null
                        selected.production.clear()
                        selected.storage.clear()
                        selected.transport.clear()
                    }
                    Type.Required -> selected.requires.remove(state.data as Resource)
                    Type.Production -> TODO()
                    Type.MovingObject -> {
                        selected.movingObject = null
                        selected.redraw = true
                    }
                }
            }
        }
    }
}

class GameStateManagerPreparedForTest(
    transportManager: TransportManager,
    mapManager: MapManager,
    log: Logger
) : GameStateManager(transportManager, mapManager, log) {
    constructor(transportManager: TransportManager, mapManager: MapManager) : this(transportManager, mapManager, DisabledLogger())
    constructor(mapManager: MapManager) : this(TransportManagerPreparedForTest(mapManager), mapManager)
    constructor() : this(MapManagerPreparedForTest())
}
