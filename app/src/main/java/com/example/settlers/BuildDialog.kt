package com.example.settlers

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class BuildDialog(private val element: Element, private val tile: FlagTile) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        //dialog.setMessage("Pick something")
        dialog.setTitle("Pick a building")
        dialog.setItems(arrayOf(BuildingType.Lumberjack.name), DialogInterface.OnClickListener { dialog, which ->
            element.building = BuildingType.Lumberjack
            tile.invalidate()
        })
        //dialog.setButton(1,"OK mate", {dialog, which ->  })
        return dialog.create()
    }
}