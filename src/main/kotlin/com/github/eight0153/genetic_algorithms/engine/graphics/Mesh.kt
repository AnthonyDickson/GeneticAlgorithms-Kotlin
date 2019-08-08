package com.github.eight0153.genetic_algorithms.engine.graphics

import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(positions: Array<Float>, colours: Array<Float>, indices: Array<Int>, private val mode: Int = GL_TRIANGLES) {
    val vaoId: Int
    private val vboId: Int
    private val colourVboId: Int
    private val indexVboId: Int
    val vertexCount: Int

    init {
        var verticesBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null
        var colourBuffer: FloatBuffer? = null

        try {
            vertexCount = indices.size

            // Create the vertex array object (VAO)
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Create the vertex buffer
            // Allocate off-heap memory that the native code can access
            verticesBuffer = MemoryUtil.memAllocFloat(positions.size)
            verticesBuffer.put(positions.toFloatArray()).flip()
            // Create the vertex buffer object (VBO)
            vboId = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer!!, GL_STATIC_DRAW)
            // 3 for 3D coordinates
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

            // Create the colour buffer
            colourBuffer = MemoryUtil.memAllocFloat(colours.size)
            colourBuffer.put(colours.toFloatArray()).flip()

            colourVboId = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, colourVboId)
            glBufferData(GL_ARRAY_BUFFER, colourBuffer!!, GL_STATIC_DRAW)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)

            // Create the index buffer
            indicesBuffer = MemoryUtil.memAllocInt(indices.size)
            indicesBuffer.put(indices.toIntArray()).flip()
            // Create the indices' VBO
            indexVboId = glGenBuffers()
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer!!, GL_STATIC_DRAW)

            // Done setting up VAO and VBO, so unbind
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            // Have to manually dealloc off-heap memory
            MemoryUtil.memFree(verticesBuffer)
            MemoryUtil.memFree(colourBuffer)
            MemoryUtil.memFree(indicesBuffer)
        }
    }

    fun render() {
        // Draw the mesh
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glDrawElements(mode, vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)
    }

    fun cleanup() {
        glDisableVertexAttribArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboId)
        glDeleteBuffers(colourVboId)
        glDeleteBuffers(indexVboId)

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }
}