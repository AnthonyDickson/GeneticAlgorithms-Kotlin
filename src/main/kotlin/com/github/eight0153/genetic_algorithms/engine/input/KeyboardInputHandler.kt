package com.github.eight0153.genetic_algorithms.engine.input

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

class KeyboardInputHandler(window: Long) {
    /** Maps keyboard keys to true if the key is down, false otherwise. */
    private val keyState = Array(GLFW.GLFW_KEY_LAST) { false }
    /** Maps keyboard keys to true if the key was down, false otherwise. */
    private val previousKeyState = Array(GLFW.GLFW_KEY_LAST) { false }
    private val keyCallback: GLFWKeyCallback?

    init {
        keyCallback = GLFW.glfwSetKeyCallback(window, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                when (action) {
                    GLFW.GLFW_PRESS -> {
                        keyState[key] = true
                    }
                    GLFW.GLFW_RELEASE -> {
                        keyState[key] = false
                    }
                }
            }
        })
    }

    /**
     * Update the keyboard state.
     *
     * This must be called before events are polled in order to correctly store the previous state.
     */
    fun update() {
        keyState.copyInto(previousKeyState)
    }

    /** Check if [key] is down. */
    fun isDown(key: Int): Boolean {
        return keyState[key]
    }

    /** Check if [key] is up. */
    fun isUp(key: Int): Boolean {
        return !isDown(key)
    }

    /** Check if [key] is being held down (was down for the current and previous frames). */
    fun isHeldDown(key: Int): Boolean {
        return isDown(key) && previousKeyState[key]
    }

    /** Check if [key] is being held up (was up for the current and previous frames). */
    fun isHeldUp(key: Int): Boolean {
        return isUp(key) && !previousKeyState[key]
    }


    /** Check if [key] is was pressed in the current frame. */
    fun wasPressed(key: Int): Boolean {
        return isDown(key) && !previousKeyState[key]
    }


    /** Check if [key] is was released in the current frame. */
    fun wasReleased(key: Int): Boolean {
        return isUp(key) && previousKeyState[key]
    }

    fun cleanup() {
        keyCallback?.free()
    }
}