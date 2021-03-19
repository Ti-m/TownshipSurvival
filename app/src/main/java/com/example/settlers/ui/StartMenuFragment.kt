package com.example.settlers.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.settlers.DefaultKeyValueStorage
import com.example.settlers.MainActivity
import com.example.settlers.MapSaver
import com.example.settlers.R
import com.example.settlers.databinding.FragmentStartMenuBinding
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import kotlin.random.Random

class StartMenuFragment : Fragment() {

    private var binding: FragmentStartMenuBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_start_menu, container, false)
        binding = FragmentStartMenuBinding.inflate(inflater, container, false)

        binding!!.newGameButton.setOnClickListener {
            val randomGenerator = Random
            val keyValueStorage = DefaultKeyValueStorage(requireActivity().getSharedPreferences("GameStateStorage", Context.MODE_PRIVATE))
            val mapGen = MapGenerator(TerrainInterpolator(randomGenerator), randomGenerator)
            val mapSaver = MapSaver(mutableMapOf(), mapGen, keyValueStorage)
            mapSaver.newGame()
            mapSaver.save()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            //finish here?
        }

        binding!!.continueGameButton.setOnClickListener {
            //mapSaver.load()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            //finish here?
        }

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}