package com.github.eight0153.genetic_algorithms

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

/** A RGBA colour. */
data class Colour(val red: Float = 0f, val green: Float = 0f, val blue: Float = 0f, val alpha: Float = 1f)

/** A 2D size. */
data class Size(val width: Int = 0, val height: Int = 0)

class Engine(
    /** The name to appear on the window handle. */
    private val windowName: String,
    /** The width and height of the window in pixels. */
    private val windowSize: Size = Size(
        800,
        600
    ),
    /** The colour of the background */
    private val backgroundColour: Colour = Colour()
) {

    private var errorCallback: GLFWErrorCallback? = null
    private var keyCallback: GLFWKeyCallback? = null

    private var window: Long? = null

    /**
     * Perform first time initialisation such as creating a window.
     */
    private fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        errorCallback = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        // Configure our window
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)

        // Create the window
        window = glfwCreateWindow(windowSize.width, windowSize.height, windowName, NULL, NULL)
        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        keyCallback = glfwSetKeyCallback(window!!, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GLFW_TRUE == 1)
                }
            }
        })

        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        // Center our window
        glfwSetWindowPos(
            window!!,
            (vidmode!!.width() - windowSize.width) / 2,
            (vidmode.height() - windowSize.height) / 2
        )

        // Make the OpenGL context current
        glfwMakeContextCurrent(window!!)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window!!)
    }

    /**
     * The main loop.
     */
    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        glClearColor(backgroundColour.red, backgroundColour.green, backgroundColour.blue, backgroundColour.alpha)

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window!!)) {

            // Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            // Swap the color buffers
            glfwSwapBuffers(window!!)

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
        }

    }

    /**
     * Setup and run the game engine its main loop.
     */
    fun run() {
        try {
            init()
            loop()
            // Destroy window
            glfwDestroyWindow(window!!)
            keyCallback?.free()
        } finally {
            // Terminate GLFW
            glfwTerminate()
            errorCallback?.free()
        }
    }

}