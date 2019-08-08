package com.github.eight0153.genetic_algorithms.engine

import org.joml.Matrix4f

class Camera(windowSize: Size) : Transform() {
    private val fieldOfView = Math.toRadians(60.0).toFloat()
    private val zNear = 0.01f
    private val zFar = 1000f

    val viewMatrix: Matrix4f get() = transformMatrix
    val projectionMatrix: Matrix4f

    init {
        val aspectRatio = windowSize.width.toFloat() / windowSize.height
        projectionMatrix = Matrix4f().perspective(fieldOfView, aspectRatio, zNear, zFar)
    }
}