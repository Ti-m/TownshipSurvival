package com.example.settlers.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class BuildDialog(
    private val items: Array<String>,
    private val handler: DialogInterface.OnClickListener
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        //dialog.setMessage("Pick something")
        dialog.setTitle("Pick a building")
//        dialog.setItems(items.toTypedArray(), handler)
        dialog.setItems(items, handler)
        return dialog.create()
    }
}