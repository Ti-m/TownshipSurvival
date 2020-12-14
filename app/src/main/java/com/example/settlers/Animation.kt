package com.example.settlers


abstract class Animation : GameObject() {
    var progress: AnimationProgress? = AnimationProgress.One
    abstract fun calcNextAnimation()
}

enum class AnimationProgress {
    One, Two, Three, Four
}

class ExplosionAnimation : Animation() {

    override fun calcNextAnimation() {
        progress = when (progress) {
            //This is a three step animation
            AnimationProgress.One -> AnimationProgress.Two
            AnimationProgress.Two -> AnimationProgress.Three
            AnimationProgress.Three -> null
            else -> null
        }
    }
}

class AnimationManager {
    fun nextAnimation(current: Animation) {
        current.calcNextAnimation()
    }
}