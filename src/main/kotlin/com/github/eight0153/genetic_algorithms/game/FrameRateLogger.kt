package com.github.eight0153.genetic_algorithms.game

class FrameRateLogger(
    /** How often to print log messages in seconds (can be fractional). */
    private val logFrequency: Double = 1.0,
    /** The constant smoothing factor for updating [frameRateAverage]. */
    private val alpha: Double = 0.99
) {
    /** Whether or not to log the frame time. */
    private var isEnabled = false
    /** The amount of time since the last log message was printed. */
    private var timeSinceLastLog = 0.0
    /** The exponential moving average of the frame rate. */
    private var frameRateAverage = 0.0

    init {
        assert(0 < alpha && alpha < 1) { "Alpha must be a value in the range (0, 1), but got $alpha." }
    }

    fun update(delta: Double) {
        timeSinceLastLog += delta
        val frameRate = 1f / delta

        frameRateAverage = alpha * frameRateAverage + (1 - alpha) * frameRate

        if (isEnabled && timeSinceLastLog >= logFrequency) {
            timeSinceLastLog = 0.0
            // TODO: Draw this via OpenGL rather than logging
            print("\rFrames per second: %.2f - Average: %.2f".format(frameRate, frameRateAverage))
        }
    }

    fun toggle() {
        isEnabled = !isEnabled
    }
}