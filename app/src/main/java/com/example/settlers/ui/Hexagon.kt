package com.example.settlers.ui

import kotlin.math.sqrt

class Hexagon(val w: Float) {

    //a is the outer radius

    /*
    * ....p1..p2...
    * .p3...c...p4.
    * ....p5..p6..
    * */
    companion object {
        private val sqrt3 = sqrt(3.0f)
    }
    val h = sqrt3 * w / 2 //half height
    val center = Pair(w,h) //half width

    //calculated from center which is 0,0
    val p3 = Pair(center.first - w  , center.second)
    val p4 = Pair(center.first + w  , center.second)
    val p1 = Pair(center.first - w/2, center.second - h)
    val p2 = Pair(center.first + w/2, center.second - h)
    val p5 = Pair(center.first - w/2, center.second + h)
    val p6 = Pair(center.first + w/2, center.second + h)

}