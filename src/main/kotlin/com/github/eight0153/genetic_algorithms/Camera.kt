package com.github.eight0153.genetic_algorithms

import org.joml.Matrix4f

class Camera(windowSize: Size) {
    private val fieldOfView = Math.toRadians(60.0).toFloat()
    private val zNear = 0.01f
    private val zFar = 1000f
    private val viewMatrix: Matrix4f
    private val projectionMatrix: Matrix4f

    init {
        val aspectRatio: Float = windowSize.width.toFloat() / windowSize.height
        projectionMatrix = Matrix4f().perspective(fieldOfView, aspectRatio, zNear, zFar)
        viewMatrix = Matrix4f()
    }
}