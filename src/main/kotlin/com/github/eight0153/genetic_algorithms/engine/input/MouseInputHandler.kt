package com.github.eight0153.genetic_algorithms.engine.input

import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorEnterCallback
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback
import org.lwjgl.glfw.GLFWScrollCallback

class MouseInputHandler(window: Long) {
    /** Maps mouse buttons to true if the button is down, false otherwise. */
    private val buttonState = Array(GLFW_MOUSE_BUTTON_LAST) { false }
    /** Maps mouse buttons to true if the button was down, false otherwise. */
    private val previousButtonState = Array(GLFW_MOUSE_BUTTON_LAST) { false }

    val position = Vector2f()
    private val previousPosition = Vector2f()
    /** How much the mouse moved between the current and previous frame. */
    val deltaPosition = Vector2f()

    val scrollOffset = Vector2f()

    private val buttonCallback: GLFWMouseButtonCallback?
    private val cursorEnterCallback: GLFWCursorEnterCallback?
    private val scrollCallback: GLFWScrollCallback?
    private val positionCallback: GLFWCursorPosCallback?

    init {
        buttonCallback = glfwSetMouseButtonCallback(window, object : GLFWMouseButtonCallback() {
            override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
                when (action) {
                    GLFW_PRESS -> buttonState[button] = true
                    GLFW_RELEASE -> buttonState[button] = false
                }
            }
        })

        cursorEnterCallback = glfwSetCursorEnterCallback(window, object : GLFWCursorEnterCallback() {
            override fun invoke(window: Long, entered: Boolean) {
                if (entered) {
                    // Reset the previous and current positions to avoid erroneous jumps in mouse location
                    // TODO: Is this even an actual problem? Might be better off just not dealing with this.
                    previousPosition.set(position)
                    deltaPosition.zero()
                }
            }
        })

        scrollCallback = glfwSetScrollCallback(window, object : GLFWScrollCallback() {
            override fun invoke(window: Long, xoffset: Double, yoffset: Double) {
                scrollOffset.set(xoffset.toFloat(), yoffset.toFloat())
            }
        })

        positionCallback = glfwSetCursorPosCallback(window, object : GLFWCursorPosCallback() {
            override fun invoke(window: Long, xpos: Double, ypos: Double) {
                position.set(xpos.toFloat(), ypos.toFloat())
                previousPosition.sub(position, deltaPosition)
            }
        })
    }

    fun update() {
        buttonState.copyInto(previousButtonState)
        previousPosition.set(position)
        // Reset delta position to prevent the delta being non-zero even if the mouse wasn't moved recently.
        deltaPosition.zero()
        scrollOffset.zero()
    }

    /** Check if [button] is down. */
    fun isDown(button: Int): Boolean {
        return buttonState[button]
    }

    /** Check if [button] is up. */
    fun isUp(button: Int): Boolean {
        return !isDown(button)
    }

    /** Check if [button] is being held down (was down for the current and previous frames). */
    fun isHeldDown(button: Int): Boolean {
        return isDown(button) && previousButtonState[button]
    }

    /** Check if [button] is being held up (was up for the current and previous frames). */
    fun isHeldUp(button: Int): Boolean {
        return isUp(button) && !previousButtonState[button]
    }


    /** Check if [button] is was pressed in the current frame. */
    fun wasPressed(button: Int): Boolean {
        return isDown(button) && !previousButtonState[button]
    }


    /** Check if [button] is was released in the current frame. */
    fun wasReleased(button: Int): Boolean {
        return isUp(button) && previousButtonState[button]
    }

    fun cleanup() {
        buttonCallback?.free()
        scrollCallback?.free()
        positionCallback?.free()
    }
}