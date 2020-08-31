package com.example.settlers

import android.content.DialogInterface

class BuildDialogHandler(
    private val transportManager: TransportManager,
    private val mapManager: MapManager
) {
    var cell: Cell? = null

    fun onClick(dialog: DialogInterface?, which: Int) {
        if (cell == null) throw IllegalStateException()

        val type = BuildingType.values()[which]
        cell!!.building = when (type) {
            BuildingType.Townhall -> Townhall()
            BuildingType.Lumberjack -> Lumberjack()
            BuildingType.Road -> Road()
        }
        cell!!.building!!.requires.forEach { needed -> //TODO Move this to MainActivity? Or some Handler class?
            val transportRequest = TransportRequestNew(destination = cell!!.coordinates, what = Resource.Wood)
            transportManager.request(transportRequest)
        }
        cell!!.building!!.offers.forEach { resource ->//TODO Move this to MainActivity? Or some Handler class?
            mapManager.applyStates(listOf(GameState(cell!!.coordinates, Command.SetResourceOffered, resource)))
        }
        cell!!.redraw = true
        cell = null
        //tile.invalidate()
    }

}
