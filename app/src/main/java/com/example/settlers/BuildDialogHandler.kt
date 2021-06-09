package com.example.settlers

import com.example.settlers.ui.BuildDialogCallback
import com.example.settlers.ui.InspectDialogCallback
import com.example.settlers.ui.StopDeliveryState

class BuildDialogHandler (
    private val gameStateManager: GameStateManager
) : BuildDialogCallback {

    override fun selectedCallback(selectedBuilding: Building, coordinates: Coordinates) {
        val b = when (selectedBuilding) { //Create a new instance, otherwise, internally it is always
            // used the reference from the object in the BuildDialog building list.
            is Townhall -> Townhall()
            is Lumberjack -> Lumberjack()
            is Forester -> Forester()
            is Stonemason -> Stonemason()
            is Fletcher -> Fletcher()
            is Lumbermill -> Lumbermill()
            is Tower -> Tower()
            is Road -> Road()
            is Fisherman -> Fisherman()
            is HouseLevel1 -> HouseLevel1()
            is HouseLevel2 -> HouseLevel2()//TODO replace with upgrade of lvl1
            is HouseLevel3 -> HouseLevel3()//TODO replace with upgrade of lvl2
            is Pyramid -> Pyramid()
            else -> throw NotImplementedError()
        }
        gameStateManager.applyStates(listOf(GameState(coordinates, Operator.Set, Type.Building, b)))
    }
}

class InspectDialogHandler (
    private val mapManager: MapManager,
    private val gameStateManager: GameStateManager,
) : InspectDialogCallback {

    override fun inspectCallback(coordinates: Coordinates, stopDelivery: StopDeliveryState) {
        val selectedCell = mapManager.findSpecificCell(coordinates)!!
        val selectedBuilding = mapManager.queryBuilding(coordinates)!!
        when (stopDelivery) {
            StopDeliveryState.Normal -> { //TODO Is Stopped and Normal kind of reversed??
                selectedBuilding.stopDelivery = true
                gameStateManager.applyStates(mapManager.removeHouseAssignmentsWithProductionBuildingAsBase(selectedCell))
            }
            StopDeliveryState.Stopped -> {
                selectedBuilding.stopDelivery = false
            }
            StopDeliveryState.NoBuilding -> throw Error("Invalid case - programming error")
        }
    }
}