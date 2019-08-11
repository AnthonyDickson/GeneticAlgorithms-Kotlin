package com.github.eight0153.genetic_algorithms.engine

import org.joml.Vector3f

/** An axis-aligned bounding box. */
class AABB(
    private val transform: Transform = Transform(),
    private val size: Vector3f = Vector3f(1.0f)
) {
    private val min: Vector3f get() = transform.translation
    private val max: Vector3f get() = transform.translation.add(size, Vector3f())

    fun contains(point: Vector3f): Boolean {
        return (min.x <= point.x && point.x <= max.x) &&
                (min.y <= point.y && point.y <= max.y) &&
                (min.z <= point.z && point.z <= max.z)
    }

    fun intersects(other: AABB): Boolean {
        return (min.x <= other.max.x && other.min.x <= max.x) &&
                (min.y <= other.max.y && other.min.y <= max.y) &&
                (min.z <= other.max.z && other.min.z <= max.z)
    }
}