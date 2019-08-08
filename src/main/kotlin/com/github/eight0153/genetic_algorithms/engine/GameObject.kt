package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import org.joml.Matrix4f

class GameObject(val mesh: Mesh, val transform: Transform = Transform()) {
    val modelMatrix: Matrix4f get() = transform.transformMatrix
    var shouldRender = true

    fun render() {
        mesh.render()
    }

    fun update(delta: Double) {}

    fun cleanup() {
        mesh.cleanup()
    }
}