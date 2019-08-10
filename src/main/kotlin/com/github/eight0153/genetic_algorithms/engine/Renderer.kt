package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.graphics.PointLight
import com.github.eight0153.genetic_algorithms.engine.graphics.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f


/** Renders an object. */
class Renderer(
    private val camera: Camera,
    private val ambientLight: Vector3f,
    private val pointLight: PointLight,
    private val specularPower: Float = 10.0f
) {
    private val shaderProgram: ShaderProgram = ShaderProgram()

    init {
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shaderProgram.link()
        // These lines have to happen after linking, otherwise the uniform variables would not yet be defined
        shaderProgram.createUniform("viewModel")
        shaderProgram.createUniform("projection")
        shaderProgram.createUniform("textureSampler")

        shaderProgram.createMaterialUniform("material")

        shaderProgram.createUniform("ambientLight")
        shaderProgram.createUniform("specularPower")
        shaderProgram.createPointLightUniform("pointLight")
    }

    fun render(gameObjects: List<GameObject>) {
        shaderProgram.bind()
        shaderProgram.setUniform("projection", camera.projectionMatrix)
        shaderProgram.setUniform("textureSampler", 0)
        shaderProgram.setUniform("ambientLight", ambientLight)
        shaderProgram.setUniform("specularPower", specularPower)

        val lightPointPosition = Vector4f(pointLight.position, 1.0f).mul(camera.viewMatrix)
        pointLight.viewPosition.set(
            lightPointPosition.x,
            lightPointPosition.y,
            lightPointPosition.z
        )
        shaderProgram.setUniform("pointLight", pointLight)

        val viewMatrix = camera.viewMatrix
        val viewModel = Matrix4f()

        for (gameObject in gameObjects.filter { it.shouldRender }) {
            viewMatrix.mul(gameObject.modelMatrix, viewModel)
            shaderProgram.setUniform("viewModel", viewModel)
            shaderProgram.setUniform("material", gameObject.material)

            gameObject.render()
        }

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}