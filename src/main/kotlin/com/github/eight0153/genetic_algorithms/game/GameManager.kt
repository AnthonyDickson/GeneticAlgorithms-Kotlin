package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

// TODO: Add creatures back in
class GameManager : GameManagerI {
    private val frameRateLogger = FrameRateLogger()

    private lateinit var gameObjects: List<GameObject>
    private lateinit var renderer: Renderer
    private lateinit var camera: Camera

    private val cameraMoveSpeed = 20f
    private val cameraRotateSpeed = 20f

    override fun init(
        windowSize: Size,
        windowName: String,
        worldSize: Size
    ) {
        camera = Camera(windowSize)
        resetCamera()

        gameObjects = ArrayList()

        for (row in 0 until worldSize.width) {
            for (col in 0 until worldSize.depth) {
                val block = GrassBlockFactory.create()
                block.transform.translate(
                    row.toFloat() - worldSize.width / 2.0f,
                    -0.5f,
                    col.toFloat() - worldSize.depth / 2.0f
                )
                (gameObjects as ArrayList<GameObject>).add(block)
            }
        }

        val creatureMesh = Creature.createMesh()
        creatureMesh.colour = Vector3f(0.8f, 0.1f, 0.1f)
        val creature = Creature(creatureMesh)

        (gameObjects as ArrayList<GameObject>).add(creature)

        renderer = Renderer(camera)
        printInfo(windowName)
    }

    private fun resetCamera() {
        camera.translation.zero()
        camera.translate(y = 4.0f)
        camera.rotation.zero()
    }

    override fun handleInput(delta: Double, keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        when {
            keyboard.wasReleased(GLFW.GLFW_KEY_ESCAPE) -> return false
            keyboard.wasPressed(GLFW.GLFW_KEY_F1) -> frameRateLogger.toggle()
            keyboard.wasPressed(GLFW.GLFW_KEY_F2) -> resetCamera()
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
            keyboard.isDown(GLFW.GLFW_KEY_Q) || keyboard.isDown(GLFW.GLFW_KEY_LEFT_SHIFT) -> {
                camera.translate(y = -cameraMoveSpeed * delta.toFloat())
            }
            keyboard.isDown(GLFW.GLFW_KEY_E) || keyboard.isDown(GLFW.GLFW_KEY_SPACE) -> {
                camera.translate(y = cameraMoveSpeed * delta.toFloat())
            }
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
            Pair("F2:", "Reset the camera"),
            Pair("MMB:", "Pan the camera"),
            Pair("RMB:", "Rotate the camera"),
            Pair("E/SPACE:", "Move the camera up"),
            Pair("Q/SHIFT:", "Move the camera down"),
            Pair("W/A/S/D:", "Move the camera forward/left/backward/right")
        )

        for ((key, function) in controls) {
            println(spacingFormat.format(key, function))
        }
        println("#${"=".repeat(lineWidth - 2)}#")
    }
}