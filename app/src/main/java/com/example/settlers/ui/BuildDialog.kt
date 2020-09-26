package com.example.settlers.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.settlers.Coordinates

class BuildDialog : DialogFragment() {

    var items: Array<String>? = null
    var clickListener: DialogInterface.OnClickListener? = null

    companion object {
        fun newInstance(coordinates: Coordinates): BuildDialog {
            val dialog = BuildDialog()
            val bundle = Bundle()
            bundle.putSerializable(COORDINATES, coordinates)
            //bundle.putString("message", message)
            dialog.arguments = bundle
            return dialog
        }

        private val COORDINATES = "coordinates"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        //dialog.setMessage("Pick something")
        val coordinates = (arguments!!.getSerializable(COORDINATES) as Coordinates)
        dialog.setTitle("Pick a building :: (x=${coordinates.x}, y=${coordinates.y})")
//        dialog.setItems(items.toTypedArray(), handler)
        dialog.setItems(items, clickListener)
        return dialog.create()
    }
}

class InspectDialog : DialogFragment() {

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
        return dialog.create()
    }
}