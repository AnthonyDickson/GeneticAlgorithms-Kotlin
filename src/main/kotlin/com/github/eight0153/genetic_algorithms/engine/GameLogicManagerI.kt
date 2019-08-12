package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler

/**
 * The interface for game logic manager.
 * This is intended to be an object that manages a portion of the game logic under the main game manager.
 */
interface GameLogicManagerI {
    /**
     * A map of key and actions.
     * For example, ("LMD", "Do a barrel roll"
     */
    val controls: Map<String, String>

    /**
     * Handle [keyboard] and [mouse] input and return true if program should continue execution or false if the
     * program should quit.
     */
    fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean

    /** Perform an update step where [delta] is the seconds since the last frame. */
    fun update(delta: Double)

    /**
     * Perform anything that needs to be done after the last [update] and before the next [render] call.
     *
     * This may include remove objects that were marked for removal.
     */
    fun postUpdate()

    /** Render the game. */
    fun render(renderer: Renderer)

    /**
     * Perform any necessary cleanup such as freeing native code objects.
     */
    fun cleanup()
}