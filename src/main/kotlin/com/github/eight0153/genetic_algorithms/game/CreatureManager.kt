package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.Bounds3D
import com.github.eight0153.genetic_algorithms.engine.GameLogicManagerI
import com.github.eight0153.genetic_algorithms.engine.Renderer
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import com.github.eight0153.genetic_algorithms.game.creatures.Creature
import org.lwjgl.glfw.GLFW

class CreatureManager(private val worldBounds: Bounds3D = Bounds3D(), initialPopulation: Int = 100) :
    GameLogicManagerI {
    override val controls: Map<String, String>
        get() = mapOf(
            Pair("F3", "Toggle population statistics")
        )

    companion object {
        const val MAX_CREATURES = 512
    }

    private val creatures = ArrayList<Creature>()
    private val populationStatisticsLogger = PopulationStatisticsLogger()

    init {
        repeat(initialPopulation) { creatures.add(Creature.create(worldBounds.sample())) }
    }

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        when {
            keyboard.wasPressed(GLFW.GLFW_KEY_F3) -> populationStatisticsLogger.toggle()
        }

        return true
    }

    override fun update(delta: Double) {
        creatures.forEach {
            it.update(delta)
            worldBounds.clip(it.transform.translation)
        }

        var numBirths = 0
        var numDeaths = 0

        for (creature in creatures.filter { it.isDead }) {
            creatures.remove(creature)
            creature.cleanup()
            numDeaths++
        }

        for (creature in creatures.filter { it.shouldReplicate }) {
            if (creatures.size + numBirths - numDeaths >= MAX_CREATURES) {
                break
            }

            creatures.add(creature.replicate())
            numBirths++
        }

        populationStatisticsLogger.update(delta, creatures.size, numBirths, numDeaths)
    }

    override fun render(renderer: Renderer) {
        renderer.render(creatures)
    }

    override fun cleanup() {
        creatures.forEach { it.cleanup() }
    }
}