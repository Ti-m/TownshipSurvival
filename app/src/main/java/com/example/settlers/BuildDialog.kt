package com.example.settlers

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class BuildDialog(private val cell: Cell, private val cells: List<Cell>, private val transports: MutableList<Transport>, private val tile: FlagTile) : DialogFragment() {
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
            cell.building!!.requires.forEach { needed ->
                val startCoordinates = cells.first { it.building?.offers?.contains(needed) ?: false}//TODO this is not the closests ...
                startCoordinates.building!!.markRequested(needed)
                transports.add(Transport(start = startCoordinates.coordinates, end = cell.coordinates, what = needed))
            }
            cell.redraw = true
            //tile.invalidate()
        })
        //dialog.setButton(1,"OK mate", {dialog, which ->  })
        return dialog.create()
    }
}