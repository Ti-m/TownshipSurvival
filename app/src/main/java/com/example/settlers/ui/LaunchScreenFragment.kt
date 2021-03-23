package com.example.settlers.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.settlers.DefaultKeyValueStorage
import com.example.settlers.MainViewModel
import com.example.settlers.MapSaver
import com.example.settlers.R
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import kotlin.random.Random

class LaunchScreenFragment : Fragment() {

    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launch_screen, container, false)
    }

    override fun onStart() {
        super.onStart()

        val randomGenerator = Random
        val keyValueStorage = DefaultKeyValueStorage(requireActivity().getSharedPreferences("GameStateStorage", Context.MODE_PRIVATE))
        val mapGen = MapGenerator(TerrainInterpolator(randomGenerator), randomGenerator)
        val mapSaver = MapSaver(model.cells, mapGen, keyValueStorage)

        val navController = findNavController()

        if (mapSaver.isSaveAvailable()) {
            navController.navigate(LaunchScreenFragmentDirections.actionLaunchScreenFragmentToGameFragment())
        } else {
            mapSaver.load()
            if (mapSaver.isSaveAvailable()) {
                navController.navigate(LaunchScreenFragmentDirections.actionLaunchScreenFragmentToGameFragment())
            } else {
                navController.navigate(LaunchScreenFragmentDirections.actionLaunchScreenFragmentToStartMenuFragment())
            }
        }
    }
}