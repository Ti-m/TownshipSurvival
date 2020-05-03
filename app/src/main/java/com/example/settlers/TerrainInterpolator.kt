package com.example.settlers

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2

open class TerrainInterpolator {
    var randomAmplitude: Double = 0.0

    fun interpolate(terrain: Array<Array<Double>>, size: Int) {
        if (isPowerOfTwo(size)) return
        if (size == 0 || size == 1) return

        doSquare(terrain[0][0].toInt(), terrain[0][0].toInt(), size)
        doDiamond(0,0,size)
    }

    fun isPowerOfTwo(n: Int): Boolean {
        return if (n == 0 || n == 1) false else ceil(log2(n.toDouble())).toInt() ==
                floor(log2(n.toDouble())).toInt()
    }

    open fun doSquare(x: Int, y: Int, size: Int) {
        this.average(0,0,2,0,0,2,2,2)
        this.set(1,1,0.0)
    }

    open fun doDiamond(x: Int, y: Int, size: Int) {
        this.average(0,0,2,0,1,1)
        this.set(1,0,0.0)

        this.average(1,1,0,0,0,2)
        this.set(0,1,0.0)

        this.average(0,2,2,2,1,1)
        this.set(1,2,0.0)

        this.average(1,1,2,0,2,2)
        this.set(2,1, 0.0)
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