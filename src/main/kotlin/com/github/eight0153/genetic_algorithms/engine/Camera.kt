package com.github.eight0153.genetic_algorithms.engine

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(windowSize: Size) {
    private val fieldOfView = Math.toRadians(60.0).toFloat()
    private val zNear = 0.01f
    private val zFar = 1000f


    var transform = Transform()
    val viewMatrix: Matrix4f get() = transform.transformMatrix
    val projectionMatrix: Matrix4f

    init {
        val aspectRatio = windowSize.width.toFloat() / windowSize.height
        projectionMatrix = Matrix4f().perspective(fieldOfView, aspectRatio, zNear, zFar)
    }

    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
        // Take the negative position so that things move in the 'correct' direction.
        // For example, when the y component of the camera pose is increased the 'world' moves downwards.
        transform.translate(-x, -y, -z)
    }

    fun translate(translation: Vector3f) {
        translate(translation.x, translation.y, translation.z)
    }
}