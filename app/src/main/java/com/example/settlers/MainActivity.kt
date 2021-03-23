package com.example.settlers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.settlers.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
        val flagDistance = 64.0f
        val flagDiameter = flagDistance / 40
        val tileGridSize = 33
        val gameBoardBorder = (4 * flagDistance).toInt()
    }

    lateinit var buildDialogClickHandler: BuildDialogCallback
    lateinit var inspectDialogClickHandler: InspectDialogCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //https://developer.android.com/guide/navigation/navigation-getting-started#navigate
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setupWithNavController(navController)
    }
}