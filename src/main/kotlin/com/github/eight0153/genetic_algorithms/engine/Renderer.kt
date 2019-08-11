package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.graphics.DirectionalLight
import com.github.eight0153.genetic_algorithms.engine.graphics.PointLight
import com.github.eight0153.genetic_algorithms.engine.graphics.ShaderProgram
import com.github.eight0153.genetic_algorithms.engine.graphics.SpotLight
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f


/** Renders an object. */
class Renderer(
    private val camera: Camera,
    private val ambientLight: Vector3f,
    private val directionalLight: DirectionalLight,
    private val pointLights: Array<PointLight>,
    private val spotLights: Array<SpotLight>,
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

        shaderProgram.createUniform("specularPower")
        shaderProgram.createUniform("ambientLight")
        shaderProgram.createDirectionalLightUniform("directionalLight")
        shaderProgram.createPointLightUniforms("pointLights", 5)
        shaderProgram.createSpotLightUniforms("spotLights", 5)
    }

    fun render(gameObjects: List<GameObject>) {
        val viewMatrix = camera.viewMatrix

        shaderProgram.bind()
        shaderProgram.setUniform("projection", camera.projectionMatrix)
        shaderProgram.setUniform("textureSampler", 0)
        shaderProgram.setUniform("specularPower", specularPower)
        shaderProgram.setUniform("ambientLight", ambientLight)

        val directionalLightDirection = Vector4f(directionalLight.direction, 0.0f).mul(viewMatrix)
        directionalLight.viewDirection.set(
            directionalLightDirection.x,
            directionalLightDirection.y,
            directionalLightDirection.z
        )
        shaderProgram.setUniform("directionalLight", directionalLight)

        for (pointLight in pointLights) {
            val pointLightPosition = Vector4f(pointLight.position, 1.0f).mul(viewMatrix)
            pointLight.viewPosition.set(
                pointLightPosition.x,
                pointLightPosition.y,
                pointLightPosition.z
            )
        }
        shaderProgram.setUniform("pointLights", pointLights)


        for (spotLight in spotLights) {
            val spotLightPosition = Vector4f(spotLight.pointLight.position, 1.0f).mul(viewMatrix)
            spotLight.pointLight.viewPosition.set(
                spotLightPosition.x,
                spotLightPosition.y,
                spotLightPosition.z
            )

            val spotLightDirection = Vector4f(spotLight.direction, 0.0f).mul(viewMatrix)
            spotLight.viewDirection.set(
                spotLightDirection.x,
                spotLightDirection.y,
                spotLightDirection.z
            )
        }

        shaderProgram.setUniform("spotLights", spotLights)

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