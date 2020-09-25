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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        //dialog.setMessage("Pick something")
        dialog.setTitle("Pick a building :: (x=${coordinates!!.x}, y=${coordinates!!.y})")
//        dialog.setItems(items.toTypedArray(), handler)
        dialog.setItems(items, clickListener)
        return dialog.create()
    }
}

class InspectDialog : DialogFragment() {
    //var coordinates: Coordinates? = null
    var x = null
    var y = null
    var content: String? = null

    companion object {
        fun newInstance(title: String, message: String): InspectDialog {
            val dialog = InspectDialog()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("message", message)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(arguments!!.getString("title"))
        dialog.setMessage(arguments!!.getString("message"))
        //dialog.setTitle("Inspect :: (x=${x}, y=${y})")
//        dialog.setItems(items.toTypedArray(), handler)
//        dialog.setItems(items, clickListener)
        return dialog.create()
    }
}