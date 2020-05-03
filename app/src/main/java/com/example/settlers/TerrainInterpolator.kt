package com.example.settlers

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2

open class TerrainInterpolator {
    var randomAmplitude: Double = 0.0

    fun interpolate(terrain: Array<Array<Double>>, size: Int) {
        if (isPowerOfTwo(size)) return
        if (size == 0 || size == 1) return

        doSquare(0, 0, size)
        doDiamond(0,0, size)
        if (size == 5) {
            val s2 = ceil(size.toDouble()/2).toInt()
            val s2h = ceil(s2.toDouble()/2).toInt()
            doSquare(0, 0, s2)
            doSquare(0, s2h, s2)
            doSquare(s2h, 0, s2)
            doSquare(s2h, s2h, s2)
            doDiamond(0,0, s2)
            doDiamond(0,s2h, s2)
            doDiamond(s2h,0, s2)
            doDiamond(s2h,s2h, s2)
        }
    }

    fun isPowerOfTwo(n: Int): Boolean {
        return if (n == 0 || n == 1) false else ceil(log2(n.toDouble())).toInt() ==
                floor(log2(n.toDouble())).toInt()
    }

    open fun doSquare(x: Int, y: Int, size: Int) {
        val u = size-1
        this.average(0, 0, u, 0, 0, u, u, u)
        this.set(u/2, u/2, 0.0)
    }

    open fun doDiamond(x: Int, y: Int, size: Int) {
        val u = size-1
        this.average(0,0,u,0,u/2,u/2)
        this.set(u/2,0,0.0)

        this.average(u/2,u/2,0,0,0,u)
        this.set(0,u/2,0.0)

        this.average(0,u,u,u,u/2,u/2)
        this.set(u/2,u,0.0)

        this.average(u/2,u/2,u,0,u,u)
        this.set(u,u/2, 0.0)
    }

    open fun set(x: Int, y: Int, value: Double) {
    }

    open fun get(x: Int, y: Int): Double {
        return 0.0
    }

    open fun average(vararg points: Int): Double {
        return 0.0
    }
}