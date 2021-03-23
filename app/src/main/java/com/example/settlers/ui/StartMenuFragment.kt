package com.example.settlers.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.settlers.*
import com.example.settlers.databinding.FragmentStartMenuBinding
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import kotlin.random.Random

class StartMenuFragment : Fragment() {

    private var binding: FragmentStartMenuBinding? = null
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_start_menu, container, false)
        binding = FragmentStartMenuBinding.inflate(inflater, container, false)

        val randomGenerator = Random
        val keyValueStorage = DefaultKeyValueStorage(requireActivity().getSharedPreferences("GameStateStorage", Context.MODE_PRIVATE))
        val mapGen = MapGenerator(TerrainInterpolator(randomGenerator), randomGenerator)
        val mapSaver = MapSaver(model.cells, mapGen, keyValueStorage)

        binding!!.newGameButton.setOnClickListener {

            mapSaver.newGame()
            mapSaver.save()
            findNavController().navigate(StartMenuFragmentDirections.actionStartMenuFragmentToGameFragment())
        }

        binding!!.continueGameButton.setOnClickListener {
            mapSaver.load()
            findNavController().navigate(StartMenuFragmentDirections.actionStartMenuFragmentToGameFragment())
        }

        binding!!.deleteButton.setOnClickListener {
            mapSaver.delete()
        }

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}