package com.example.settlers

import com.example.settlers.ui.BuildDialogCallback

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
            is Tower -> Tower()
            is Road -> Road()
            is Pyramid -> Pyramid()
            else -> throw NotImplementedError()
        }
        gameStateManager.applyStates(listOf(GameState(coordinates, Operator.Set, Type.Building, b)))
    }
}
