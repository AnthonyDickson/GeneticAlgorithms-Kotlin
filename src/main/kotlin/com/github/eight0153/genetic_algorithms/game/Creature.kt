package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.graphics.Material
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.random.Random

// TODO: Add genes and other typical genetic operators
class Creature(
    mesh: Mesh,
    material: Material = createMaterial(),
    transform: Transform = Transform()
) : GameObject(mesh, material, transform) {
    companion object {
        fun createMesh(): Mesh {
            return ResourcePool.getMesh("/models/cube.obj")
        }

        fun createMaterial(colour: Vector4f? = null): Material {
            return Material(
                colour ?: Utils.randomColour(),
                0.1f
            )
        }

        /**
         * Create a [Creature] at the given [position]  and with the given [colour].
         * The y component of [position] is ignored.
         */
        fun create(
            position: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
            colour: Vector4f? = null
        ): Creature {
            val creature = Creature(createMesh())

            if (colour != null) {
                creature.material.ambientColour.set(colour)
                creature.material.diffuseColour.set(colour)
                creature.material.specularColour.set(colour)
            }

            creature.transform.translation.set(position.x, 0.0f, position.z)

            return creature
        }
    }

    val replicationChance = 0.015
    val deathChance = 0.01
    val speed = Random.nextDouble(1.0, 4.0).toFloat()
    var isDead = false
    var shouldReplicate: Boolean = false
    private val velocity = Vector3f()
    private val velocityBounds = Bounds3D(Vector3f(-speed), Vector3f(speed))
    private var age = 0.0

    private val lifeExpectancy = 40

    override fun update(delta: Double) {
        super.update(delta)
        age += delta
        shouldReplicate = false

        if (!isDead) {
            velocity.add(
                (Random.nextDouble(-1.0, 1.0)).toFloat(),
                0.0f,
                (Random.nextDouble(-1.0, 1.0)).toFloat()
            )

            velocityBounds.clip(velocity)

            transform.translate((velocity.x * delta).toFloat(), 0.0f, (velocity.z * delta).toFloat())

            if (Random.nextFloat() < deathChance * delta * age / lifeExpectancy) {
                isDead = true
            } else if (Random.nextFloat() < replicationChance * delta * lifeExpectancy / (0.1 * age + lifeExpectancy)) {
                shouldReplicate = true
            }
        }
    }

}