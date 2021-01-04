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
//            Make GameStateManger the only thing which is allowed to change state?
//            Make everything immutable and only changeable by GamestateManager?
//            So even handle the production progress by GameStateManger?
        mapManager.getCellsWhichShallRunAnAnimation().forEach { (_, cell) ->
            prepareNextAnimation(cell)
        }

        mapManager.getCellsWithFinishedTowers().forEach { (_, cell) ->
            applyStates(engageTarget(cell))
        }

        mapManager.getCellsWithMovingObjects().forEach { (_, cell) ->
            applyStates(destruction(cell))
        }
        mapManager.getCellsWithMovingObjects().forEach { (coordinates, _) ->
            applyStates(transportManager.move(coordinates))
        }

        mapManager.getCellsWhichNeedToUpdateProductionRequirements().forEach { (_, cell) ->
            applyStates(transportManager.refreshProductionRequirements(cell))
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
            applyStates(mapManager.runConstruction(cell))
        }
        //applyStates(transportManager.convertStorageToProduction())
        //applyStates(transportManager.convertTransportToStorage())
        //applyStates(transportManager.moveResources())

    }

    private fun engageTarget(cell: Cell): List<GameState> {
        val range = (mapManager.queryBuilding(cell.coordinates) as Tower).range
        transportManager.shootWithTowerCalculatePath(cell.coordinates, range)?.let { target ->
            if (transportManager.cellHasArrow(cell)) {//TODO generalize to ammunition
                return shootArrow(target)
            }
        }
        return emptyList()
    }

    private fun shootArrow(target: TargetCoordinates): List<GameState> {
        val result = mutableListOf(
            GameState(target.start, Operator.Remove, Type.Production, Arrow),//TODO generalize to ammunition
            GameState(target.start, Operator.Set, Type.Animation, ShootAnimation()),
            GameState(target.destination, Operator.Set, Type.Damage, Damage(1)),
//            GameState(path, Operator.Set, Type.Animation, ShootPathAnimation()),
//            GameState(target, Operator.Set, Type.Animation, ExplosionAnimation()),

        )
        result.addAll(target.path.map {
            GameState(it, Operator.Set, Type.Animation, ProjectileAnimation())
        })

        return result
    }

    private fun prepareNextAnimation(cell: Cell) {
        //TODO create a GameStateObject here, which handles the removal?
        cell.animation!!.parts.removeFirstOrNull()
        if (cell.animation!!.parts.count() == 0 ) {
            cell.animation = null //Remove animation
        }
    }

    private fun destruction(cell: Cell): Collection<GameState> {
        if (cell.movingObject is Zombie &&
            cell.building != null &&
            cell.building !is Road &&
            cell.building !is Spawner
        ) {
            return listOf(
                GameState(cell.coordinates, Operator.Remove, Type.Building, null),
                GameState(cell.coordinates, Operator.Remove, Type.MovingObject, null),
                GameState(cell.coordinates, Operator.Set, Type.Animation, ExplosionAnimation()),
            )
        }
        return emptyList()
    }

    fun applyStates(newStates: Collection<GameState>) {
        newStates.forEach { state ->
            applyState(state)
        }
    }

    //A GameState is a request to change the game state, this method handles them
    fun applyState(state: GameState) {
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
                        selected.touched = true
                    }
                    Type.Storage -> {
                        selected.storage.add(state.data as Resource)
                        selected.touched = true
                    }
                    Type.Building -> {
                        selected.building = state.data as Building
                        selected.requires.clear()//Make sure old requests are deleted, in case of building replacement
                        selected.building!!.requiresConstruction.forEach { needed ->
                            applyState(
                                GameState(
                                    selected.coordinates,
                                    Operator.Set,
                                    Type.Required,
                                    needed
                                )
                            )
//                            val transportRequest = TransportRequestNew(
//                                destination = selected.coordinates,
//                                what = needed
//                            )
//                            transportManager.request(transportRequest)
                        }
                        selected.building!!.offers.forEach { resource ->
                            applyState(
                                GameState(
                                    selected.coordinates,
                                    Operator.Set,
                                    Type.Storage,
                                    resource
                                )
                            )
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
                        selected.touched = true
                    }
                    Type.Animation -> {
                        selected.animation = state.data as Animation
                    }
                    Type.Damage -> {
                        //TODO handle damage to buildings?
                        val a = selected.movingObject!!.health.minus((state.data as Damage).value)
                        if (a <= 0) {
                            selected.movingObject = null
                            applyState(
                                GameState(
                                    selected.coordinates,
                                    Operator.Set,
                                    Type.Animation,
                                    ExplosionAnimation()
                                )
                            )
                        }
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
                    }
                    Type.Storage -> {
                        selected.storage.remove(state.data as Resource)
                    }
                    Type.Building -> {
                        selected.requires.clear()
                        selected.building = null
                        selected.production.clear()
                        selected.storage.clear()
                        selected.transport.clear()
                    }
                    Type.Required -> selected.requires.remove(state.data as Resource)
                    Type.Production -> selected.production.remove(state.data as Resource)
                    Type.MovingObject -> {
                        selected.movingObject = null
                    }
                    Type.Animation -> {
                        selected.animation = null
                    }
                    Type.Damage -> throw IllegalStateException()
                }
            }
        }
    }
}

class GameStateManagerPreparedForTest(
    transportManager: TransportManager,
    mapManager: MapManager,
    log: Logger,
) : GameStateManager(transportManager, mapManager, log) {
    constructor(transportManager: TransportManager, mapManager: MapManager) : this(transportManager, mapManager, DisabledLogger())
    constructor(mapManager: MapManager) : this(TransportManagerPreparedForTest(mapManager), mapManager)
    constructor() : this(MapManagerPreparedForTest())
}
