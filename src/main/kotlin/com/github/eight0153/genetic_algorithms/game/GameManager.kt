package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.GameManagerI
import com.github.eight0153.genetic_algorithms.engine.Size
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL33
import kotlin.random.Random

class GameManager : GameManagerI {
    private val frameRateLogger = FrameRateLogger()

    private lateinit var renderer: Renderer
    private lateinit var quadMesh: Mesh

    private lateinit var camera: Camera
    private val cameraMoveSpeed = 0.1f

    override fun init(windowSize: Size, windowName: String) {
        camera = Camera(windowSize)
        // A quad/rectangle
        quadMesh = Mesh(
            arrayOf(
                -0.5f, -0.5f, -1.05f,
                -0.5f, 0.5f, -1.05f,
                0.5f, -0.5f, -1.05f,
                0.5f, 0.5f, -1.05f
            ),
            arrayOf(
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f
            ),
            arrayOf(0, 1, 2, 1, 2, 3)
        )
//        cubeMesh = Mesh(
//            arrayOf(
//                -0.5f, -0.5f, -0.5f,
//                -0.5f, 0.5f, -0.5f,
//                0.5f, -0.5f, -0.5f,
//                0.5f, 0.5f, -0.5f
//                -0.5f, -0.5f, -1.5f,
//                -0.5f, 0.5f, -1.5f,
//                0.5f, -0.5f, -1.5f,
//                0.5f, 0.5f, -1.5f
//            ),
//            arrayOf(
//                0.5f, 0.0f, 0.0f,
//                0.0f, 0.5f, 0.0f,
//                0.0f, 0.0f, 0.5f,
//                0.0f, 0.5f, 0.5f,
//                0.5f, 0.0f, 0.0f,
//                0.0f, 0.5f, 0.0f,
//                0.0f, 0.0f, 0.5f,
//                0.0f, 0.5f, 0.5f
//            ),
//            arrayOf(0, 1, 2, 1, 2, 3, // Front face
//                    0, 1, 4, 1, 4, 5, // Left face
//                    2, 3, 6, 3 , 6, 7, // Right face
//                    1, 3, 5, 3, 5, 7, // Top face
//                    0, 2, 4, 2, 4, 6, // Bottom face
//                    4, 5, 6, 5, 6, 7  // Back face
//                )
//        )
        renderer = Renderer(camera, quadMesh)
        printInfo(windowName)
    }

    override fun handleInput(keyboard: KeyboardInputHandler, mouse: MouseInputHandler): Boolean {
        if (keyboard.wasReleased(GLFW.GLFW_KEY_ESCAPE)) {
            return false
        } else if (keyboard.wasPressed(GLFW.GLFW_KEY_F1)) {
            frameRateLogger.toggle()
        }

        // EPILEPSY WARNING: Flashing Colours
        if (mouse.isHeldDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            GL33.glClearColor(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1.0f)
        }

        if (keyboard.isDown(GLFW.GLFW_KEY_W)) {
            camera.position.y += cameraMoveSpeed
        } else if (keyboard.isDown(GLFW.GLFW_KEY_S)) {
            camera.position.y -= cameraMoveSpeed
        }

        if (keyboard.isDown(GLFW.GLFW_KEY_A)) {
            camera.position.x -= cameraMoveSpeed
        } else if (keyboard.isDown(GLFW.GLFW_KEY_D)) {
            camera.position.x += cameraMoveSpeed
        }

        return true
    }

    override fun update(delta: Double) {
        frameRateLogger.update(delta)
    }

    override fun render() {
        renderer.render()
    }

    override fun cleanup() {
        renderer.cleanup()
        quadMesh.cleanup()
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
            "This just renders a square at this point.",
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

        println("F1: \tToggle frame rate logging")
        println(
            "LMD: \tChange background colour rapidly while LMD is held down. \n" +
                    "\t\tWARNING: This feature may potentially trigger seizures for people with \n" +
                    "\t\tphotosensitive epilepsy. \n" +
                    "\t\tUser discretion is advised."
        )
        println("W: Move up")
        println("S: Move down")
        println("A: Move left")
        println("D: Move right")
        println("#${"=".repeat(lineWidth - 2)}#")
    }
}