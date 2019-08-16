package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.BoundsND
import kotlin.random.Random

class Chromosome {
    // TODO: Add gene for sensory range
    // TODO: Make separate genes for daytime sensory range and nighttime sensory range
    // TODO: Add diseases that affect creatures with certain genes. Would be interesting to see if natural selection takes care of things.
    // TODO: Add gene that controls how strong a creature's immune system is (i.e. lowers chance of getting sick
    //  or healthiness decreasing)?
    companion object {
        // Define mappings here
        /** How likely a creature is to replicate. */
        const val REPLICATION_CHANCE = 0x0
        /** How likely a creature is to die.
         *
         * Can be thought of modelling things like susceptibility to illness and stubbed toes (ouch!).
         */
        const val DEATH_CHANCE = 0x1
        /** How likely each gene is to undergo mutation during replication. */
        const val MUTATION_CHANCE = 0x2
        /** How fast a creature can move. Gotta go fast. Zoom zoom! */
        const val SPEED = 0x3
        /** How large a creature is. */
        const val SIZE = 0x4
        /** The red component of a creature's colour. */
        const val COLOUR_RED = 0x5
        /** The green component of a creature's colour. */
        const val COLOUR_GREEN = 0x6
        /** The blue component of a creature's colour. */
        const val COLOUR_BLUE = 0x7
        /** How efficient a creature is at digesting food. */
        const val METABOLIC_EFFICIENCY = 0x8
        /** The distance a creature can sense objects. */
        const val SENSORY_RANGE = 0x9
        /** How greedy a creature is. */
        const val GREEDINESS = 0xa
        /** How likely a creature is likely to prioritise long-term planning over short-term planning. */
        const val THRIFTINESS = 0xb

        // TODO: Find a way to automatically calculate how many genes there are.
        const val NUM_GENES = 12

        val geneValueBounds = BoundsND(NUM_GENES)

        init {
            geneValueBounds.min[REPLICATION_CHANCE] = 0.0
            geneValueBounds.max[REPLICATION_CHANCE] = 1.0

            geneValueBounds.min[DEATH_CHANCE] = 0.001
            geneValueBounds.max[DEATH_CHANCE] = 1.0

            geneValueBounds.min[MUTATION_CHANCE] = 0.0
            geneValueBounds.max[MUTATION_CHANCE] = 1.0

            geneValueBounds.min[SPEED] = 0.5
            geneValueBounds.max[SPEED] = 8.0

            geneValueBounds.min[SIZE] = 0.5
            geneValueBounds.max[SIZE] = 2.0

            geneValueBounds.min[COLOUR_RED] = 0.0
            geneValueBounds.max[COLOUR_RED] = 1.0
            geneValueBounds.min[COLOUR_GREEN] = 0.0
            geneValueBounds.max[COLOUR_GREEN] = 1.0
            geneValueBounds.min[COLOUR_BLUE] = 0.0
            geneValueBounds.max[COLOUR_BLUE] = 1.0

            geneValueBounds.min[METABOLIC_EFFICIENCY] = 0.1
            geneValueBounds.max[METABOLIC_EFFICIENCY] = 2.0

            geneValueBounds.min[SENSORY_RANGE] = 0.0
            geneValueBounds.max[SENSORY_RANGE] = 32.0

            geneValueBounds.min[GREEDINESS] = 0.0
            geneValueBounds.max[GREEDINESS] = 1.0

            geneValueBounds.min[THRIFTINESS] = 0.0
            geneValueBounds.max[THRIFTINESS] = 1.0
        }
    }

    private var genes: Array<Double> = Array(NUM_GENES) { 0.0 }

    constructor() {
        genes = geneValueBounds.sample()
    }

    constructor(chromosome: Chromosome) {
        chromosome.genes.copyInto(genes)
    }

    // TODO: Add crossover operator

    // TODO: Make mutations around the current value, rather than outright replacing it. This avoids situations where a
    //  small creature may give birth to a creature many times its size.
    fun mutate() {
        for (i in 0 until NUM_GENES) {
            if (Random.nextDouble() < genes[MUTATION_CHANCE]) {
                genes[i] = Random.nextDouble(geneValueBounds.min[i], geneValueBounds.max[i])
            }
        }
    }

    operator fun get(i: Int): Double {
        return genes[i]
    }

}