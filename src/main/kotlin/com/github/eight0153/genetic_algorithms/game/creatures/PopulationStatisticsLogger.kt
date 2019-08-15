package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.Engine
import com.github.eight0153.genetic_algorithms.engine.TickerSubscriberI

// TODO: Abstract loggers (PopulationStatisticsLogger and FrameRateLogger share quite a bit of common structure and code)
// TODO: Plot population statistics
class PopulationStatisticsLogger(
    /** The constant smoothing factor for updating [averagePopulation]. */
    private val alpha: Double = 0.99
) : TickerSubscriberI {
    private var population: Int = 0
    private var populationGrowthRate: Double = 0.0
    private var populationGrowth: Int = 0
    private var totalDeaths: Int = 0
    private var totalBirths: Int = 0
    private var numBirths: Int = 0
    private var numDeaths: Int = 0
    /** Whether or not to log the population statistics. */
    private var isEnabled = false
    /** The exponential moving average of the population. */
    private var averagePopulation = 0.0
    private var averagePopulationGrowth = 0.0
    private var averagePopulationGrowthRate = 0.0

    init {
        assert(0 < alpha && alpha < 1) { "Alpha must be a value in the range (0, 1), but got $alpha." }

        Engine.ticker.subscribe(this)
    }

    fun update(
        population: Int,
        numBirths: Int,
        numDeaths: Int
    ) {
        this.population = population
        this.numDeaths += numDeaths
        this.numBirths += numBirths
        totalDeaths += numDeaths
        totalBirths += numBirths
        populationGrowth += numBirths - numDeaths
        populationGrowthRate = populationGrowth / population.toDouble()

        averagePopulation = alpha * averagePopulation + (1 - alpha) * population
        averagePopulationGrowth = alpha * averagePopulationGrowth + (1 - alpha) * populationGrowth
        averagePopulationGrowthRate = alpha * averagePopulationGrowthRate + (1 - alpha) * populationGrowthRate
    }

    override fun onTick() {
        if (isEnabled) {
            // TODO: Draw this via OpenGL rather than logging

            print(
                "\rPopulation: ${population} (Avg:  %.1f) - ".format(averagePopulation) +
                        "Growth: $populationGrowth (Avg. %.1f) - ".format(averagePopulationGrowth) +
                        "Growth Rate: %.3f (Avg. %.3f) - ".format(populationGrowthRate, averagePopulationGrowthRate) +
                        "Deaths: ${this.numDeaths} ($totalDeaths total) - Births: ${this.numBirths} ($totalBirths total)"
            )

            // Only count number of births and deaths since last update
            this.numBirths = 0
            this.numDeaths = 0
            populationGrowth = 0
        }
    }

    fun toggle() {
        isEnabled = !isEnabled
    }
}