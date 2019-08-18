package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.graphics.Material
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.game.World
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_BLUE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_GREEN
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_RED
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.DEATH_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.GREEDINESS
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.METABOLIC_EFFICIENCY
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.REPLICATION_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SENSORY_RANGE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SIZE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SPEED
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.THRIFTINESS
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.geneValueBounds
import com.github.eight0153.genetic_algorithms.game.food.Food
import com.github.eight0153.genetic_algorithms.game.food.FoodManager
import org.joml.Vector3f
import java.lang.Math.pow
import kotlin.math.log
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
// TODO: Make creatures with low metabolism (new gene?) to get less energy from food which is spread over a
//  period of time and hunger slower, and creatures with a high metabolism to get a higher instantaneous energy
//  boost from food but hunger quicker.
// TODO: Make creatures mate with each other. Once a creature has built up a stockpile of food it could then start
//  looking for a mate.
// TODO: Add names for creatures (include regnal numbers for same name in same species)
// TODO: Make ID for creatures to be species + name + regnal number
class Creature(
    val chromosome: Chromosome = Chromosome(),
    transform: Transform = Transform(scale = chromosome[SIZE].toFloat()),
    boundingBox: AABB = AABB(transform),
    mesh: Mesh = createMesh(),
    material: Material = createMaterial(
        colour = Vector3f(
            chromosome[COLOUR_RED].toFloat(),
            chromosome[COLOUR_GREEN].toFloat(),
            chromosome[COLOUR_BLUE].toFloat()
        )
    )
) : GameObject(mesh, material, transform, boundingBox), TickerSubscriberI {
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
         * Create a [Creature] at the given [position]  and with the given [chromosome].
         * The y component of [position] is ignored.
         */
        fun create(
            position: Vector3f = Vector3f(0.0f, 0.0f, 0.0f),
            chromosome: Chromosome? = null
        ): Creature {
            val creature = Creature(chromosome ?: Chromosome())

            creature.transform.translation.set(position.x, 0.5f * (creature.transform.scale - 1.0f), position.z)

            return creature
        }

        private const val STARTING_ENERGY = 100.0
        private var creatureCounter = 0
        val nextId: Int get() = ++creatureCounter
    }

    val id = nextId
    var species: Species? = null

    var age = 0
    private var hunger = 0.0
    // TODO: Implement a thirstiness mechanic?
