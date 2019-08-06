package com.github.eight0153.genetic_algorithms

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil


/** Renders an object. */
class Renderer {
    private val shaderProgram: ShaderProgram
    private val vaoId: Int
    private val vboId: Int

    init {
        val vertices: FloatArray = floatArrayOf(0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f) // A triangle

        // Must allocate vertex buffer in off-heap memory so native code (OpenGL) can access it.
        // This is not garbage collected!
        val verticesBuffer = MemoryUtil.memAllocFloat(vertices.size)
        verticesBuffer.put(vertices).flip()

        // Create the VAO (Vertex Array Object)
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Create the VBO (Vertex Buffer Object)
        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        // Unbind the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        // Unbind the VAO
        glBindVertexArray(0)

        // Vertices are loaded into the GPU memory so we no longer need it and can get rid of the buffer.
        MemoryUtil.memFree(verticesBuffer)

        shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(Utils.loadResource("shaders/vertex.vs"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"))
        shaderProgram.link()
    }

    fun render() {
        shaderProgram.bind()

        // Bind to the VAO
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)

        // Draw the vertices
        glDrawArrays(GL_TRIANGLES, 0, 3)

        // Restore state
        glDisableVertexAttribArray(0)
        glBindVertexArray(0)

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()

        glDisableVertexAttribArray(0)

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboId)

        // Delete the VAO
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }
}