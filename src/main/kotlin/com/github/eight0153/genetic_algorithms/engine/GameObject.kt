package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.graphics.Material
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import org.joml.Matrix4f

open class GameObject(
    private val mesh: Mesh,
    var material: Material = Material(),
    val transform: Transform = Transform(),
    val boundingBox: AABB = AABB(transform)
) {
    val modelMatrix: Matrix4f get() = transform.transformMatrix
    var shouldRender = true
    var shouldRemove = false

    open fun render() {
        mesh.render(material)
    }

    open fun update(delta: Double) {}

    open fun onCollision(other: GameObject) {}

    open fun cleanup() {}
}