package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.GameObject
import com.github.eight0153.genetic_algorithms.engine.ResourcePool
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh

object GrassBlockFactory {
    fun createMesh(): Mesh {
        val mesh = ResourcePool.getMesh("/models/cube.obj")
        mesh.texture = ResourcePool.getTexture("/textures/grassblock.png")

        return mesh
    }

    fun create(): GameObject {
        return GameObject(createMesh())
    }
}