package com.github.eight0153.genetic_algorithms.engine

import org.joml.Vector3f
import kotlin.random.Random

class Bounds1D(val min: Float, val max: Float) {

    /** Check if the bounds contains [value]. */
    fun contains(value: Float): Boolean {
        return min < value && value < max
    }


    /** Get [value] constrained to the range ([min], [max]). */
    fun clip(value: Float): Float {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }

    /** Generate a random point that lies within the [Bounds1D]. */
    fun sample(): Float {
        return Random.nextDouble(min.toDouble(), max.toDouble()).toFloat()
    }
}

class Bounds3D(
    val min: Vector3f = Vector3f(Float.NEGATIVE_INFINITY),
    val max: Vector3f = Vector3f(Float.POSITIVE_INFINITY)
) {
    fun contains(position: Vector3f): Boolean {
        return min.x < position.x && position.x < max.x &&
                min.y < position.y && position.y < max.y &&
                min.z < position.z && position.z < max.z
    }

    fun clip(vector: Vector3f): Vector3f {
        vector.max(min)
        vector.min(max)

        return vector
    }

    /** Generate a random point that lies within the [Bounds3D]. */
    fun sample(): Vector3f {
        return Vector3f(
            Random.Default.nextDouble(min.x.toDouble(), max.x.toDouble()).toFloat(),
            Random.Default.nextDouble(min.y.toDouble(), max.y.toDouble()).toFloat(),
            Random.Default.nextDouble(min.z.toDouble(), max.z.toDouble()).toFloat()
        )
    }
}