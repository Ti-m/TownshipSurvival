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
        private val availableBuildings = arrayOf(Townhall(), Lumberjack(), Forester(), Stonemason(), Tower(), Fletcher(), Road(), Pyramid())
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

interface InspectDialogCallback {
    fun inspectCallback(coordinates: Coordinates, stopDelivery: StopDeliveryState)
}

enum class StopDeliveryState {Stopped, Normal, NoBuilding}

class InspectDialog : BaseDialog() {

    private lateinit var callback: InspectDialogCallback

    companion object {
        fun newInstance(coordinates: Coordinates, message: String, stopDelivery: StopDeliveryState): InspectDialog {
            val dialog = InspectDialog()
            val bundle = Bundle()
            bundle.putSerializable(COORDINATES, coordinates)
            bundle.putString(MESSAGE, message)
            bundle.putSerializable(STOP_DELIVERY, stopDelivery)
            dialog.arguments = bundle
            return dialog
        }

        private val MESSAGE = "message"
        private val STOP_DELIVERY = "stop_delivery"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = (context as MainActivity).inspectDialogClickHandler
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        val coordinates = (arguments!!.getSerializable(COORDINATES) as Coordinates)
        val deliveryState = arguments!!.getSerializable(STOP_DELIVERY) as StopDeliveryState
        val deliveryText = when (deliveryState) {
            StopDeliveryState.Stopped -> "Resume Delivery"
            StopDeliveryState.Normal -> "Stop Delivery"
            StopDeliveryState.NoBuilding -> null
        }

        dialog.setTitle( "Inspect :: (x=${coordinates.x}, y=${coordinates.y})")
        //dialog.setMessage(arguments!!.getString(MESSAGE))
        dialog.setItems(arrayOf(
            arguments!!.getString(MESSAGE), //The data, click is ignored
            deliveryText
        ).filterNotNull().toTypedArray(), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                callback.inspectCallback(coordinates, deliveryState)
            }
        })
        return dialog.create()
    }
}