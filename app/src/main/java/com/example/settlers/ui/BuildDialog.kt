package com.example.settlers.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.settlers.*

interface BuildDialogCallback {
    fun selectedCallback(selectedBuilding: Building, coordinates: Coordinates)
}

abstract class BaseDialog : DialogFragment() {
    companion object {
        val COORDINATES = "coordinates"
    }
}

class BuildDialog : BaseDialog() {

    private lateinit var callback: BuildDialogCallback

    companion object {
        fun newInstance(coordinates: Coordinates): BuildDialog {
            val dialog = BuildDialog()
            val bundle = Bundle()
            bundle.putSerializable(COORDINATES, coordinates)
            bundle.putSerializable("items", availableBuildings.map { it.javaClass.simpleName }.toTypedArray())
            dialog.arguments = bundle
            return dialog
        }

        private val ITEMS = "items"
        private val availableBuildings = arrayOf(Townhall(), Lumberjack(), Stonemason(), Tower(), Fletcher(), Road(), Pyramid())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = (context as MainActivity).buildDialogClickHandler
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        val coordinates = (arguments!!.getSerializable(COORDINATES) as Coordinates)
        dialog.setTitle("Pick a building :: (x=${coordinates.x}, y=${coordinates.y})")
        val items = arguments!!.getSerializable(ITEMS) as Array<String>
        dialog.setItems(items, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                callback.selectedCallback(availableBuildings[which], coordinates)
            }
        })

        return dialog.create()
    }
}

class InspectDialog : BaseDialog() {

    companion object {
        fun newInstance(coordinates: Coordinates, message: String): InspectDialog {
            val dialog = InspectDialog()
            val bundle = Bundle()
            bundle.putSerializable(COORDINATES, coordinates)
            bundle.putString(MESSAGE, message)
            dialog.arguments = bundle
            return dialog
        }

        private val MESSAGE = "message"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        val coordinates = (arguments!!.getSerializable(COORDINATES) as Coordinates)
        dialog.setTitle( "Inspect :: (x=${coordinates.x}, y=${coordinates.y})")
        dialog.setMessage(arguments!!.getString(MESSAGE))
        return dialog.create()
    }
}