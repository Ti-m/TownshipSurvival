package com.example.settlers

class BuildDialogHandler(
    private val gameStateManager: GameStateManager
) {
    fun onClick(cell: Cell, which: Building) {
        gameStateManager.applyStates(listOf(GameState(cell.coordinates, Operator.Set, Type.Building, which)))
//        cell.building = which //TODO set here through gamestate object?
//        cell.building!!.requires.forEach { needed ->
//            val transportRequest = TransportRequestNew(destination = cell.coordinates, what = needed)
//            transportManager.request(transportRequest)
//        }
//        cell.building!!.offers.forEach { resource ->
//            mapManager.applyStates(listOf(GameState(cell.coordinates, Operator.Set, Type.Offered, resource)))
//        }
//        cell.redraw = true
    }

}
