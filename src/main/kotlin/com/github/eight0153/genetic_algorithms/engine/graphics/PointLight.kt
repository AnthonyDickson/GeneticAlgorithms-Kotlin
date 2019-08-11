package com.github.eight0153.genetic_algorithms.engine.graphics

import org.joml.Vector3f

data class PointLight(
    val colour: Vector3f,
    val position: Vector3f,
    val intensity: Float = 1.0f,
    val attenuation: Attenuation = Attenuation()
) {
    /** The position of the [PointLight] in world space (i.e. transformed by the view matrix of the camera). */
    val viewPosition = Vector3f(position)
}