//    private var thirstiness = 0.0
    private var energy = STARTING_ENERGY
    private val maxEnergy = STARTING_ENERGY + chromosome[SIZE] * STARTING_ENERGY
    private var target: Food? = null

    private var destination: Vector3f? = null
    // TODO: Allow a creature to stockpile more than one food
    private var heldFood: Food? = null

    var isDead: Boolean = false

    private val isGreedy: Boolean = chromosome[GREEDINESS] > 0.5 * geneValueBounds.max[GREEDINESS]

    /** Creatures will replicate subject to stockpiled food, energy levels and random chance. */
    val shouldReplicate: Boolean
        get() = energy > STARTING_ENERGY && Random.nextFloat() < chromosome[REPLICATION_CHANCE]

    // TODO: Make thrifty creatures stockpile more food before resting
    /** Creatures will rest if they have saved up enough energy, have some food in reserve and they are not greedy. */
    private val shouldRest: Boolean
        get() = energy > STARTING_ENERGY && heldFood != null && !isGreedy

    /** Creatures will look for food if it does not have a valid target or destination. */
    private val shouldLookForFood: Boolean
        get() = (target == null || target!!.wasEaten || target!!.wasPickedUp)

    /** A creature should scout if there is no food nearby (i.e. it cannot find food nearby). */
    private val shouldScout: Boolean
        get() = target == null && destination == null

    /** Creatures will move if they have a valid destination. */
    private val shouldMove: Boolean
        get() = destination != null

    init {
        Engine.ticker.subscribe(this)
    }

    override fun update(delta: Double) {
        super.update(delta)

        // Stop creatures from moving to spot were food used to be and then all stacking up on each other
        if (target != null && (target!!.wasPickedUp || target!!.wasEaten)) {
            rest()
        }

        if (shouldRest) {
            rest()
        } else if (shouldLookForFood) {
            lookForFood()

            if (shouldScout) {
                scout()
            }
        }

        if (shouldMove) {
            moveTowardsDestination(delta)
        }
    }

    /** Do nothing. */
    private fun rest() {
        target = null
        destination = null
    }

    /**
     * Locate the nearest [Food] within the creature's [SENSORY_RANGE] and set the creature's [target] and
     * [destination] if found.
     */
    private fun lookForFood() {
        val closestFood: List<Pair<Food, Float>> = FoodManager.closestFoodTo(
            transform.translation,
            chromosome[SENSORY_RANGE].toFloat()
        )

        if (closestFood.isNotEmpty()) {
            val i = Random.nextInt(closestFood.size)
            target = closestFood[i].first
            destination = closestFood[i].first.transform.translation
        } else {
            target = null
        }
    }

    /**
     * Move to a random point at the perimeter of the creature's [SENSORY_RANGE] in hope that food will come into
     * range.
     */
    private fun scout() {
        val x =
            (transform.translation.x + (if (Random.nextFloat() < 0.5f) -1 else 1) * (chromosome[SENSORY_RANGE]).toFloat() - 1.0f)
        val z =
            (transform.translation.z + (if (Random.nextFloat() < 0.5f) -1 else 1) * (chromosome[SENSORY_RANGE]).toFloat() - 1.0f)

        destination = Vector3f(x, transform.translation.y, z)
        World.bounds.clip(destination!!, boundingBox)
    }

    /** Move the creature towards its destination by a step scaled by [delta]. */
    private fun moveTowardsDestination(delta: Double) {
        val step = Vector3f()
        destination!!.sub(transform.translation, step)

        step.y = 0.0f

        if (step.length() > chromosome[SPEED] * delta) {
            step.normalize((chromosome[SPEED] * delta).toFloat())
        }

        transform.translate(step)
        // TODO: Decrease energy based on size, mass and speed.
        energy -= delta * pow(step.length().toDouble(), 2.0) * pow(chromosome[SIZE], 2.0)

        if (transform.translation.distance(destination!!) < 0.01f) {
            rest()
        }
    }

    override fun onTick() {
        age += 1

        if (energy <= 0 || Random.nextFloat() < chromosome[DEATH_CHANCE]) {
            isDead = true
            return
        }

        if (heldFood != null && energy < 0.5 * STARTING_ENERGY) {
            eat(heldFood!!)
            heldFood!!.cleanup()
            heldFood = null
        }

        hunger += 1.0 * chromosome[METABOLIC_EFFICIENCY]
        energy -= log(1 + hunger, 10.0)
    }

    fun replicate(): Creature {
        val chromosome = Chromosome(chromosome)
        chromosome.mutate()

        val creature = create(transform.translation, chromosome)

        // Birthing costs energy for the parent(s)
        energy -= 0.5 * STARTING_ENERGY
        // Newborns are born slightly weak
        creature.energy = 0.5 * STARTING_ENERGY

        // Non-greedy parent will share stockpiled food with their children
        if (heldFood != null && !isGreedy) {
            giveFoodTo(creature)
        }

        return creature
    }

    private fun giveFoodTo(creature: Creature) {
        creature.heldFood = heldFood
        heldFood = null
    }

    override fun onCollision(other: GameObject) {
        if (other is Food && !other.wasEaten && !other.wasPickedUp) {
            // TODO: Allow creatures to stockpile more than one piece of food.
            //  From this you could give creatures altruistic traits to share surplus food with certain creatures
            //  (e.g. kin, species, particular traits). You could also give creatures short-term and long-term planning
            //  traits that change how the creature balances short vs. long term gains. For example, a short-sighted
            //  creature may just eat any food it picks up regardless of any long-term goals
            // TODO: Give a chance for nearby creatures to challenge this creature for the food.
            if (energy < STARTING_ENERGY || Random.nextFloat() < 1.0 - chromosome[THRIFTINESS]) {
                eat(other)
            } else {
                heldFood = other
                other.wasPickedUp = true
            }
        }
    }

    private fun eat(food: Food) {
        val noms = food.consume()
        // Clip to [0, ∞) since you can't get 'unhungry', or maybe you can...
        // TODO: Allow creatures to overeat? This could increase the chance of them getting sick (and then perhaps puke
        //  and lose energy and increase thirstiness?).
        hunger = max(0.0, hunger - noms)
        // TODO: Increase creature size when eating food at max energy and min hunger? Greedy creatures get fat???
        energy = min(energy + noms * chromosome[METABOLIC_EFFICIENCY], maxEnergy)
    }

    override fun cleanup() {
        Engine.ticker.unsubscribe(this)
    }

    /** Calculate a measure of similarity between this [Creature] and [other] that gives a value in the range `[-∞, 1]`. */
    fun similarity(other: Creature): Double {
        return chromosome.similarity(other.chromosome)
    }

}