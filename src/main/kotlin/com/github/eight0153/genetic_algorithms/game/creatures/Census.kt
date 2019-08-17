package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_BLUE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_GREEN
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_RED
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.DEATH_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.GREEDINESS
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.METABOLIC_EFFICIENCY
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.MUTATION_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.REPLICATION_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SENSORY_RANGE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SIZE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SPEED
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.THRIFTINESS
import java.lang.Math.pow
import kotlin.math.sqrt

// TODO: Export census data to a server and view via web page (give option to view absolute numbers and stacked percentages)
class Census(val population: List<Creature>) {
    companion object {
        var censusNumber = 0
    }

    init {
        censusNumber++
    }

    private val metrics = mapOf(
        Pair("Fertility Rate", REPLICATION_CHANCE),
        Pair("Mortality Risk", DEATH_CHANCE),
        Pair("Mutation Chance", MUTATION_CHANCE),
        Pair("Speed", SPEED),
        Pair("Size", SIZE),
        Pair("Colour Red", COLOUR_RED),
        Pair("Colour Green", COLOUR_GREEN),
        Pair("Colour Blue", COLOUR_BLUE),
        Pair("Metabolic Efficiency", METABOLIC_EFFICIENCY),
        Pair("Sensory Range", SENSORY_RANGE),
        Pair("Greediness", GREEDINESS),
        Pair("Thriftiness", THRIFTINESS)
    )

    fun printSummary() {
        val formatString = "%-24s: %05.2f - %05.2f - %05.2f - %05.2f - %05.2f"

        println("Census #$censusNumber")
        println("Population: ${population.size}")
        println(" ".repeat(26) + "min   - max   - median - mean  - std.")

        val values = Array(population.size) { 0.0 }

        for ((metric, gene) in metrics) {
            population.forEachIndexed { index, creature -> values[index] = creature.chromosome[gene] }
            val summary = getSummaryOf(values)
            println(
                formatString.format(
                    metric,
                    summary.min,
                    summary.max,
                    summary.median,
                    summary.mean,
                    summary.standardDeviation
                )
            )
        }
    }

    private fun getSummaryOf(values: Array<Double>): Summary {
        val min = values.min()!!
        val max = values.max()!!
        val mean = values.average()
        val sumSquareDifference = values.reduce { acc, x -> acc + pow(x - mean, 2.0) }
        val variance = sumSquareDifference / population.size
        val standardDeviation = sqrt(variance)
        val sortedValues = values.sorted()
        val median = if (values.size % 2 == 0) {
            0.5 * (sortedValues[sortedValues.size / 2] + sortedValues[sortedValues.size / 2 + 1])
        } else {
            sortedValues[sortedValues.size / 2]
        }

        return Summary(min, max, mean, standardDeviation, median)
    }

    data class Summary(
        val min: Double,
        val max: Double,
        val mean: Double,
        val standardDeviation: Double,
        val median: Double
    )
}