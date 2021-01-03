package com.example.settlers

//An animation is an overlay picture which is printed in the next Game tick. An Animation is always
// a cycle of multiple animations which are run in a certain order
abstract class Animation : GameObject() {
    //A list of animation parts which will run one after another
    //Always run element 0. Pop afterwards
    abstract var parts: MutableList<AnimationPart>
}

//Baseclass for the steps of the animation
abstract class AnimationPart

class ExplosionAnimationOne : AnimationPart()
class ExplosionAnimationTwo : AnimationPart()
class ExplosionAnimationThree : AnimationPart()

class ExplosionAnimation : Animation() {
    override var parts: MutableList<AnimationPart> = mutableListOf(
        ExplosionAnimationOne(),
        ExplosionAnimationTwo(),
        ExplosionAnimationThree()
    )
}

class ShootAnimationOne : AnimationPart()
class ShootAnimationTwo : AnimationPart()

class ShootAnimation: Animation() {
    override var parts: MutableList<AnimationPart> = mutableListOf(
        ShootAnimationOne(),
        ShootAnimationTwo()
    )
}

class ProjectileAnimationOne : AnimationPart()
class ProjectileAnimationTwo : AnimationPart()

class ProjectileAnimation: Animation() {
    override var parts: MutableList<AnimationPart> = mutableListOf(
        ProjectileAnimationOne(),
        ProjectileAnimationTwo()
    )
}