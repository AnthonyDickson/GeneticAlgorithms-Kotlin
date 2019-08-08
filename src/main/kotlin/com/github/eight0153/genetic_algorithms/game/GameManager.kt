package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.GL_LINES

class GameManager : GameManagerI {
    private val frameRateLogger = FrameRateLogger()

    private lateinit var renderer: Renderer
    private lateinit var gameObjects: Array<GameObject>

    private lateinit var camera: Camera
    private val cameraMoveSpeed = 20f
    private val cameraRotateSpeed = 20f
    private val cameraZoomSpeed = 10f

    override fun init(windowSize: Size, windowName: String) {
        camera = Camera(windowSize)
        resetCamera()

        val cube = GameObject(
            Mesh(
                arrayOf(
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f
                ),
                arrayOf(
                    0.5f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.0f, 0.5f,
                    0.0f, 0.5f, 0.5f,
                    0.5f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.0f, 0.5f,
                    0.0f, 0.5f, 0.5f
                ),
                arrayOf(
                    0, 1, 2, 1, 2, 3, // Front face
                    0, 1, 4, 1, 4, 5, // Left face
                    2, 3, 6, 3, 6, 7, // Right face
                    1, 3, 5, 3, 5, 7, // Top face
                    0, 2, 4, 2, 4, 6, // Bottom face
                    4, 5, 6, 5, 6, 7  // Back face
                )
            )
        )
        cube.transform.translate(y = 0.5f)

        val ground = GameObject(
            Mesh(
                arrayOf(
                    0.0f, 0.0f, 0.0f,
                    100.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 100.0f,
                    100.0f, 0.0f, 100.0f
                ),
                arrayOf(
                    0.5f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.0f, 0.5f,
                    0.0f, 0.5f, 0.5f
                ),
                arrayOf(0, 1, 2, 1, 2, 3)
            )
        )
        ground.transform.translate(-50.0f, 0.0f, -50.0f)

        val axes = GameObject(
            Mesh(
                // Duplicate origin point so that colour is uniform for the entire line and we get a:
                // - red line for the x-axis
                // - green line for the y-axis
                // - blue line for the z-axis
                arrayOf(
                    0.0f, 0.0f, 0.0f,
                    100.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 100.0f, 0.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 100.0f
                ),
                arrayOf(
                    0.5f, 0.0f, 0.0f,
                    0.5f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.5f, 0.0f,
                    0.0f, 0.0f, 0.5f,
                    0.0f, 0.0f, 0.5f
                ),
                arrayOf(0, 1, 2, 3, 4, 5),
                GL_LINES
            )
        )

        gameObjects = arrayOf(cube, axes, ground)
        renderer = Renderer(camera)
        printInfo(windowName)
    }

    fun resetCamera() {
        camera.translation.zero()
        camera.translate(y = 1.0f, z = 5.0f)
        camera.rotation.zero()
        camera.rotate(15.0f)
    }

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        when {
            keyboard.wasReleased(GLFW.GLFW_KEY_ESCAPE) -> return false
            keyboard.wasPressed(GLFW.GLFW_KEY_F1) -> frameRateLogger.toggle()
            keyboard.wasPressed(GLFW.GLFW_KEY_F2) -> gameObjects[1].shouldRender = !gameObjects[1].shouldRender
            keyboard.wasPressed(GLFW.GLFW_KEY_F3) -> resetCamera()
        }

        // Camera Translation
        when {
            // Translation on the camera pose along the x and y axes needs to be flipped for objects to appear to move
            // 'naturally'.
            keyboard.isDown(GLFW.GLFW_KEY_W) -> camera.translate(z = -cameraMoveSpeed * delta.toFloat())
            keyboard.isDown(GLFW.GLFW_KEY_S) -> camera.translate(z = cameraMoveSpeed * delta.toFloat())
        }
        // Split up x-axis and y-axis movement so that you can move along both axes simultaneously
        when {
            keyboard.isDown(GLFW.GLFW_KEY_A) -> camera.translate(x = -cameraMoveSpeed * delta.toFloat())
            keyboard.isDown(GLFW.GLFW_KEY_D) -> camera.translate(x = cameraMoveSpeed * delta.toFloat())
            mouse.isHeldDown(GLFW.GLFW_MOUSE_BUTTON_MIDDLE) -> camera.translate(
                x = mouse.deltaPosition.x * delta.toFloat(),
                y = -mouse.deltaPosition.y * delta.toFloat()
            )
        }

        when {
            keyboard.isDown(GLFW.GLFW_KEY_Q) -> camera.translate(y = cameraMoveSpeed * delta.toFloat())
            keyboard.isDown(GLFW.GLFW_KEY_E) -> camera.translate(y = -cameraMoveSpeed * delta.toFloat())
        }

        // Camera rotation
        if (mouse.isHeldDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) camera.rotate(
            // Moving the mouse up/down orbits about the x-axis.
            x = -mouse.deltaPosition.y * delta.toFloat() * cameraRotateSpeed,

            // Moving the mouse left/right orbits anti-clockwise/clockwise around the y-axis.
            y = -mouse.deltaPosition.x * delta.toFloat() * cameraRotateSpeed
        )

        // TODO: Implement zoom (moving forwards and backwards in the direction the camera is pointing.

        return true
    }

    override fun update(delta: Double) {
        for (gameObject in gameObjects) {
            gameObject.update(delta)
        }

        frameRateLogger.update(delta)
    }

    override fun render() {
        renderer.render(gameObjects)
    }

    override fun cleanup() {
        for (gameObject in gameObjects) {
            gameObject.cleanup()
        }

        renderer.cleanup()
    }

    private fun printInfo(programName: String, lineWidth: Int = 80) {
        val titlePadding = " ".repeat((lineWidth - programName.length - 2) / 2)

        println("#${"=".repeat(lineWidth - 2)}#")

        // Draw box around the title.
        println("$titlePadding+${"-".repeat(programName.length)}+")
        println("$titlePadding|$programName|")
        println("$titlePadding+${"-".repeat(programName.length)}+")

        // Print the info text and center it.
        val infoMessages = arrayOf(
            "This just renders a cube at this point.",
            "Nothing too interesting.",
            "I know, the title is bit of a let down, isn't it?",
            "But at least there is this sort of fancy text formatting, right??"
        )

        for (message in infoMessages) {
            println(" ".repeat((lineWidth - message.length) / 2) + message)
        }
        println()

        // Print the controls
        val header = "Controls"
        println(header)
        println("-".repeat(header.length))

        val spacingFormat = "%-15s %s"

        val controls = mapOf(
            Pair("F1:", "Toggle frame rate logging"),
            Pair("F2:", "Toggle XYZ axes"),
            Pair("F3:", "Reset the camera"),
            Pair("MMB:", "Pan the camera"),
            Pair("RMB:", "Rotate the camera"),
            Pair("Q/E:", "Move the camera up/down"),
            Pair("W/A/S/D:", "Move the camera forward/left/backward/right")
        )

        for ((key, function) in controls) {
            println(spacingFormat.format(key, function))
        }
        println("#${"=".repeat(lineWidth - 2)}#")
    }
}