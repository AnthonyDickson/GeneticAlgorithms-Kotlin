package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.GameObject
import com.github.eight0153.genetic_algorithms.engine.Utils
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.graphics.Texture

class GrassBlock {
    companion object {
        fun createMesh(): Mesh {
            val vertices = floatArrayOf(
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f
            )

            val textureCoordinates = floatArrayOf(
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                .5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For texture coordinates on top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For texture coordinates on right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For texture coordinates on left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For texture coordinates on bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f
            )
            val indices = intArrayOf(
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                6, 15, 14, 6, 14, 4,
                // Bottom face
                19, 18, 16, 19, 16, 17,
                // Back face
                7, 6, 4, 7, 4, 5
            )
            return Mesh(
                vertices, textureCoordinates, indices,
                Texture(Utils.getResourcePath("/textures/grassblock.png"))
            )
        }

        fun createGameObject(): GameObject {
            return GameObject(createMesh())
        }
    }
}