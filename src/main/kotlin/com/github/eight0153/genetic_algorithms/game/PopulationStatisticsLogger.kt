package com.github.eight0153.genetic_algorithms.game

// TODO: Abstract loggers (PopulationStatisticsLogger and FrameRateLogger share quite a bit of common structure and code)
// TODO: Plot population statistics
class PopulationStatisticsLogger(
    /** How often to print log messages in seconds (can be fractional). */
    private val logFrequency: Double = 1.0,
    /** The constant smoothing factor for updating [averagePopulation]. */
    private val alpha: Double = 0.99
) {
    private var populationGrowthRate: Double = 0.0
    private var populationGrowth: Int = 0
    private var totalDeaths: Int = 0
    private var totalBirths: Int = 0
    private var numBirths: Int = 0
    private var numDeaths: Int = 0
    /** Whether or not to log the frame time. */
    private var isEnabled = true
    /** The amount of time since the last log message was printed. */
    private var timeSinceLastLog = 0.0
    /** The exponential moving average of the population. */
    private var averagePopulation = 0.0
    private var averagePopulationGrowth = 0.0
    private var averagePopulationGrowthRate = 0.0

    init {
        assert(0 < alpha && alpha < 1) { "Alpha must be a value in the range (0, 1), but got $alpha." }
    }

    fun update(
        delta: Double,
        population: Int,
        numBirths: Int,
        numDeaths: Int
    ) {
        timeSinceLastLog += delta


        this.numDeaths += numDeaths
        this.numBirths += numBirths
        totalDeaths += numDeaths
        totalBirths += numBirths
        populationGrowth += numBirths - numDeaths
        populationGrowthRate = populationGrowth / population.toDouble()

        averagePopulation = alpha * averagePopulation + (1 - alpha) * population
        averagePopulationGrowth = alpha * averagePopulationGrowth + (1 - alpha) * populationGrowth
        averagePopulationGrowthRate = alpha * averagePopulationGrowthRate + (1 - alpha) * populationGrowthRate


        if (isEnabled && timeSinceLastLog >= logFrequency) {
            timeSinceLastLog = 0.0
            // TODO: Draw this via OpenGL rather than logging

            print(
                "\rPopulation: $population (Avg:  %.1f) - ".format(averagePopulation) +
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