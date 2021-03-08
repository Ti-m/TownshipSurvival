package com.example.settlers.ui

import com.example.settlers.MainActivity
import kotlin.math.round

class Hexagon(isLowDpi: Boolean) {

    //a is the outer radius

    /*
    * ....p1..p2...
    * .p3...c...p4.
    * ....p5..p6..
    * */
    companion object {
        private const val sqrt3 = 2//sqrt(3.0f) //Final layout is on Int pixels, so do this approximation
    }
    val w = MainActivity.flagDistance / 4

    //Fixes issue on low dpi screen like Fire HD tablet
    private val slightlyMore = if (isLowDpi) 1 else 0

    val h = round(sqrt3 * w / 2) //half height
    val center = Pair(2 * w, 2 * h) //half width

    //calculated from center which is 0,0
    val p3 = Pair(center.first - 2 * w  , center.second)
    val p4 = Pair(center.first + 2 * w  , center.second)
    val p1 = Pair(center.first - w, center.second - 2 * h - slightlyMore)
    val p2 = Pair(center.first + w, center.second - 2 * h - slightlyMore)
    val p5 = Pair(center.first - w, center.second + 2 * h + slightlyMore)
    val p6 = Pair(center.first + w, center.second + 2 * h + slightlyMore)
}