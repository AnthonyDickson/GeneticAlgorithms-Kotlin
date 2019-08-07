package com.github.eight0153.genetic_algorithms.game

import com.github.eight0153.genetic_algorithms.engine.Size
import com.github.eight0153.genetic_algorithms.engine.Utils
import com.github.eight0153.genetic_algorithms.engine.graphics.Mesh
import com.github.eight0153.genetic_algorithms.engine.graphics.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL30.*


/** Renders an object. */
class Renderer(windowSize: Size, private val mesh: Mesh) {
    private val shaderProgram: ShaderProgram
    private val fieldOfView = Math.toRadians(60.0).toFloat()
    private val zNear = 0.01f
    private val zFar = 1000f
    private val viewMatrix: Matrix4f
    private val projectionMatrix: Matrix4f

    init {
        val aspectRatio: Float = windowSize.width.toFloat() / windowSize.height
        projectionMatrix = Matrix4f().perspective(fieldOfView, aspectRatio, zNear, zFar)
        viewMatrix = Matrix4f()

        shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shaderProgram.link()
        // These lines have to happen after linking, otherwise `projectionMatrix` would not yet be defined
        shaderProgram.createUniform("projectionMatrix")
    }

    fun render() {
        shaderProgram.bind()
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)
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