package com.github.eight0153.genetic_algorithms.engine.graphics

import org.joml.Vector3f
import kotlin.math.cos

data class SpotLight(
    val pointLight: PointLight,
    val direction: Vector3f,
    /**
     * The cosine of the angle of the cone of light that this [SpotLight] emits.
     *
     * For example, if you want a spot light that emits light in a 90 degree cone you should set [cosineConeAngle] to
     * something like cos(Math.toDegrees(90)).
     */
    val cosineConeAngle: Float = cos(Math.PI / 2.0).toFloat()
) {
    /** The direction of the [SpotLight] in world space (i.e. transformed by the view matrix of the camera). */
    val viewDirection = Vector3f(direction)
}