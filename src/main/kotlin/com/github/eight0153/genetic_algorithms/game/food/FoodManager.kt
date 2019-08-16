package com.github.eight0153.genetic_algorithms.game.food

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.joml.Vector3f

object FoodManager : GameLogicManagerI, TickerSubscriberI {
    private var maxFood: Int = 0
    private var spawnRate: Int = 1
    private var foodFillingness: Double = 1.0

    lateinit var food: ArrayList<Food>
    private lateinit var worldBounds: Bounds3D

    fun init(
        worldBounds: Bounds3D,
        /** The maximum number of pieces of food to have spawned at once. */
        maxFood: Int = 10,
        spawnRate: Int = 1,
        foodFillingness: Double = 1.0
    ) {
        this.worldBounds = worldBounds
        this.maxFood = maxFood
        this.spawnRate = spawnRate
        this.foodFillingness = foodFillingness

        food = ArrayList(maxFood)
        repeat(maxFood) { spawnFood() }

        Engine.ticker.subscribe(this)
    }

    private fun spawnFood() {
        val position = worldBounds.sample()
        val food = Food.create(fillingness = foodFillingness)
        food.transform.translate(x = position.x, z = position.z)

        this.food.add(food)
    }

    fun closestFoodTo(point: Vector3f, range: Float): List<Pair<Food, Float>> {
        val foodInRange = food.map { it to it.transform.translation.distance(point) }

        return foodInRange.filter { it.second <= range }.sortedBy { it.second }.take(3)
    }

    override val controls: Map<String, String>
        get() = emptyMap()

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        return true
    }

    override fun onTick() {
        var added = 0

        while (added < spawnRate && food.size < maxFood) {
            spawnFood()
            added++
        }
    }

    override fun update(delta: Double) {}

    override fun postUpdate() {
        val foodIterator = food.iterator()

        while (foodIterator.hasNext()) {
            val food = foodIterator.next()

            if (food.shouldRemove || food.wasPickedUp) {
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