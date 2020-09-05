package com.example.settlers

class BuildDialogHandler(
    private val transportManager: TransportManager,
    private val mapManager: MapManager
) {
    fun onClick(cell: Cell, which: Building) {
        cell.building = which
        cell.building!!.requires.forEach { needed ->
            val transportRequest = TransportRequestNew(destination = cell.coordinates, what = needed)
            transportManager.request(transportRequest)
        }
        cell.building!!.offers.forEach { resource ->
            mapManager.applyStates(listOf(GameState(cell.coordinates, Operator.Set, Type.Offered, resource)))
        }
        cell.redraw = true
    }

}
