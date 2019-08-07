package com.github.eight0153.genetic_algorithms

import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class Mesh(positions: Array<Float>) {
    val vaoId: Int
    val vboId: Int
    val vertexCount: Int

    init {
        var verticesBuffer: FloatBuffer? = null

        try {
            // Allocate off-heap memory that the native code can access
            verticesBuffer = MemoryUtil.memAllocFloat(positions.size)
            vertexCount = positions.size / 3
            verticesBuffer.put(positions.toFloatArray()).flip()

            // Create the vertex array object (VAO)
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Create the vertex buffer object (VBO)
            vboId = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer!!, GL_STATIC_DRAW)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

            // Done setting up VAO and VBO, so unbind
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            if (verticesBuffer != null) {
                // Have to manually dealloc off-heap memory
                MemoryUtil.memFree(verticesBuffer)
            }
        }
    }

    fun cleanup() {
        glDisableVertexAttribArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboId)

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }
}