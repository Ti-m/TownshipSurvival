package com.example.settlers

import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger

open class GameStateManager(
    private val transportManager: TransportManager,
    private val mapManager: MapManager,
    private val log: Logger
) {
    fun tick() {
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
        //applyStates(transportManager.convertStorageToProduction())
        //applyStates(transportManager.convertTransportToStorage())
        //applyStates(transportManager.moveResources())

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
                    Type.Building -> TODO()
                    Type.Required -> selected.requires.remove(state.data as Resource)
                    Type.Production -> TODO()
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

object GameStateCreator {
    fun L3_T3_unfinishedRoad(): List<GameState> {
        return listOf(
            GameState(Coordinates(2,0), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(1,1), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(2,2), Operator.Set, Type.Building, Townhall()),
            GameState(Coordinates(5,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(7,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(9,1), Operator.Set, Type.Building, Road()),
            GameState(Coordinates(9,1), Operator.Set, Type.Building, Lumberjack()),
            GameState(Coordinates(8,0), Operator.Set, Type.Building, Lumberjack()),
            GameState(Coordinates(8,2), Operator.Set, Type.Building, Lumberjack()),
        )
    }
}
