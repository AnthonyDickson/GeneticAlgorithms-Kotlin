package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.joml.Vector2f

/**
 * The interface for game manager.
 *
 * [GameManagerI] objects are managed by a game [Engine].
 * The game [Engine] will call certain functions in the [GameManagerI] for you:
 * 1. The [init] function will be called after OpenGL has been setup.
 * 2. Then in each iteration of the main loop, the [Engine] will call the following functions in the given order:
 *      - [handleInput]
 *      - [update]
 *      - [render]
 * 3. Finally when the program is being closed [cleanup] will be called.
 */
interface GameManagerI {
    /**
     * Initialise the game manager.
     *
     * Initialisation can be done in a constructor but anything relying on OpenGL should be deferred to this
     * method so that the game [Engine] can setup the window and initialise OpenGL beforehand.
     */
    fun init(windowSize: Vector2f, windowName: String)

    /**
     * Handle [keyboard] and [mouse] input and return true if program should continue execution or false if the
     * program should quit.
     */
    fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean

    /** Perform an update step where [delta] is the seconds since the last frame. */
    fun update(delta: Double)

    /** Render the game. */
    fun render()

    /**
     * Perform any necessary cleanup such as freeing native code objects.
     */
    fun cleanup()
}