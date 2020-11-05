package com.example.settlers

import com.example.settlers.ui.BuildDialogCallback

class BuildDialogHandler (
    private val gameStateManager: GameStateManager
) : BuildDialogCallback {

    override fun selectedCallback(selectedBuilding: Building, coordinates: Coordinates) {
        gameStateManager.applyStates(listOf(GameState(coordinates, Operator.Set, Type.Building, selectedBuilding)))
    }
}
