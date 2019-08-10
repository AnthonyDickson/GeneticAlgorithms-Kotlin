package com.github.eight0153.genetic_algorithms.engine.graphics

import org.joml.Vector3f

class PointLight(
    val colour: Vector3f,
    val position: Vector3f,
    val intensity: Float,
    val attenuation: Attenuation
) {
    /** The position of the [PointLight] in world space (i.e. transformed by the view matrix of the camera). */
    val viewPosition = Vector3f(position)

    data class Attenuation(
        val constant: Float,
        val linear: Float,
        val exponent: Float
    )
}