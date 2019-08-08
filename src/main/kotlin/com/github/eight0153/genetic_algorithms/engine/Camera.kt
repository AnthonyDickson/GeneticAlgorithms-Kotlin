package com.github.eight0153.genetic_algorithms.engine

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(windowSize: Size) {
    private val fieldOfView = Math.toRadians(60.0).toFloat()
    private val zNear = 0.01f
    private val zFar = 1000f

    val translation: Vector3f = Vector3f()
    val rotation: Vector3f = Vector3f()
    val viewMatrix: Matrix4f
        get() {
            val view = Matrix4f()
            view.rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
            view.rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            view.translate(-translation.x, -translation.y, -translation.z)
            return view
        }
    val projectionMatrix: Matrix4f

    init {
        val aspectRatio = windowSize.width.toFloat() / windowSize.height
        projectionMatrix = Matrix4f().perspective(fieldOfView, aspectRatio, zNear, zFar)
    }

    fun translate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f) {
        if (x != 0.0f) {
            translation.x += sin(Math.toRadians((rotation.y - 90.0f).toDouble())).toFloat() * -1.0f * x
            translation.z += cos(Math.toRadians((rotation.y - 90.0f).toDouble())).toFloat() * x
        }
        if (z != 0.0f) {
            translation.x += sin(Math.toRadians(rotation.y.toDouble())).toFloat() * -1.0f * z
            translation.z += cos(Math.toRadians(rotation.y.toDouble())).toFloat() * z
        }

        translation.y += y
    }

    fun rotate(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f) {
        rotation.add(x, y, z)
    }
}