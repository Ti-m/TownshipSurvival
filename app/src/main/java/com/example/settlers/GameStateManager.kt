package com.example.settlers

import com.example.settlers.util.DisabledLogger
import com.example.settlers.util.Logger

class Audit {
    private var log = ""
    private var turnCount = 0

    fun newTurn() {
        turnCount += 1
    }

    fun doAudit(state: GameState) {
        log = "$log\n $turnCount: $state"
    }
}

open class GameStateManager(
    private val transportManager: TransportManager,
    private val mapManager: MapManager,
    private val log: Logger = DisabledLogger(),
    private val audit: Audit? = null
) {
    companion object {
        private val TAG = "GameRunLoop"
    }

    fun tick() {
        log.logi(TAG, "tick")
        audit?.newTurn()
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
            applyStates(refreshProductionRequirements(cell))
        }

        mapManager.getCellsWhichRequireStuff().forEach { (_, cell) ->
            applyStates(convertStorageToProduction(cell))
        }
        mapManager.getCellsWhichRequireStuff().forEach { (_, cell) ->
            applyStates(convertTransportToStorage(cell))
        }
        mapManager.getCellsWhichRequireStuffWhichIsNotInStorage().forEach { (_, cell) ->
            applyStates(transportManager.moveResources(cell))
        }

        mapManager.getCellsWhichShallContinueAProduction().forEach { (_, cell) ->
            applyStates(runProduction(cell))
        }

        mapManager.getCellsWhichShallRunAProduction().forEach { (_, cell) ->
            applyStates(runProduction(cell))
        }

        mapManager.getCellsWhichShallRunAProductionWithConsumingOutsideResources().forEach { (_, cell) ->
            applyStates(runProductionWithConsumingOutsideResource(cell))
        }

        mapManager.getCellsWhichShallRunAProductionWithProducingOutsideResources().forEach { (_, cell) ->
            applyStates(runProductionWithProducingOutsideResource(cell))
        }

        mapManager.getCellsWhichShallRunAConstruction().forEach { (_, cell) ->
            applyStates(runConstruction(cell))
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

    //This only does a single step each tick
    private fun convertStorageToProduction(cell: Cell): List<GameState> {
        val matched = mutableListOf<GameState>()
        cell.requires.forEach { required ->
            if (cell.storage.contains(required)) {
                matched.add(GameState(cell.coordinates, Operator.Set, Type.Production, required))
                matched.add(GameState(cell.coordinates, Operator.Remove, Type.Storage, required))
                matched.add(GameState(cell.coordinates, Operator.Remove, Type.Required, required))
                return matched //Only do a single loop
            }
        }
        return matched
    }

    //This only does a single step each tick
    //It looks better for animations and otherwise I need ti allocate a lot of memory to create
    // copys of the requires and transport lists
    private fun convertTransportToStorage(cell: Cell): List<GameState> {
        val matched = mutableListOf<GameState>()
        cell.requires.forEach { required ->
            if (cell.transport.contains(required)) {
                matched.add(GameState(cell.coordinates, Operator.Set, Type.Storage, required))
                matched.add(GameState(cell.coordinates, Operator.Remove, Type.Transport, required))
                //matched.add(GameState(cell.key, Operator.Remove, Type.Required, required))
                return matched //Only do a single loop
            }
        }
        return matched
    }

    private fun refreshProductionRequirements(cell: Cell): Collection<GameState> {
        return getResourcesNotAlreadyRequested(cell).map {
            GameState(cell.coordinates, Operator.Set, Type.Required, it)
        }
    }

    private fun getResourcesNotAlreadyRequested(cell: Cell): List<Resource> {
        val list = mutableListOf<Resource>()
        list.addAll(cell.production)
        list.addAll(cell.transport)
        list.addAll(cell.storage)

        val res = cell.building!!.requiresProduction.toMutableList()
        list.forEach {
            res.remove(it)
        }
        return res
    }

    private fun runProduction(cell: Cell): Collection<GameState> {
        return if (!cell.building!!.isProductionInProgress()) {
            val states = cell.building!!.produce(cell.coordinates).toMutableList()
            states.addAll(cell.building!!.removeProductionRequirementsFromProduction(cell.coordinates))
            states
        } else {
            cell.building!!.produce(cell.coordinates)
        }
    }

    private fun runProductionWithConsumingOutsideResource(cell: Cell): Collection<GameState> {
        return if (!cell.building!!.isProductionInProgress()) {
            if (transportManager.isWorldResourceInRange(cell, cell.building!!.produceConsumesWorldResource!!)) {
                val states = transportManager.removeWorldResourceInRange(cell, cell.building!!.produceConsumesWorldResource!!).toMutableList()
                states.addAll(cell.building!!.produce(cell.coordinates))
                return states
            }
            emptyList()
        } else {
            cell.building!!.produce(cell.coordinates)
        }
    }

    //In this case, produce is only a timer to know when the next WorldResource is created
    private fun runProductionWithProducingOutsideResource(cell: Cell): Collection<GameState> {
        return if (!cell.building!!.isProductionInProgress()) {
            if (transportManager.isSpaceAvailableForWorldResource(cell.coordinates)) {
                val states = transportManager.addWorldResourceInRange(cell.coordinates, cell.building!!.produceCreatesWorldResource!!).toMutableList()
                states.addAll(cell.building!!.produce(cell.coordinates))
                return states
            }
            emptyList()
        } else {
            cell.building!!.produce(cell.coordinates)
        }
    }


    private fun runConstruction(cell: Cell) : Collection<GameState> {
        return if (!cell.building!!.isConstructionInProgress()) {
            cell.building!!.construct()
            cell.building!!.removeConstructionRequirementsFromProduction(cell.coordinates)
        } else {
            cell.building!!.construct()
            emptyList()
        }
    }

    fun applyStates(newStates: Collection<GameState>) {
        newStates.forEach { state ->
            applyState(state)
        }
    }

    //A GameState is a request to change the game state, this method handles them
    fun applyState(state: GameState) {
        log.logi("GameStateManager", "apply State: $state")
        audit?.doAudit(state)

        val selected = mapManager.findSpecificCell(state.coordinates)!!
        when (state.operator) {
            Operator.Set -> {
                when (state.type) {
                    Type.Transport -> {
                        val res = state.data as Resource
                        if (selected.transport.count() < 2) {
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
                    Type.WorldResource -> selected.worldResource = state.data as WorldResource
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
                    Type.WorldResource -> selected.worldResource = null
                }
            }
        }
    }

    fun setNextSpawner() {
        nextSpawnerLocation()?.let {
            applyState(GameStateCreator.createSpawner(it))
        }
    }

    private fun nextSpawnerLocation(): Coordinates? {
        val southEastEdge = mapManager.getSouthEastEdge()

        return transportManager.nextSpawnerLocation(southEastEdge)
    }
}