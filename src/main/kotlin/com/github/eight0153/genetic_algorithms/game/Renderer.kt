package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.Utils
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.graphics.ShaderProgram
import org.lwjgl.opengl.GL30.*


/** Renders an object. */
class Renderer(
    private val camera: Camera,
    private val mesh: Mesh
) {
    private val shaderProgram: ShaderProgram = ShaderProgram()

    init {
        shaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shaderProgram.link()
        // These lines have to happen after linking, otherwise `projectionMatrix` would not yet be defined
        shaderProgram.createUniform("viewMatrix")
        shaderProgram.createUniform("projectionMatrix")
    }

    fun render() {
        shaderProgram.bind()
        shaderProgram.setUniform("viewMatrix", camera.viewMatrix)
        shaderProgram.setUniform("projectionMatrix", camera.projectionMatrix)
        // Draw the mesh
        glBindVertexArray(mesh.vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glDisableVertexAttribArray(0)
        glBindVertexArray(0)

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}