package com.github.eight0153.genetic_algorithms

import com.github.eight0153.genetic_algorithms.engine.Engine
import com.github.eight0153.genetic_algorithms.game.GameManager
import org.joml.Vector2f
import org.joml.Vector3f

fun main() {
    val windowSize = Vector2f(800.0f, 600.0f)
    val windowName = "Genetic Algorithms"

    val engine = Engine(
        GameManager(Vector3f(64.0f, 16.0f, 64.0f)),
        windowName,
        windowSize = windowSize
    )
    engine.run()
}

