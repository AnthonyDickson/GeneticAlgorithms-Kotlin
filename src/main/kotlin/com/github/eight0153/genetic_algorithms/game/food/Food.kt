package com.github.eight0153.genetic_algorithms.game.food

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
    material: Material,
    /** How filling the piece of food is. */
    // TODO: Not all food is made equal? Make fillingness slightly random?
    private val fillingness: Double = 1.0
) : GameObject(mesh, material, transform, boundingBox) {
    var wasEaten: Boolean = false

    companion object {
        // TODO: Create apple mesh + texture
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

        fun create(fillingness: Double = 1.0, colour: Vector3f? = null): Food {
            val transform = Transform(Vector3f(0.0f, 0.25f, 0.0f), 0.5f)
            return Food(
                transform,
                AABB(transform, Vector3f(transform.scale)),
                createMesh(),
                createMaterial(colour),
                fillingness
            )
        }
    }

    /** Consume this piece of food (removes the game object) and return how filling it is (how much hunger it removes). */
    fun consume(): Double {
        shouldRemove = true
        wasEaten = true

        return fillingness
    }
}