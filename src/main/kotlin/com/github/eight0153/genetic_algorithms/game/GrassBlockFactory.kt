package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.GameObject
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.graphics.Texture

class GrassBlockFactory {
    companion object {
        private val mesh: Mesh = Mesh.load("/models/cube.obj")

        init {
            mesh.texture = Texture("/textures/grassblock.png")
        }

        fun create(): GameObject {
            return GameObject(mesh)
        }
    }
}