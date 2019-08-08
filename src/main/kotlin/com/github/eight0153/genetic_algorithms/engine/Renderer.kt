package com.github.eight0153.genetic_algorithms.engine

import com.github.eight0153.genetic_algorithms.engine.graphics.ShaderProgram


/** Renders an object. */
class Renderer(
    private val camera: Camera
) {
    private val shaderProgram: ShaderProgram =
        ShaderProgram()

    init {
        shaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shaderProgram.link()
        // These lines have to happen after linking, otherwise `projectionMatrix` would not yet be defined
        shaderProgram.createUniform("viewModel")
        shaderProgram.createUniform("projection")
    }

    fun render(gameObjects: Array<GameObject>) {
        shaderProgram.bind()
        shaderProgram.setUniform("projection", camera.projectionMatrix)
        val viewMatrix = camera.viewMatrix

        for (gameObject in gameObjects.filter { it.shouldRender }) {
            val viewModel = viewMatrix.mul(gameObject.modelMatrix)
            shaderProgram.setUniform("viewModel", viewModel)
            gameObject.render()
        }

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}