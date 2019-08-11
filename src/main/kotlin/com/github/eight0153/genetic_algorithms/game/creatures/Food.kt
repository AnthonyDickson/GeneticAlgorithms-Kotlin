package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.AABB
import com.github.eight0153.genetic_algorithms.engine.GameObject
import com.github.eight0153.genetic_algorithms.engine.ResourcePool
import com.github.eight0153.genetic_algorithms.engine.Transform
import com.github.eight0153.genetic_algorithms.engine.graphics.Material
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import org.joml.Vector3f

class Food(
    transform: Transform,
    boundingBox: AABB = AABB(transform),
    mesh: Mesh,
    material: Material
) : GameObject(mesh, material, transform, boundingBox) {
    companion object {
        fun createMesh(): Mesh {
            return ResourcePool.getMesh("/models/cube.obj")
        }

        fun createMaterial(colour: Vector3f? = null): Material {
            return Material(
                // Red like an apple :)
                colour ?: Vector3f(1.0f, 0.03f, 0.0f),
                0.2f
            )
        }

        fun create(
            position: Vector3f = Vector3f(0.0f, 0.25f, 0.0f),
            colour: Vector3f? = null
        ): Food {
            val transform = Transform(position, 0.5f)
            return Food(
                transform,
                AABB(transform, Vector3f(transform.scale)),
                createMesh(),
                createMaterial(colour)
            )
        }
    }
}