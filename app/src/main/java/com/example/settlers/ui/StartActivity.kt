package com.example.settlers.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.settlers.*
import com.example.settlers.databinding.ActivityStartBinding
import com.example.settlers.terrain.MapGenerator
import com.example.settlers.terrain.TerrainInterpolator
import kotlin.random.Random

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_start)

        val binding: ActivityStartBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //val model: MainViewModel by viewModels()


        //eigentlich müsste MainViewModel doch der geladene Zustand geteilt sein?

        binding.newGameButton.setOnClickListener {
            val randomGenerator = Random
            val keyValueStorage = DefaultKeyValueStorage(getSharedPreferences("GameStateStorage", Context.MODE_PRIVATE))
            val mapGen = MapGenerator(TerrainInterpolator(randomGenerator), randomGenerator)
            val mapSaver = MapSaver(mutableMapOf(), mapGen, keyValueStorage)
            mapSaver.newGame()
            mapSaver.save()
            startActivity(Intent(this, MainActivity::class.java))
            //finish here?
        }

        binding.continueGameButton.setOnClickListener {
            //mapSaver.load()
            startActivity(Intent(this, MainActivity::class.java))
            //finish here?
        }
    }
}