package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.graphics.Material
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_BLUE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_GREEN
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_RED
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.DEATH_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.LIFE_EXPECTANCY
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.METABOLIC_EFFICIENCY
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.REPLICATION_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SPEED
import com.github.eight0153.genetic_algorithms.game.food.Food
import org.joml.Vector3f
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


// TODO: Implement hunger/energy system that acts as a resource pool for performing actions such as moving.
// TODO: Implement a hit points system which decreases with hunger and possibly fighting with other creatures.
// TODO: Make creatures fight each other, or not? Could add pacifist and aggressive traits. Based on these traits
//  creatures may fight over nearby food and potential mates. When fighting certain traits such as size, speed,
//  bravery/cowardice, altruism and greediness could affect the probability that a creature concedes or accepts the 'duel'.
// TODO: Give creatures a sensory range and make them move towards food and perhaps away from threatening creatures
//  (predators,  stronger creatures etc.)
// TODO: Implement a simple search algorithm for creatures. This could be:
//   Pick a point within the creatures sensory range, move to that point, check for food, move towards food if
//   found, otherwise start again.
// TODO: Make creatures with low metabolism (new gene?) to get less energy from food which is spread over a
//  period of time and hunger slower, and creatures with a high metabolism to get a higher instantaneous energy
//  boost from food but hunger quicker.
class Creature(
    private val chromosome: Chromosome = Chromosome(),
    transform: Transform = Transform(),
    boundingBox: AABB = AABB(transform),
    mesh: Mesh = createMesh(),
    material: Material = createMaterial(
        colour = Vector3f(
            chromosome[COLOUR_RED].toFloat(),
            chromosome[COLOUR_GREEN].toFloat(),
            chromosome[COLOUR_BLUE].toFloat()
        )
    )
) : GameObject(mesh, material, transform, boundingBox) {
    companion object {
        fun createMesh(): Mesh {
            return ResourcePool.getMesh("/models/cube.obj")
        }

        fun createMaterial(colour: Vector3f? = null): Material {
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
            colour: Vector3f? = null,
            chromosome: Chromosome? = null
        ): Creature {
            val creature = Creature(chromosome ?: Chromosome())

            if (colour != null) {
                creature.material.ambientColour.set(colour)
                creature.material.diffuseColour.set(colour)
                creature.material.specularColour.set(colour)
            }

            creature.transform.translation.set(position.x, 0.0f, position.z)

            return creature
        }

        private const val MAX_ENERGY = 100.0
    }

    var isDead = false
    var shouldReplicate: Boolean = false
    private val velocity = Vector3f()
    private val velocityBounds = Bounds3D(
        Vector3f(-chromosome[SPEED].toFloat()),
        Vector3f(chromosome[SPEED].toFloat())
    )
    private var age = 0.0
    // TODO: Increase hunger on tick and on movement.
    private var hunger = 0.0
    // TODO: Implement a thirstiness mechanic
//    private var thirstiness = 0.0
    private var energy = MAX_ENERGY
    /** How healthy the creature is as a number from 0.0 (dead) to 1.0 (super healthy).
     *
     * The less health the more likely the creature is to die.
     */
    private var healthiness = 1.0

    override fun update(delta: Double) {
        super.update(delta)
        age += delta

        if (!isDead) {
            velocity.add(
                (Random.nextDouble(-1.0, 1.0)).toFloat(),
                0.0f,
                (Random.nextDouble(-1.0, 1.0)).toFloat()
            )

            velocityBounds.clip(velocity)

            transform.translate((velocity.x * delta).toFloat(), 0.0f, (velocity.z * delta).toFloat())
            // TODO: Decrease energy based on size, mass, speed and metabolic efficiency.

            // TODO: Decrease healthiness with random probability proportional to hunger, low energy and
            //  susceptibility to sickness. Scale this based on low energy and high hunger.

            if (Random.nextFloat() < chromosome[DEATH_CHANCE] * delta * age / chromosome[LIFE_EXPECTANCY] + (1.0 - healthiness) / healthiness) {
                isDead = true
            } else if (Random.nextFloat() < chromosome[REPLICATION_CHANCE] *
                delta * chromosome[LIFE_EXPECTANCY] / (0.1 * age + chromosome[LIFE_EXPECTANCY])
            ) {
                shouldReplicate = true
            }
        }
    }

    fun replicate(): Creature {
        shouldReplicate = false

        val chromosome = Chromosome(chromosome)
        chromosome.mutate()

        return Creature(
            chromosome,
            Transform(transform),
            mesh = createMesh(),
            material = createMaterial(
                colour = Vector3f(
                    chromosome[COLOUR_RED].toFloat(),
                    chromosome[COLOUR_GREEN].toFloat(),
                    chromosome[COLOUR_BLUE].toFloat()
                )
            )
        )
    }

    override fun onCollision(other: GameObject) {
        if (other is Food) {
            // TODO: Allow creatures to stockpile food.
            //  From this you could give creatures altruistic traits to share surplus food with certain creatures
            //  (e.g. kin, species, particular traits). You could also give creatures short-term and long-term planning
            //  traits that change how the creature balances short vs. long term gains. For example, a short-sighted
            //  creature may just eat any food it picks up regardless of any long-term goals
            // TODO: Give a chance for nearby creatures to challenge this creature for the food.
            // TODO: Stockpile food based on how hungry or how much energy it has? Add 'greedy' trait that will eat food
            //  regardless and a 'thrifty' trait that stockpiles food when it doesn't immediately need to eat it?
            eat(other)
        }
    }

    fun eat(food: Food) {
        // Clip to [0, ∞) since you can't get 'unhungry', or maybe you can...
        // TODO: Allow creatures to overeat? This could increase the chance of them getting sick (and then perhaps puke
        //  and lose energy and increase thirstiness?).
        hunger = max(0.0, hunger - food.consume())
        // TODO: Increase creature size when eating food at max energy and min hunger? Greedy creatures get fat???
        energy = min(energy + food.consume() * chromosome[METABOLIC_EFFICIENCY], MAX_ENERGY)
    }

}