package com.example.settlers.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.settlers.Coordinates
import com.example.settlers.databinding.FragmentBuildItemBinding

class BuildRecyclerViewAdapter(
    private val values: List<BuildFragmentPlaceholderItem>,
    private val clickHandler: BuildDialogCallback,
    private val coordinates: Coordinates
) : RecyclerView.Adapter<BuildRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(binding: FragmentBuildItemBinding) : RecyclerView.ViewHolder(binding.root) {
        //val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val imageButton: ImageButton = binding.imageButton
        val layout: LinearLayout = binding.layout

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentBuildItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        holder.contentView.text = item.content
        holder.imageButton.setImageResource(item.type.drawableRessoucreId)
        holder.layout.setOnClickListener {
            clickHandler.selectedCallback(coordinates = coordinates, selectedBuilding = item.type)
        }
        holder.imageButton.setOnClickListener {
            clickHandler.selectedCallback(coordinates = coordinates, selectedBuilding = item.type)
        }
    }
    override fun getItemCount() = values.size
}