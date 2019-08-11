package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.BoundsND
import kotlin.random.Random

class Chromosome {
    // TODO: Add gene for size
    // TODO: Add gene for sensory range
    // TODO: Make separate genes for daytime sensory range and nighttime sensory range
    companion object {
        // Define mappings here
        const val NUM_GENES = 8

        val geneValueBounds = BoundsND(NUM_GENES)

        const val REPLICATION_CHANCE = 0x0
        const val DEATH_CHANCE = 0x1
        const val MUTATION_CHANCE = 0x2
        const val LIFE_EXPECTANCY = 0x3
        const val SPEED = 0x4
        const val COLOUR_RED = 0x5
        const val COLOUR_GREEN = 0x6
        const val COLOUR_BLUE = 0x7

        init {
            geneValueBounds.min[REPLICATION_CHANCE] = 0.01
            geneValueBounds.max[REPLICATION_CHANCE] = 0.1

            geneValueBounds.min[DEATH_CHANCE] = 0.01
            geneValueBounds.max[DEATH_CHANCE] = 1.0

            geneValueBounds.min[MUTATION_CHANCE] = 0.0
            geneValueBounds.max[MUTATION_CHANCE] = 1.0

            geneValueBounds.min[LIFE_EXPECTANCY] = 1.0
            geneValueBounds.max[LIFE_EXPECTANCY] = 100.0

            geneValueBounds.min[SPEED] = 1.0
            geneValueBounds.max[SPEED] = 4.0

            geneValueBounds.min[COLOUR_RED] = 0.0
            geneValueBounds.max[COLOUR_RED] = 1.0
            geneValueBounds.min[COLOUR_GREEN] = 0.0
            geneValueBounds.max[COLOUR_GREEN] = 1.0
            geneValueBounds.min[COLOUR_BLUE] = 0.0
            geneValueBounds.max[COLOUR_BLUE] = 1.0
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