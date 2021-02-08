package com.example.settlers.util

import kotlin.random.Random

class TestDoubleRandom : Random() {
    override fun nextBits(bitCount: Int): Int = 4

    override fun nextInt(): Int = 50
}