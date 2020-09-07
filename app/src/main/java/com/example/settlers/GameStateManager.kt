package com.example.settlers

import com.example.settlers.util.Logger


class GameStateManager(
    //private val transportManager: TransportManager,
    private val mapManager: MapManager,
    private val log: Logger
) {

    //TODO Add overload without List
    fun applyStates(newStates: List<GameState>) {
        newStates.forEach { state ->
            applyState(state)
        }
    }

    fun applyState(state: GameState){
        log.logi("GameStateManager", "apply State: $state")
        val selected = mapManager.findSpecificCell(state.coordinates)
        when (state.operator) {
            Operator.Set -> {
                when (state.type) {
                    Type.Resource -> {
                        val res = state.data as Resource
                        if (selected.transport.count() <= 2) {
                            selected.transport.add(res)
                        } else {
                            throw IllegalStateException("$state :: ${selected.transport}")
                        }
                        selected.redraw = true
                    }
                    Type.Offered -> {
                        selected.storage.add(state.data as Resource)
                    }
                    Type.Building -> {
                        selected.building = state.data as Building
                        selected.redraw = true
                        selected.building!!.requires.forEach { needed ->
                            applyState(GameState(selected.coordinates, Operator.Set, Type.Requires, needed))
//                            val transportRequest = TransportRequestNew(
//                                destination = selected.coordinates,
//                                what = needed
//                            )
//                            transportManager.request(transportRequest)
                        }
                        selected.building!!.offers.forEach { resource ->
                            applyState(GameState(selected.coordinates, Operator.Set, Type.Offered, resource))
                        }
                    }
                    Type.Requires -> {
                        selected.requires.add(state.data as Resource)
                    }
                }
            }
            Operator.Remove -> {
                when (state.type) {
                    Type.Resource -> {
                        val res = state.data as Resource
                        if (!selected.transport.remove(res)) {
                            throw IllegalStateException()
                        }
                        selected.redraw = true
                    }
                    Type.Offered -> {
                        selected.storage.remove(state.data as Resource)
                    }
                    Type.Building -> TODO()
                    Type.Requires -> TODO()
                }
            }
        }
    }
}
