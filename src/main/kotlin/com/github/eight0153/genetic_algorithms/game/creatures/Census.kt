package com.github.eight0153.genetic_algorithms.game.creatures

// TODO: Export census data to a server and view via web page (give option to view absolute numbers and stacked percentages)
class Census(
    population: List<Creature>
) {
    companion object {
        private var censusCount = 0
        val nextId: Int get() = ++censusCount
    }

    val id: Int = nextId
    val population: MutableList<Creature> = ArrayList(population.size)

    init {
        this.population.addAll(population)
    }
}