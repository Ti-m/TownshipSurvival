package com.example.settlers

//An animation is an overlay picture which is printed in the next Game tick. An Animation is always
// a cycle of multiple animations which are run in a certain order
abstract class Animation : GameObject() {
    var progress: AnimationProgress? = AnimationProgress.One
        protected set //Should only be set by itself
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

class ShootAnimation: Animation() {
    override fun calcNextAnimation() {
        progress = when (progress) {
            //This is a three step animation
            AnimationProgress.One -> AnimationProgress.Two
            AnimationProgress.Two -> null
            else -> null
        }
    }
}

class ProjectileAnimation: Animation() {
    override fun calcNextAnimation() {
        progress = when (progress) {
            //This is a three step animation
            AnimationProgress.One -> AnimationProgress.Two
            AnimationProgress.Two -> null
            else -> null
        }
    }

}

//The AnimationManager handles the logic to calculate the next animation.
class AnimationManager {
    //This method sets the next Picture in an animation cycle based on the previous
    fun nextAnimation(current: Animation) {
        current.calcNextAnimation()
    }
}