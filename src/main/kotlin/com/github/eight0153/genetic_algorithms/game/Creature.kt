package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.Bounds3D
import com.github.eight0153.genetic_algorithms.engine.GameObject
import com.github.eight0153.genetic_algorithms.engine.ResourcePool
import com.github.eight0153.genetic_algorithms.engine.Transform
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import org.joml.Vector3f
import kotlin.random.Random

// TODO: Add genes and other typical genetic operators
class Creature(mesh: Mesh, transform: Transform = Transform()) : GameObject(mesh, transform) {
    companion object {
        fun createMesh(): Mesh {
            return ResourcePool.getMesh("/models/cube.obj")
        }

        /**
         * Create a [Creature] at the given [position]  and with the given [colour].
         * The y component of [position] is ignored.
         */
        fun create(
            position: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
            colour: Vector3f? = null
        ): Creature {
            val creature = Creature(createMesh())
            creature.colour = colour ?: Vector3f(
                Random.Default.nextFloat(),
                Random.Default.nextFloat(),
                Random.Default.nextFloat()
            )

            creature.transform.translation.set(position.x, 0.0f, position.z)

            return creature
        }
    }

    override var colour = Vector3f(0.8f, 0.1f, 0.1f)
    override var isTextured = false

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