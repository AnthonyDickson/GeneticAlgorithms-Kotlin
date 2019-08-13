package com.github.eight0153.genetic_algorithms.game.food

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler

// TODO: Spawn food periodically
class FoodManager(
    private val worldBounds: Bounds3D,
    /** The maximum number of pieces of food to have spawned at once. */
    private val maxFood: Int = 10
) : GameLogicManagerI, TickerSubscriberI {
    val food = ArrayList<Food>(maxFood)

    init {
        repeat(maxFood) { food[it] = Food.create() }

        Engine.ticker.subscribe(this)
    }

    override val controls: Map<String, String>
        get() = emptyMap()

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        return true
    }

    override fun onTick() {
        if (food.size < maxFood) {
            val position = worldBounds.sample()
            val food = Food.create()
            food.transform.translate(x = position.x, z = position.z)

            this.food.add(food)
        }
    }

    override fun update(delta: Double) {}

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