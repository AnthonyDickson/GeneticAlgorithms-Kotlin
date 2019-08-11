package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.GameLogicManagerI
import com.github.eight0153.genetic_algorithms.engine.Renderer
import com.github.eight0153.genetic_algorithms.engine.graphics.DirectionalLight
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.glClearColor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** Manages the lighting and timing for a night and day cycle. */
class DayNightCycleManager(
    /** How long a day should be, in seconds (can be fractional). */
    private val dayLength: Float,
    private val ambientLight: Vector3f,
    /** The directional light representing the sun/moon. */
    private val directionalLight: DirectionalLight
) : GameLogicManagerI {
    companion object {
        // Some colours.
        val skyBlue = Vector3f(0.6f, 0.8f, 0.9f)
        private val sortOfYellow = Vector3f(0.9f, 0.85f, 0.7f)
        private val royalOrange = Vector3f(0.9f, 0.4f, 0.3f)
        private val nightBlack = Vector3f(0.05f, 0.05f, 0.1f)
        private val sortOfBlue = Vector3f(0.8f, 0.8f, 0.9f)

        // Some colours associated with their type of light and time of the day
        val ambientNight: Vector3f = nightBlack.mul(4.0f, Vector3f())
        val ambientDay: Vector3f = sortOfYellow.mul(0.5f, Vector3f())

        val sunDawn = royalOrange
        val sunMidday = sortOfYellow
        val sunDusk = royalOrange
        /** Technically the colour of the moonlight, not the sun ¯\_(ツ)_/¯ */
        val sunMidnight = sortOfBlue

        val skyDawn = royalOrange
        val skyMidday = skyBlue
        val skyDusk = royalOrange
        val skyMidnight = nightBlack

        // Some time definitions
        private const val MONTHS_PER_YEAR = 12
        const val DAYS_PER_MONTH = 28 // Every month is February now, I do hope the creatures like February...
        const val DAYS_PER_YEAR = DAYS_PER_MONTH * MONTHS_PER_YEAR
        const val HOURS_PER_DAY = 24
        const val MINUTES_PER_HOUR = 60
        const val SECONDS_PER_MINUTE = 60

        const val MIDNIGHT: Double = 0.0
        const val DAWN: Double = Math.PI / 2
        const val MIDDAY: Double = Math.PI
        const val DUSK: Double = 3 * Math.PI / 2

        const val ONE_DAY: Double = (2 * Math.PI)

    }

    /** Copy of the original ambient light object */
    private val originalAmbientLight = Vector3f(ambientLight)
    /** Copy of the original directional light object */
    private val originalDirectionalLight = DirectionalLight(directionalLight)
    /** The direction of the sun in 3D space. */
    private val sunPosition: Vector3f = Vector3f(directionalLight.direction)

    /**
     * Measures the time of day like a clock.
     *
     * Midnight (12 a.m.) is zero degrees and midday is 180 degrees.
     */
    private var timeOfDay: Double = MIDNIGHT
    /** The time since the world was created. */
    private var timeSinceEpoch = 0.0

    // Getters for date.
    private val years: Double get() = days / DAYS_PER_YEAR
    private val months: Double get() = days / DAYS_PER_MONTH
    private val days: Double get() = timeSinceEpoch / ONE_DAY
    private val hours: Double get() = (days % 1) * HOURS_PER_DAY
    private val minutes: Double get() = (hours % 1) * MINUTES_PER_HOUR
    private val seconds: Double get() = (minutes % 1) * SECONDS_PER_MINUTE

    /** The date (years, months, days since epoch). */
    private val date: String get() = "%04d/%02d/%02d".format(years.toInt(), months.toInt(), days.toInt())

    /** The time (hours since midnight). */
    private val time: String get() = "%02d:%02d:%05.2f".format(hours.toInt(), minutes.toInt(), seconds)

    /** The date and time since epoch. */
    private val timestamp: String get() = "$date $time"

    /** Update the time of day and lighting? */
    private var shouldUpdate = true
    private val timeDateLogger = TimeDateLogger({ this.timestamp })

    override val controls: Map<String, String>
        get() = mapOf(
            Pair("F4", "Toggle Timestamp Logging"),
            Pair("F5", "Toggle Day/Night Cycle"),
            Pair("F6", "Toggle Day/Night (Only when day/night cycle is OFF)")
        )

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        when {
            keyboard.wasPressed(GLFW.GLFW_KEY_F4) -> timeDateLogger.toggle()
            keyboard.wasPressed(GLFW.GLFW_KEY_F5) -> toggle()
            keyboard.wasPressed(GLFW.GLFW_KEY_F6) -> {
                if (!shouldUpdate) {
                    timeOfDay = if (timeOfDay == MIDDAY) MIDNIGHT else MIDDAY
                }
            }
        }

        return true
    }

    /** Toggle the day/night cycle lighting effects. */
    private fun toggle() {
        shouldUpdate = !shouldUpdate

        if (shouldUpdate) {
            timeOfDay = timeSinceEpoch % ONE_DAY
        } else {
            reset()
        }
    }

    /** Restore the lighting to the original settings. */
    private fun reset() {
        timeOfDay = MIDDAY
        glClearColor(skyBlue.x, skyBlue.y, skyBlue.z, 1.0f)
        ambientLight.set(originalAmbientLight)
        directionalLight.direction.set(originalDirectionalLight.direction)
        directionalLight.colour.set(originalDirectionalLight.colour)
        directionalLight.intensity = originalDirectionalLight.intensity
    }

    override fun update(delta: Double) {
        step(delta * ONE_DAY / dayLength)
        timeDateLogger.update(delta)
    }

    /** Move time forward by the given [amount], which should be given as a ratio of 2 * [Math.PI]. */
    private fun step(amount: Double) {
        timeSinceEpoch += amount

        if (shouldUpdate) {
            timeOfDay = (timeOfDay + amount) % ONE_DAY
        }

        sunPosition.set(
            Vector3f(
                sin(timeOfDay).toFloat(),
                cos(timeOfDay).toFloat(),
                directionalLight.direction.z
            )
        )

        val skyColour = timeOfDayLerp(skyDawn, skyMidday, skyDusk, skyMidnight)
        val lightColour = timeOfDayLerp(sunDawn, sunMidday, sunDusk, sunMidnight)
        val ambientColour = ambientDay.lerp(ambientNight, (sunPosition.y + 1) / 2, Vector3f())

        // Update lighting
        ambientLight.set(ambientColour)
        directionalLight.direction.set(sunPosition.x, abs(sunPosition.y), sunPosition.z)
        directionalLight.colour.set(lightColour)
        // TODO: Rewrite with more general wave formula?? It would be nice to have a proper function rather than
        //  some magic numbers...
        // Scale and shift s.t. intensity in the interval [0.25, 1.0]
        directionalLight.intensity = -sunPosition.y * 0.375f + 0.625f
        glClearColor(skyColour.x, skyColour.y, skyColour.z, 1.0f)
    }

    /** Linearly interpolate between colours based on the time of day. */
    private fun timeOfDayLerp(
        dawnColour: Vector3f,
        middayColour: Vector3f,
        duskColour: Vector3f,
        midnightColour: Vector3f
    ): Vector3f {
        return when {
            timeOfDay < DAWN -> midnightColour.lerp(dawnColour, 1.0f - sunPosition.y, Vector3f())
            timeOfDay < MIDDAY -> dawnColour.lerp(middayColour, abs(sunPosition.y), Vector3f())
            timeOfDay < DUSK -> middayColour.lerp(duskColour, 1.0f - abs(sunPosition.y), Vector3f())
            else -> duskColour.lerp(midnightColour, sunPosition.y, Vector3f())
        }
    }

    override fun render(renderer: Renderer) {}
    override fun cleanup() {}
}