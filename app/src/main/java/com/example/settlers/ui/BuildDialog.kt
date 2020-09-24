package com.example.settlers.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.settlers.Coordinates

class BuildDialog : DialogFragment() {

    //TODO will this work this way? There is a zero argument constructor now. But the values will not get set on recreation
    var items: Array<String>? = null
    var clickListener: DialogInterface.OnClickListener? = null
    var coordinates: Coordinates? = null //For debugging
    var storage: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        //dialog.setMessage("Pick something")
        dialog.setTitle("Pick a building\n$coordinates")
//        dialog.setItems(items.toTypedArray(), handler)
        dialog.setItems(items, clickListener)
        return dialog.create()
    }
}