package com.example.settlers.terrain

import kotlin.math.*
import kotlin.random.Random

open class TerrainInterpolator {
    protected var randomAmplitude: Double = 1.0
    protected var randomAmplitudeDecrease: Double = 0.0
    protected var totalNumber = 0
    protected var offset: Double = 0.0
    protected lateinit var terrain: Array<Array<Double?>>

    fun interpolate(terrain: Array<Array<Double?>>, size: Int, randomAmplitude: Double = 1.0, offset: Double = 0.0) {
        if (!isPowerOfTwo(size-1)) return
        if (size == 0 || size == 1) return
        this.terrain = terrain
        this.randomAmplitude = randomAmplitude
        this.offset = offset
        this.totalNumber = size * size

        diamondPass(0, 0, size)

    }

    private fun diamondPass(x: Int, y: Int, size: Int) {
        if (size >= 3) {
            doSquare(x, y, size)
            doDiamond(x, y, size)
            if (size >= 5) {
                //unfinished here
                val s2 = ceil(size.toDouble() / 2).toInt()
                val s2h = ceil(s2.toDouble() / 2).toInt()
                doSquare(x = x, y = y, size = s2)
                doSquare(x = x, y = y + s2 - 1, size = s2)
                doSquare(x = x + s2 - 1, y = y, size = s2)
                doSquare(x = x + s2 - 1, y = y + s2 - 1, size = s2)
                doDiamond(x = x, y = y, size = s2)
                doDiamond(x = x, y = y + s2 - 1, size = s2)
                doDiamond(x = x + s2 -1, y = y, size = s2)
                doDiamond(x = x + s2 - 1, y = y + s2 - 1, size = s2)

                if (size >= 9) {
                    diamondPass(x = x, y = y, size = s2)
                    diamondPass(x = x, y = y + s2 - 1, size = s2)
                    diamondPass(x = x + s2 - 1, y = y, size = s2)
                    diamondPass(x = x + s2 - 1, y = y + s2 - 1, size = s2)
                }
            }
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
        val result =  value * random() + offset
        terrain[x][y] = result
    }

    open fun get(x: Int, y: Int): Double {
        return  terrain[x][y]!!
    }

    open fun average(vararg points: Int): Double {
        var i = 0
        var result = 0.0
        var divider = 0
        while (i < points.size) {
            if (points[i] < terrain.count() && points[i+1] < terrain[0].count()) {
                result += (terrain[points[i]][points[i + 1]]!!)
                divider += 1
            }
            i += 2
        }
        //return round((result / (points.size / 2)) * 100) / 100
        //return round((result / divider) * 100) / 100
        return result / divider
    }

    open fun random(): Double {
        val rnd = sin(Random.nextDouble(6.28))//TODO set seed?
        randomAmplitudeDecrease-= 1/totalNumber
        return 1.0 + (randomAmplitude - randomAmplitudeDecrease) * rnd
        //val value = randomAmplitude * rnd
        //return value
    }
}