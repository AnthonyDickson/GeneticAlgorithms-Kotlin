package com.github.eight0153.genetic_algorithms.engine.graphics

import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(
    vertices: FloatArray, textureCoordinates: FloatArray, indices: IntArray,
    private val texture: Texture
) {
    private val vaoId: Int
    private val vboIds: ArrayList<Int>
    private val vertexCount: Int

    init {
        var verticesBuffer: FloatBuffer? = null
        var textureCoordinatesBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null

        try {
            vertexCount = indices.size
            vboIds = ArrayList()

            // Create the vertex array object (VAO)
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Create the vertex buffer object (VBO)
            var vboId = glGenBuffers()
            vboIds.add(vboId)
            // Allocate off-heap memory that the native code can access
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.size)
            verticesBuffer.put(vertices).flip()

            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer!!, GL_STATIC_DRAW)
            // 3 for 3D coordinates
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

            // Texture VBO
            vboId = glGenBuffers()
            vboIds.add(vboId)

            textureCoordinatesBuffer = MemoryUtil.memAllocFloat(textureCoordinates.size)
            textureCoordinatesBuffer.put(textureCoordinates).flip()

            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, textureCoordinatesBuffer!!, GL_STATIC_DRAW)
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0)

            // Indices VBO
            vboId = glGenBuffers()
            vboIds.add(vboId)

            indicesBuffer = MemoryUtil.memAllocInt(indices.size)
            indicesBuffer.put(indices).flip()

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId)
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer!!, GL_STATIC_DRAW)

            // Done setting up VAO and VBO, so unbind
            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            // Have to manually dealloc off-heap memory
            MemoryUtil.memFree(verticesBuffer)
            MemoryUtil.memFree(textureCoordinatesBuffer)
            MemoryUtil.memFree(indicesBuffer)
        }
    }

    fun render() {
        texture.bind()

        // Draw the mesh
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)
        texture.unbind()
    }

    fun cleanup() {
        glDisableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboIds.toIntArray())

        texture.cleanup()

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }
}
