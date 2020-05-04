package com.example.settlers

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.round

open class TerrainInterpolator {
    protected var randomAmplitude: Double = 1.0
    protected var offset: Double = 0.0
    protected lateinit var terrain: Array<Array<Double?>>

    fun interpolate(terrain: Array<Array<Double?>>, size: Int, randomAmplitude: Double = 1.0, offset: Double = 0.0) {
        if (isPowerOfTwo(size)) return
        if (size == 0 || size == 1) return
        this.terrain = terrain
        this.randomAmplitude = randomAmplitude
        this.offset = offset

        doSquare(0, 0, size)
        doDiamond(0,0, size)
        if (size > 3) {
            val s2 = ceil(size.toDouble() / 2).toInt()
            val s2h = ceil(s2.toDouble() / 2).toInt()
            doSquare(0, 0, s2)
            doSquare(0, s2h, s2)
            doSquare(s2h, 0, s2)
            doSquare(s2h, s2h, s2)
            doDiamond(0, 0, s2)
            doDiamond(0, s2h, s2)
            doDiamond(s2h, 0, s2)
            doDiamond(s2h, s2h, s2)
        }
    }

    fun isPowerOfTwo(n: Int): Boolean {
        return if (n == 0 || n == 1) false else ceil(log2(n.toDouble())).toInt() ==
                floor(log2(n.toDouble())).toInt()
    }

    open fun doSquare(x: Int, y: Int, size: Int) {
        val u = size-1
        this.set(x = x + u/2, y = y + u/2, value = this.average(x + 0, y + 0, x + u, y + 0, x + 0, y + u, x + u, y + u))
    }

    open fun doDiamond(x: Int, y: Int, size: Int) {
        val u = size-1

        //this.set(x = x+u/2, y = y+0,   value = this.average(x + 0  , y + 0  , x + u/2, y - u/2, x + u  , y + 0  , x + u/2 , y + u/2))
        this.set(x = x+u/2, y = y+0,   value = this.average(x + 0  , y + 0  , x + u, y + 0, x + u/2, y + u/2))
        this.set(x = x+0,   y = y+u/2, value = this.average(x + u/2, y + u/2, x + 0  , y + 0  , x + 0  , y + u))
        this.set(x = x+u/2, y = y+u,   value = this.average(x + 0  , y + u  , x + u  , y + u  , x + u/2, y + u/2))
        this.set(x = x+u,   y = y+u/2, value = this.average(x + u/2, y + u/2, x + u  , y + 0  , x + u  , y + u))
    }

    open fun set(x: Int, y: Int, value: Double) {
        terrain[x][y] = value * random() + offset
    }

    open fun get(x: Int, y: Int): Double {
        return  terrain[x][y]!!
    }

    open fun average(vararg points: Int): Double {
        var i = 0
        var result = 0.0
       // var divider = 0
        while (i < points.size) {
       //     if (points[i] > 0 && points[i+1] > 0 && points[i] < terrain.count() && points[i+1] < terrain[0].count()) {
                result += (terrain[points[i]][points[i + 1]]!!)
        //        divider += 1
       //     }
            i += 2
        }
        return round((result / (points.size / 2)) * 100) / 100
        //return round((result / divider) * 100) / 100
    }

    open fun random(): Double {
        return randomAmplitude
    }
}