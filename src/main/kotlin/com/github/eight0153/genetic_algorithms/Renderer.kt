package com.github.eight0153.genetic_algorithms

import org.lwjgl.opengl.GL30.*


/** Renders an object. */
class Renderer(private val mesh: Mesh) {
    private val shaderProgram: ShaderProgram

    init {
        shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shaderProgram.link()
    }

    fun render() {
        shaderProgram.bind()

        // Draw the mesh
        glBindVertexArray(mesh.vaoId)
        glEnableVertexAttribArray(0)

        // Draw the vertices
        glDrawArrays(GL_TRIANGLES, 0, mesh.vertexCount)

        // Restore state
        glDisableVertexAttribArray(0)
        glBindVertexArray(0)

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}