package com.example.settlers

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class BuildDialog(private val cell: Cell, private val tile: FlagTile) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        //dialog.setMessage("Pick something")
        dialog.setTitle("Pick a building")
        val items = BuildingType.values().map { it.name }
        dialog.setItems(items.toTypedArray(), DialogInterface.OnClickListener { dialog, which ->
            cell.building = BuildingType.values()[which]
            tile.invalidate()
        })
        //dialog.setButton(1,"OK mate", {dialog, which ->  })
        return dialog.create()
    }
}