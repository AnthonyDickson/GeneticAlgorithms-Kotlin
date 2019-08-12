package com.github.eight0153.genetic_algorithms.game.food

import com.github.eight0153.genetic_algorithms.engine.GameLogicManagerI
import com.github.eight0153.genetic_algorithms.engine.Renderer
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.joml.Vector3f

class FoodManager : GameLogicManagerI {
    val food = ArrayList<Food>()

    init {
        food.add(
            Food.create(Vector3f(0.0f, -0.25f, 5.0f))
        )
    }

    override val controls: Map<String, String>
        get() = emptyMap()

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        return true
    }

    override fun update(delta: Double) {

    }

    override fun postUpdate() {
        val foodIterator = food.iterator()

        while (foodIterator.hasNext()) {
            val food = foodIterator.next()

            if (food.shouldRemove) {
                food.cleanup()
                foodIterator.remove()
            }
        }
    }

    override fun render(renderer: Renderer) {
        renderer.render(food)
    }

    override fun cleanup() {
        food.forEach { it.cleanup() }
    }
}