package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.GameObject
import com.github.eight0153.genetic_algorithms.engine.ResourcePool
import com.github.eight0153.genetic_algorithms.engine.graphics.Material
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh

object GrassBlockFactory {
    fun createMaterial(): Material {
        return Material(ResourcePool.getTexture("/textures/grassblock.png"))
    }

    fun createMesh(): Mesh {
        return ResourcePool.getMesh("/models/cube.obj")
    }

    fun create(): GameObject {
        return GameObject(createMesh(), createMaterial())
    }
}