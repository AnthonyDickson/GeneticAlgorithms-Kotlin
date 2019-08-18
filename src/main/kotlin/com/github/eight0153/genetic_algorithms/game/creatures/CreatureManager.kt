package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.Engine
import com.github.eight0153.genetic_algorithms.engine.GameLogicManagerI
import com.github.eight0153.genetic_algorithms.engine.Renderer
import com.github.eight0153.genetic_algorithms.engine.TickerSubscriberI
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import com.github.eight0153.genetic_algorithms.game.World
import org.lwjgl.glfw.GLFW

class CreatureManager(
    initialPopulation: Int = 100,
    /** How often (in ticks) a [Census] should be taken. */
    private val ticksPerCensus: Int = 60
) :
    GameLogicManagerI, TickerSubscriberI {

    companion object {
        const val MAX_CREATURES = 5192
    }

    override val controls: Map<String, String>
        get() = mapOf(
            Pair("F3", "Toggle population statistics")
        )

    val creatures = ArrayList<Creature>()
    val species = ArrayList<Species>()
    private val censusDataStore = CensusDataStore()
    private val populationStatisticsLogger = PopulationStatisticsLogger()

    init {
        censusDataStore.init()

        repeat(initialPopulation) {
            creatures.add(Creature.create(World.bounds.sample()))
            assignSpecies(creatures.last())
            censusDataStore.add(creatures.last())
        }

        Engine.ticker.subscribe(this)
    }

    private fun assignSpecies(creature: Creature) {
        var foundSpecies = false

        for (species in species) {
            foundSpecies = species.add(creature)

            if (foundSpecies) {
                break
            }
        }

        if (!foundSpecies) {
            species.add(Species(NameGenerator.uniqueRandom(), creature))
            species.last().add(creature)
            censusDataStore.add(species.last())
        }
    }

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        when {
            keyboard.wasPressed(GLFW.GLFW_KEY_F3) -> populationStatisticsLogger.toggle()
        }

        return true
    }

    override fun onTick() {
        var numBirths = 0
        var numDeaths = 0

        val creatureIterator = creatures.listIterator()

        while (creatureIterator.hasNext()) {
            val creature = creatureIterator.next()

            if (creature.isDead) {
                for (species in species) {
                    if (species.remove(creature)) {
                        break
                    }
                }

                creature.cleanup()
                creatureIterator.remove()
                numDeaths++
            } else if (creature.shouldReplicate && creatures.size + 1 <= MAX_CREATURES) {
                val offspring = creature.replicate()

                creatureIterator.add(offspring)
                assignSpecies(offspring)
                censusDataStore.add(offspring)
                numBirths++
            }
        }

        species.removeAll { it.isExtinct }
        populationStatisticsLogger.update(creatures.size, numBirths, numDeaths)

        if (Engine.ticker.ticks % ticksPerCensus == 0) {
            censusDataStore.add(Census(creatures, species))
        }
    }

    override fun update(delta: Double) {
        creatures.forEach {
            it.update(delta)
            World.bounds.clip(it.transform.translation)
        }
    }

    override fun postUpdate() {}

    override fun render(renderer: Renderer) {
        renderer.render(creatures)
    }

    override fun cleanup() {
        creatures.forEach { it.cleanup() }
        censusDataStore.cleanup()

        Engine.ticker.unsubscribe(this)
    }
}