package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.*
import com.github.eight0153.genetic_algorithms.engine.graphics.DirectionalLight
import com.github.eight0153.genetic_algorithms.engine.graphics.PointLight
import com.github.eight0153.genetic_algorithms.engine.graphics.SpotLight
import com.github.eight0153.genetic_algorithms.engine.input.KeyboardInputHandler
import com.github.eight0153.genetic_algorithms.engine.input.MouseInputHandler
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class GameManager(private val worldSize: Vector3f) : GameManagerI {
    private val frameRateLogger = FrameRateLogger()

    private var gameObjects = ArrayList<GameObject>()
    private var gameLogicManagers = ArrayList<GameLogicManagerI>()

    private lateinit var camera: Camera
    private lateinit var renderer: Renderer
    private lateinit var worldBounds: Bounds3D

    private val cameraMoveSpeed = 20f
    private val cameraRotateSpeed = 20f

    override fun init(
        windowSize: Vector2f,
        windowName: String
    ) {
        val minBounds = Vector3f(
            -0.5f * worldSize.x,
            0.0f,
            -0.5f * worldSize.z
        )

        // Minus one from max bounds to prevent off-by-one error in bounds clipping
        val maxBounds = Vector3f(
            0.5f * worldSize.x - 1,
            worldSize.y - 1,
            0.5f * worldSize.z - 1
        )

        worldBounds = Bounds3D(minBounds, maxBounds)

        camera = Camera(windowSize, worldBounds)
        resetCamera()

        gameObjects = ArrayList()

        for (row in 0 until worldSize.x.toInt()) {
            for (col in 0 until worldSize.z.toInt()) {
                val block = GrassBlockFactory.create()
                block.transform.translate(
                    minBounds.x + row.toFloat(),
                    -1.0f,
                    minBounds.z + col.toFloat()
                )
                gameObjects.add(block)
            }
        }

        gameObjects.add(GrassBlockFactory.create())

        gameLogicManagers.add(CreatureManager(worldBounds))


        val ambientLight = Vector3f(0.5f, 0.5f, 0.6f)
        val directionalLight = DirectionalLight(
            colour = Vector3f(1.0f, 1.0f, 0.9f),
            direction = Vector3f(1.0f, 1.0f, 1.0f),
            intensity = 0.8f
        )

        gameLogicManagers.add(
            DayNightCycleManager(
                60.0f,
                ambientLight,
                directionalLight
            )
        )

        renderer = Renderer(
            camera,
            ambientLight = ambientLight,
            pointLight = PointLight(
                colour = Vector3f(1.0f, 1.0f, 0.8f),
                position = Vector3f(0.0f, 1.0f, -2.0f),
                intensity = 0.8f
            ),
            directionalLight = directionalLight,
            spotLight = SpotLight(
                PointLight(
                    colour = Vector3f(1.0f, 0.5f, 0.5f),
                    position = Vector3f(0.0f, 0.5f, 1.0f)
                ),
                direction = Vector3f(0.0f, 0.0f, -1.0f)
            )
        )

        var testBlock = GrassBlockFactory.create()
        testBlock.transform.translate(y = 1.0f)
        gameObjects.add(testBlock)

        testBlock = GrassBlockFactory.create()
        testBlock.transform.translate(x = 1.0f)
        gameObjects.add(testBlock)

        testBlock = GrassBlockFactory.create()
        testBlock.transform.translate(x = 1.0f, y = 1.0f)
        gameObjects.add(testBlock)

        testBlock = GrassBlockFactory.create()
        testBlock.transform.translate(x = -1.0f)
        gameObjects.add(testBlock)

        testBlock = GrassBlockFactory.create()
        testBlock.transform.translate(x = -1.0f, y = 1.0f)
        gameObjects.add(testBlock)

        testBlock = GrassBlockFactory.create()
        testBlock.transform.translate(z = 2.0f)
        gameObjects.add(testBlock)

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


        for (gameLogicManager in gameLogicManagers) {
            val shouldContinue = gameLogicManager.handleInput(delta, keyboard, mouse)

            if (!shouldContinue) {
                return false
            }
        }

        return true
    }

    override fun update(delta: Double) {
        gameObjects.forEach { it.update(delta) }
        gameLogicManagers.forEach { it.update(delta) }
        frameRateLogger.update(delta)
    }

    override fun render() {
        renderer.render(gameObjects)
        gameLogicManagers.forEach { it.render(renderer) }
    }

    override fun cleanup() {
        gameObjects.forEach { it.cleanup() }
        gameLogicManagers.forEach { it.cleanup() }
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
            "There are things that move around on the screen.",
            "Sometimes they die, sometimes they reproduce.",
            "You can fly the camera around and pretend you're superman.",
            "Although, it is still kinda dark."
        )

        for (message in infoMessages) {
            println(" ".repeat((lineWidth - message.length) / 2) + message)
        }
        println()

        // Print the controls
        val header = "Controls"
        println(header)
        println("-".repeat(header.length))

        val spacingFormat = "%-8s - %s"

        val controls = mutableMapOf(
            Pair("F1", "Toggle frame rate logging"),
            Pair("F2", "Reset the camera"),
            Pair("MMB", "Pan the camera"),
            Pair("RMB", "Rotate the camera"),
            Pair("E/SPACE", "Move the camera up"),
            Pair("Q/SHIFT", "Move the camera down"),
            Pair("W/A/S/D", "Move the camera forward/left/backward/right")
        )

        gameLogicManagers.forEach { controls.putAll(it.controls) }

        for ((key, function) in controls) {
            println(spacingFormat.format(key, function))
        }
        println("#${"=".repeat(lineWidth - 2)}#")
    }
}