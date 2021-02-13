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
            is Pyramid -> Pyramid()
            else -> throw NotImplementedError()
        }
        gameStateManager.applyStates(listOf(GameState(coordinates, Operator.Set, Type.Building, b)))
    }
}

class InspectDialogHandler (
    private val mapManager: MapManager
) : InspectDialogCallback {

    override fun inspectCallback(coordinates: Coordinates, stopDelivery: StopDeliveryState) {
        val building = mapManager.queryBuilding(coordinates)
        when (stopDelivery) {
            StopDeliveryState.Normal -> building!!.stopDelivery = true
            StopDeliveryState.Stopped -> building!!.stopDelivery = false
            StopDeliveryState.NoBuilding -> throw Error("Invalid case - programming error")
        }
    }
}