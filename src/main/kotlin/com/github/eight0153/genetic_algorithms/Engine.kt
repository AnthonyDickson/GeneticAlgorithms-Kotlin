package com.github.eight0153.genetic_algorithms

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil.NULL

data class Colour(val red: Float = 0f, val green: Float = 0f, val blue: Float = 0f, val alpha: Float = 1f)
data class Size(val width: Int = 0, val height: Int = 0)

class Engine(
    windowName: String,
    /** The width and height of the window in pixels. */
    windowSize: Size = Size(
        800,
        600
    ),
    /** How often to update the display. For example, 60 fps is 1/60 seconds. */
    private val targetFrameTime: Float = 1 / 60f,
    private val backgroundColour: Colour = Colour()
) {
    /** An error callback that will print GLFW error messages to System.err. */
    private val errorCallback: GLFWErrorCallback? = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))
    private val window: Long
    private val keyboardInput: KeyboardInputHandler
    private val renderer: Renderer
    private val frameRateLogger = FrameRateLogger()

    init {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        // Configure our window
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

        window = glfwCreateWindow(windowSize.width, windowSize.height, windowName, NULL, NULL)

        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Get the resolution of the primary monitor
        val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        // Center our window
        glfwSetWindowPos(
            window,
            (videoMode!!.width() - windowSize.width) / 2,
            (videoMode.height() - windowSize.height) / 2
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

        renderer = Renderer()
        keyboardInput = KeyboardInputHandler(window)
    }

    private fun mainLoop() {
        // Set the clear color
        glClearColor(backgroundColour.red, backgroundColour.green, backgroundColour.blue, backgroundColour.alpha)

        var previous = glfwGetTime()
        var processingTime = 0.0

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            val frameStartTime = glfwGetTime()
            val delta = frameStartTime - previous
            previous = frameStartTime
            processingTime += delta

            handleInput()

            // Update game state while we have time.
            while (processingTime >= targetFrameTime) {
                updateGameState(delta)
                processingTime -= targetFrameTime
            }

            render()
            sync(frameStartTime)
        }

    }

    private fun printControls(lineWidth: Int = 80) {
        val header = "Controls"
        val headerPadding = " ".repeat((lineWidth - header.length - 2) / 2)

        println("#${"=".repeat(lineWidth - 2)}#")

        // Draw box around the header.
        println("$headerPadding+${"-".repeat(header.length)}+")
        println("$headerPadding|$header|")
        println("$headerPadding+${"-".repeat(header.length)}+")

        println("F1: Toggle frame rate logging.")
        println("#${"=".repeat(lineWidth - 2)}#")
    }

    private fun handleInput() {
        // This must be called before events are polled in order to correctly store the previous state.
        keyboardInput.update()
        // TODO: Handle mouse input

        // Poll for window events. The key callback will only be invoked during this call.
        glfwPollEvents()


        if (keyboardInput.wasReleased(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window, true)
        } else if (keyboardInput.wasPressed(GLFW_KEY_F1)) {
            frameRateLogger.toggle()
        }
    }

    private fun updateGameState(delta: Double) {
        frameRateLogger.update(delta)
    }

    /**
     * Render the scene to the window.
     */
    private fun render() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        renderer.render()

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
        renderer.cleanup()
        keyboardInput.cleanup()
        errorCallback?.free()
        glfwDestroyWindow(window)
        glfwTerminate()
    }

    /**
     * Setup and run the game engine its main loop.
     *
     * This call blocks.
     */
    fun run() {
        try {
            printControls()
            mainLoop()
        } finally {
            cleanup()
        }
    }

}