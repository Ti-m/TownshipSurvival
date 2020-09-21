package com.example.settlers

class BuildDialogHandler(
    private val gameStateManager: GameStateManager
) {
    fun onClick(cell: Cell, which: Building) {
        gameStateManager.applyStates(listOf(GameState(cell.coordinates, Operator.Set, Type.Building, which)))
        //TODO somehow get the flagtile here and
//        it.value.invalidate()
//        it.value.cell.redraw = false
    }

}
