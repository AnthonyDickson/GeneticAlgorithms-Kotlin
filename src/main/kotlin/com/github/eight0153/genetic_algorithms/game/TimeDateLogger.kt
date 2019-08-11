package com.github.eight0153.genetic_algorithms.game

class TimeDateLogger(
    private val onLog: () -> String,
    /** How often to print log messages in seconds (can be fractional). */
    private val logFrequency: Double = 1.0
) {
    /** Whether or not to log the timestamp. */
    private var isEnabled = false
    /** The amount of time since the last log message was printed. */
    private var timeSinceLastLog = 0.0

    fun update(delta: Double) {
        timeSinceLastLog += delta

        if (isEnabled && timeSinceLastLog >= logFrequency) {
            timeSinceLastLog = 0.0
            // TODO: Draw this via OpenGL rather than logging
            print("\r${onLog()}")
        }
    }

    fun toggle() {
        isEnabled = !isEnabled
    }
}