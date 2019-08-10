package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.graphics.Texture

object ResourcePool {
    private val textures: MutableMap<String, Texture> = HashMap()
    private val meshes: MutableMap<String, Mesh> = HashMap()

    fun getTexture(resourceID: String): Texture {
        return textures.getOrPut(resourceID) { Texture(resourceID) }
    }

    fun getMesh(resourceID: String): Mesh {
        return meshes.getOrPut(resourceID) { Mesh.load(resourceID) }
    }

    fun cleanup() {
        textures.values.forEach { it.cleanup() }
        meshes.values.forEach { it.cleanup() }
    }
}