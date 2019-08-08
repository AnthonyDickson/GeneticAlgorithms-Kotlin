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
        shaderProgram.createUniform("modelMatrix")
        shaderProgram.createUniform("viewMatrix")
        shaderProgram.createUniform("projectionMatrix")
    }

    fun render(gameObjects: Array<GameObject>) {
        shaderProgram.bind()
        shaderProgram.setUniform("viewMatrix", camera.viewMatrix)
        shaderProgram.setUniform("projectionMatrix", camera.projectionMatrix)

        for (gameObject in gameObjects.filter { it.shouldRender }) {
            shaderProgram.setUniform("modelMatrix", gameObject.modelMatrix)
            gameObject.render()
        }

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}