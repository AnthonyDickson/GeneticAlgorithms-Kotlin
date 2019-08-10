package com.github.eight0153.genetic_algorithms

import com.github.eight0153.genetic_algorithms.engine.Engine
import com.github.eight0153.genetic_algorithms.game.GameManager
import org.joml.Vector2f
import org.joml.Vector3f

fun main() {
    val skyBlue = Vector3f(0.52f, 0.82f, 0.9f)
    val windowSize = Vector2f(800.0f, 600.0f)
    val windowName = "Genetic Algorithms"

    val engine = Engine(
        GameManager(),
        windowName,
        windowSize = windowSize,
        backgroundColour = skyBlue
    )
    engine.run()
}

