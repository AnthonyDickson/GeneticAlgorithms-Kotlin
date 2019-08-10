package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.graphics.Colour
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil.NULL
import kotlin.math.roundToInt

class Engine(
    private val gameManager: GameManagerI,
    windowName: String,
    /** The width and height of the window in pixels. */
    windowSize: Vector2f = Vector2f(800.0f, 600.0f),
    /** How often to update the display. For example, 60 fps is 1/60 seconds. */
    private val targetFrameTime: Float = 1 / 60f,
    backgroundColour: Colour = Colour()
) {
    /** An error callback that will print GLFW error messages to System.err. */
    private val errorCallback: GLFWErrorCallback? = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))
    private val window: Long
    private val keyboard: KeyboardInputHandler
    private val mouse: MouseInputHandler

    init {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        // Configure our window
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

        window = glfwCreateWindow(windowSize.x.roundToInt(), windowSize.y.roundToInt(), windowName, NULL, NULL)

        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Get the resolution of the primary monitor
        val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        // Center our window
        glfwSetWindowPos(
            window,
            ((videoMode!!.width() - windowSize.x) / 2).roundToInt(),
            ((videoMode.height() - windowSize.y) / 2).roundToInt()
        )

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        // Set the clear color
        glClearColor(backgroundColour.red, backgroundColour.green, backgroundColour.blue, backgroundColour.alpha)

        keyboard = KeyboardInputHandler(window)
        mouse = MouseInputHandler(window)
        gameManager.init(windowSize, windowName)
    }

    private fun mainLoop() {
        var previous = glfwGetTime()
        var processingTime = 0.0

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            val frameStartTime = glfwGetTime()
            val delta = frameStartTime - previous
            previous = frameStartTime
            processingTime += delta

            updateInput(delta)

            // Update game state while we have time.
            while (processingTime >= targetFrameTime) {
                gameManager.update(delta)
                processingTime -= targetFrameTime
            }

            render()
            sync(frameStartTime)
        }

    }

    private fun updateInput(delta: Double) {
        // This must be called before events are polled in order to correctly store the previous state.
        keyboard.update()
        mouse.update()

        // Poll for window events. The key and mouse callbacks will only be invoked during this call.
        glfwPollEvents()

        if (!gameManager.handleInput(delta, keyboard, mouse)) {
            glfwSetWindowShouldClose(window, true)
        }
    }

    /**
     * Render the scene to the window.
     */
    private fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        gameManager.render()

        glfwSwapBuffers(window)
    }

    /** Wait until [targetFrameTime] seconds have passed since [frameStartTime].*/
    private fun sync(frameStartTime: Double) {
        val endTime = frameStartTime + targetFrameTime

        while (glfwGetTime() < endTime) {
            try {
                Thread.sleep(1)
            } catch (ie: InterruptedException) {
            }
        }
    }

    private fun cleanup() {
        gameManager.cleanup()
        ResourcePool.cleanup()
        keyboard.cleanup()
        mouse.cleanup()
        errorCallback?.free()
        glfwDestroyWindow(window)
        glfwTerminate()
    }

    /**
     * Run the game engine its main loop.
     *
     * This call blocks.
     */
    fun run() {
        try {
            mainLoop()
        } finally {
            cleanup()
        }
    }

}