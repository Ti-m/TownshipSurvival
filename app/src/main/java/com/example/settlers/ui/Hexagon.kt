package com.example.settlers.ui

import kotlin.math.sqrt

class Hexagon(val a: Float) {

    //a is the outer radius

    /*
    * ....p1..p2...
    * .p3...c...p4.
    * ....p5..p6..
    * */
    companion object {
        private val sqrt3 = sqrt(3.0f)
    }
    val r = sqrt3 * a / 2
    val center = Pair(a,r)

    //calculated from center which is 0,0
    val p3 = Pair(center.first - a  , center.second)
    val p4 = Pair(center.first + a  , center.second)
    val p1 = Pair(center.first - a/2, center.second - r)
    val p2 = Pair(center.first + a/2, center.second - r)
    val p5 = Pair(center.first - a/2, center.second + r)
    val p6 = Pair(center.first + a/2, center.second + r)

}