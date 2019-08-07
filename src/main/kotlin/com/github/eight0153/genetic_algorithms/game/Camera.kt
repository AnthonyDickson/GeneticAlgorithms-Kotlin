package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.Size
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class Camera(windowSize: Size) {
    private val fieldOfView = Math.toRadians(60.0).toFloat()
    private val zNear = 0.01f
    private val zFar = 1000f

    // Take the negative position so that things move in the 'correct' direction.
    // For example, when the y component of the camera pose is increased the 'world' moves downwards.
    val viewMatrix: Matrix4f get() = Matrix4f().translate(position.negate()).rotate(rotation).scale(scale)
    val projectionMatrix: Matrix4f


    var position = Vector3f()
    var scale = 1.0f
    var rotation = Quaternionf()

    init {
        val aspectRatio = windowSize.width.toFloat() / windowSize.height
        projectionMatrix = Matrix4f().perspective(fieldOfView, aspectRatio, zNear, zFar)
    }
}