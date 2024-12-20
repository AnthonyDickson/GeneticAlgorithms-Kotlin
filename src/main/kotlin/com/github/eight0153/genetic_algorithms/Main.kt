package com.github.eight0153.genetic_algorithms

import com.github.eight0153.genetic_algorithms.engine.Engine
import com.github.eight0153.genetic_algorithms.game.GameManager
import org.joml.Vector2f
import org.joml.Vector3f

// Make program configurable via config files
fun main() {
    val windowSize = Vector2f(800.0f, 600.0f)
    val windowName = "Genetic Algorithms"

    val engine = Engine(
        GameManager(Vector3f(128.0f, 32.0f, 128.0f)),
        windowName,
        windowSize = windowSize
    )
    engine.run()
}

