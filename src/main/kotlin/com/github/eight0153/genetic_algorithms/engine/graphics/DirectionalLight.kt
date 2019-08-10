package com.github.eight0153.genetic_algorithms.engine.graphics

import org.joml.Vector3f

data class DirectionalLight(
    val colour: Vector3f,
    val direction: Vector3f,
    val intensity: Float
) {
    val viewDirection: Vector3f = Vector3f(direction)
}