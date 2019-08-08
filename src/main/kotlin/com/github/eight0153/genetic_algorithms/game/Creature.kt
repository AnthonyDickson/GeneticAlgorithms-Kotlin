package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.GameObject
import com.github.eight0153.genetic_algorithms.engine.Transform
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.graphics.createCubeMesh
import org.joml.Vector3f
import kotlin.random.Random

// TODO: Add genes and other typical genetic operators
class Creature(mesh: Mesh = createCubeMesh(), transform: Transform = Transform()) : GameObject(mesh, transform) {
    companion object {
        private val random = Random(42)
    }

    val replicationChance = 0.015

    val deathChance = 0.01
    val speed = random.nextDouble(1.0, 4.0).toFloat()
    var isDead = false
    var shouldReplicate: Boolean = false
    private val velocity = Vector3f()
    private var age = 0.0

    init {
        // TODO: Add bounds to init parameters
        transform.translate(
            (random.nextInt(-30, 30) * speed),
            0.5f,
            (random.nextInt(-30, 30) * speed)
        )
    }

    private val lifeExpectancy = 40

    override fun update(delta: Double) {
        super.update(delta)
        age += delta
        shouldReplicate = false

        if (!isDead) {
            velocity.add(
                (random.nextDouble(-1.0, 1.0)).toFloat(),
                0.0f,
                (random.nextDouble(-1.0, 1.0)).toFloat()
            )

            when {
                velocity.x < -speed -> velocity.x = -speed
                velocity.x > speed -> velocity.x = speed
                velocity.z < -speed -> velocity.z = -speed
                velocity.z > speed -> velocity.z = speed
            }

            transform.translate((velocity.x * delta).toFloat(), 0.0f, (velocity.z * delta).toFloat())

            when {
                // TODO: Get rid of these magic numbers
                transform.translation.x < -50.0f -> transform.translation.x = -50.0f
                transform.translation.x > 50.0f -> transform.translation.x = 50.0f
                transform.translation.z < -50.0f -> transform.translation.z = -50.0f
                transform.translation.z > 50.0f -> transform.translation.z = 50.0f
            }

            if (random.nextFloat() < deathChance * delta * age / lifeExpectancy) {
                isDead = true
            } else if (random.nextFloat() < replicationChance * delta * lifeExpectancy / (0.1 * age + lifeExpectancy)) {
                shouldReplicate = true
            }
        }
    }

}