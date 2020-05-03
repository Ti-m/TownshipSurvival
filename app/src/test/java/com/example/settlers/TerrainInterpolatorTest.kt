package com.example.settlers

import de.bechte.junit.runners.context.HierarchicalContextRunner
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(HierarchicalContextRunner::class)
class TerrainInterpolatorTest {

    private lateinit var actions: String
    private lateinit var dummy: Array<Array<Double>>
    private lateinit var interpolator: TerrainInterpolator

    inner class SimpleValidations {
        @Before
        fun setUp() {
            actions = ""
            dummy = arrayOf(arrayOf())
            interpolator = TerrainInterpolatorSpy()
        }

        @Test
        fun terminalCondition_sizeOne() {
            interpolator.interpolate(dummy, 1)
            assertEquals("", actions)
        }

        @Test
        fun sizeMustBePowerOfTwoPlus1() {
            interpolator.interpolate(dummy, 2)
            //triggers out of bound in case of error
        }

        @Test
        fun Check_isPowerOfTwo() {
            assertEquals(true, interpolator.isPowerOfTwo(2))
            assertEquals(true, interpolator.isPowerOfTwo(4))
            assertEquals(true, interpolator.isPowerOfTwo(8))

            assertEquals(false, interpolator.isPowerOfTwo(1))
            assertEquals(false, interpolator.isPowerOfTwo(7))
            assertEquals(false, interpolator.isPowerOfTwo(18))
        }
    }

    inner class SquareDiamondCoordinateCalculations {
        @Before
        fun setUp() {
            actions = ""
            dummy = Array(3) {
                Array(3) {
                    0.0
                }
            }
            interpolator = TerrainInterpolatorSpy()
        }

        @Test
        fun simpleThreeByThree_SquarePass() {
            interpolator.interpolate(dummy, 3)
            assertThat(
                actions, startsWith(
                    "Square(0,0,3): A([0,0],[2,0],[0,2],[2,2])->[1,1]."
                )
            )
        }

        @Test
        fun simpleThreeByThree_DiamondPass() {
            interpolator.interpolate(dummy, 3)
            assertThat(
                actions, endsWith(
                    "Diamond(0,0,3): " +
                            "A([0,0],[2,0],[1,1])->[1,0]. " +
                            "A([1,1],[0,0],[0,2])->[0,1]. " +
                            "A([0,2],[2,2],[1,1])->[1,2]. " +
                            "A([1,1],[2,0],[2,2])->[2,1]. "
                )
            )
        }

        @Test
        fun DiamondSquare_FirstPass() {
            dummy = Array(5) {
                Array(5) {
                    0.0
                }
            }
            interpolator.interpolate(dummy, 5)
            assertThat(
                actions, startsWith(
                    "Square(0,0,5): A([0,0],[4,0],[0,4],[4,4])->[2,2]. " +
                            "Diamond(0,0,5): " +
                            "A([0,0],[4,0],[2,2])->[2,0]. " +
                            "A([2,2],[0,0],[0,4])->[0,2]. " +
                            "A([0,4],[4,4],[2,2])->[2,4]. " +
                            "A([2,2],[4,0],[4,4])->[4,2]. "
                )
            )
        }
    }

    inner class SquareDiamondRepetition {

        @Before
        fun setUp() {
            actions = ""
            dummy = Array(5) {
                Array(5) {
                    0.0
                }
            }
            interpolator = TerrainInterpolatorDiamondSquareSpy()
        }


        @Test
        fun FiveByFive() {
            interpolator.interpolate(dummy, 5)
            assertThat(
                actions, `is`(
                    "" +
                            "Square(0,0,5) Diamond(0,0,5) " +
                            "Square(0,0,3) Square(0,2,3) Square(2,0,3) Square(2,2,3) " +
                            "Diamond(0,0,3) Diamond(0,2,3) Diamond(2,0,3) Diamond(2,2,3) "
                )
            )
        }
    }

    inner class Averages {
        @Before
        fun setup() {
            dummy = Array(3) {
                Array(3) {
                    0.0
                }
            }
            interpolator = TerrainInterpolator();
        }

        @Test
        fun zero() {
            interpolator.interpolate(dummy, 3)
            assertThat(
                dummy,
                `is`(
                    arrayOf(
                        arrayOf(0.0, 0.0, 0.0),
                        arrayOf(0.0, 0.0, 0.0),
                        arrayOf(0.0, 0.0, 0.0)
                    )
                )
            )
        }

        @Test
        fun allOnes() {
            dummy[2][2] = 1.0
            dummy[0][2] = dummy[2][2]
            dummy[2][0] = dummy[0][2]
            dummy[0][0] = dummy[2][0]
            interpolator.interpolate(dummy, 3)
            assertThat(
                dummy,
                `is`(
                    arrayOf(
                        arrayOf(1.0, 1.0, 1.0),
                        arrayOf(1.0, 1.0, 1.0),
                        arrayOf(1.0, 1.0, 1.0)
                    )
                )
            )
        }

    }

    private inner class TerrainInterpolatorSpy : TerrainInterpolator() {
        override fun doSquare(x: Int, y: Int, size: Int) {
            actions += String.format("Square(%d,%d,%d): ", x, y, size)
            super.doSquare(x, y, size)
        }

        override fun doDiamond(x: Int, y: Int, size: Int) {
            actions += String.format(
                "Diamond(%d,%d,%d): ", x, y, size
            )
            super.doDiamond(x, y, size)
        }

        override fun set(x: Int, y: Int, value: Double) {
            actions += String.format("->[%d,%d]. ", x, y)
        }

        override fun get(x: Int, y: Int): Double {
            return -1.0
        }

        override fun average(vararg points: Int): Double {
            actions += "A("
            var i = 0
            while (i < points.size) {
                actions += String.format(
                    "[%d,%d],", points[i], points[i + 1]
                )
                i += 2
            }
            actions = actions.substring(0, actions.length - 1) + ")"
            return super.average(*points)
        }
    }

    private inner class TerrainInterpolatorDiamondSquareSpy : TerrainInterpolator() {
        override fun doSquare(x: Int, y: Int, size: Int) {
            actions += String.format("Square(%d,%d,%d) ", x, y, size)
        }

        override fun doDiamond(x: Int, y: Int, size: Int) {
            actions += String.format("Diamond(%d,%d,%d) ", x, y, size)
        }

    }

    private inner class TerrainInterpolatorWithFixedRandom : TerrainInterpolator() {
        fun random(): Double {
            return randomAmplitude;
        }
    }
}