package com.example.settlers.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.settlers.*
import com.example.settlers.databinding.FragmentBuildBinding

class BuildFragment : Fragment() {

    val placeholderData = listOf(
        BuildFragmentPlaceholderItem("Townhall", type = Townhall()),
        BuildFragmentPlaceholderItem("Lumberjack", type = Lumberjack()),
        BuildFragmentPlaceholderItem("Forester", type = Forester()),
        BuildFragmentPlaceholderItem("Lumbermill", type = Lumbermill()),
        BuildFragmentPlaceholderItem("Stonemason", type = Stonemason()),
        BuildFragmentPlaceholderItem("Tower", type = Tower()),
        BuildFragmentPlaceholderItem("Fletcher", type = Fletcher()),
        BuildFragmentPlaceholderItem("Road", type = Road()),
        BuildFragmentPlaceholderItem("Fisherman", type = Fisherman()),
        BuildFragmentPlaceholderItem("HouseLevel1", type = HouseLevel1()),
        BuildFragmentPlaceholderItem("HouseLevel2", type = HouseLevel2()),
        BuildFragmentPlaceholderItem("HouseLevel3", type = HouseLevel3()),
        BuildFragmentPlaceholderItem("Pyramid", type = Pyramid()),
    )
//    private val availableBuildings = arrayOf(
//        Townhall(),
//        Lumberjack(),
//        Forester(),
//        Lumbermill(),
//        Stonemason(),
//        Tower(),
//        Fletcher(),
//        Road(),
//        Fisherman(),
//        HouseLevel1(),
//        HouseLevel2(),//TODO replace with upgrade of lvl1
//        HouseLevel3(),//TODO replace with upgrade of lvl2
//        Pyramid()
//    )

    companion object {
        fun newInstance(coordinates: Coordinates): BuildFragment {
            val fragment = BuildFragment()
            val bundle = Bundle()
            bundle.putSerializable(BaseDialog.COORDINATES, coordinates)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val a = FragmentBuildBinding.inflate(
            LayoutInflater.from(context),
            container,
            false
        )

        val cb = (context as MainActivity).buildDialogClickHandler
        val coordiantes = (requireArguments().getSerializable(BaseDialog.COORDINATES) as Coordinates)

        with(a.buildRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = BuildRecyclerViewAdapter(placeholderData, cb, coordiantes)
        }
        return a.root
    }
}


//TODO remove
data class BuildFragmentPlaceholderItem(val content: String, val type: Building)