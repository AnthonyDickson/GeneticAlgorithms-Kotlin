package com.github.eight0153.genetic_algorithms.engine

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

open class Transform(
    var position: Vector3f = Vector3f(),
    var scale: Float = 1f,
    var rotation: Quaternionf = Quaternionf()
) {
    val transformMatrix: Matrix4f get() = Matrix4f().translate(position).rotate(rotation).scale(scale)

    open fun rotate(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
        rotation.rotateXYZ(
            Math.toRadians(x.toDouble()).toFloat(),
            Math.toRadians(y.toDouble()).toFloat(),
            Math.toRadians(z.toDouble()).toFloat()
        )
    }

    fun rotate(rotation: Vector3f) {
        rotate(rotation.x, rotation.y, rotation.z)
    }

    fun translate(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
        position.add(x, y, z)
    }

    fun translate(translation: Vector3f) {
        translate(translation.x, translation.y, translation.z)
    }
}