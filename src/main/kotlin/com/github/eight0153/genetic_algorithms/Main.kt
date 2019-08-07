package com.github.eight0153.genetic_algorithms

import com.github.eight0153.genetic_algorithms.engine.Colour
import com.github.eight0153.genetic_algorithms.engine.Engine
import com.github.eight0153.genetic_algorithms.engine.Size
import com.github.eight0153.genetic_algorithms.game.GameManager

fun main() {
    val skyBlue = Colour(0.52f, 0.82f, 0.9f)
    val windowSize = Size(800, 600)
    val windowName = "Genetic Algorithms"

    val engine = Engine(
        GameManager(),
        windowName,
        windowSize = windowSize,
        backgroundColour = skyBlue
    )
    engine.run()
}

