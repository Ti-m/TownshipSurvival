package com.example.settlers

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2

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
        this.set(u/2, u/2, this.average(0, 0, u, 0, 0, u, u, u))
    }

    open fun doDiamond(x: Int, y: Int, size: Int) {
        val u = size-1

        this.set(u/2,0,this.average(0,0,u,0,u/2,u/2))
        this.set(0,u/2,this.average(u/2,u/2,0,0,0,u))
        this.set(u/2,u, this.average(0,u,u,u,u/2,u/2))
        this.set(u,u/2, this.average(u/2,u/2,u,0,u,u))
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
        while (i < points.size) {
                result += (terrain[points[i]][points[i + 1]]!!)

            i += 2
        }
        return result / (points.size / 2)
    }

    open fun random(): Double {
        return randomAmplitude
    }
}