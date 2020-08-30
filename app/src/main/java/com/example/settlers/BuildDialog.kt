package com.example.settlers

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class BuildDialog(
    private val cell: Cell,
    private val cells: List<Cell>,
    private val transportManager: TransportManager,
    private val tile: FlagTile,
    private val mapManager: MapManager
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        //dialog.setMessage("Pick something")
        dialog.setTitle("Pick a building")
        val items = BuildingType.values().map { it.name }
        dialog.setItems(items.toTypedArray(), DialogInterface.OnClickListener { dialog, which ->
            val type = BuildingType.values()[which]
            cell.building = when (type) {
                BuildingType.Townhall -> Townhall()
                BuildingType.Lumberjack -> Lumberjack()
                BuildingType.Road -> Road()
            }
            cell.building!!.requires.forEach { needed -> //TODO Move this to MainActivity? Or some Handler class?
                val transportRequest = TransportRequestNew(destination = cell.coordinates, what = Resource.Wood)
                transportManager.request(transportRequest)
            }
            cell.building!!.offers.forEach { resource ->//TODO Move this to MainActivity? Or some Handler class?
                mapManager.applyStates(listOf(GameState(cell.coordinates, Command.SetResourceOffered, resource)))
            }
            cell.redraw = true
            //tile.invalidate()
        })
        //dialog.setButton(1,"OK mate", {dialog, which ->  })
        return dialog.create()
    }
}