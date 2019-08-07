package com.github.eight0153.genetic_algorithms

fun main() {
    val skyBlue = Colour(0.52f, 0.82f, 0.9f)
    val windowSize = Size(800, 600)
    val windowName = "Genetic Algorithms"

    val gameManager = GameManager()
    val engine = Engine(gameManager, windowName, windowSize = windowSize, backgroundColour = skyBlue)
    engine.run()
}

