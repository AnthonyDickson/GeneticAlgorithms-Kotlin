package com.github.eight0153.genetic_algorithms.engine.graphics

import com.github.eight0153.genetic_algorithms.engine.Utils
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.*


class Mesh(
    positions: FloatArray,
    textureCoordinates: FloatArray,
    normals: FloatArray,
    indices: IntArray
) {
    private val vaoId: Int
    private val vboIds: ArrayList<Int>
    private val vertexCount: Int

    init {
        var positionBuffer: FloatBuffer? = null
        var textureCoordinatesBuffer: FloatBuffer? = null
        var normalsBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null

        try {
            vertexCount = indices.size
            vboIds = ArrayList()

            // Create the vertex array object (VAO)
            vaoId = glGenVertexArrays()
            glBindVertexArray(vaoId)

            // Positions vertex buffer object (VBO)
            var vboId = glGenBuffers()
            vboIds.add(vboId)
            // Allocate off-heap memory that the native code can access
            positionBuffer = MemoryUtil.memAllocFloat(positions.size)
            positionBuffer.put(positions).flip()

            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, positionBuffer!!, GL_STATIC_DRAW)
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

            // Vertex normals VBO
            vboId = glGenBuffers()
            vboIds.add(vboId)
            normalsBuffer = MemoryUtil.memAllocFloat(normals.size)
            normalsBuffer.put(normals).flip()

            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer!!, GL_STATIC_DRAW)
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0)

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
            MemoryUtil.memFree(positionBuffer)
            MemoryUtil.memFree(textureCoordinatesBuffer)
            MemoryUtil.memFree(normalsBuffer)
            MemoryUtil.memFree(indicesBuffer)
        }
    }

    fun render(material: Material) {
        material.texture?.bind()

        // Draw the mesh
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)

        // Restore state
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)
        glBindVertexArray(0)

        material.texture?.unbind()
    }

    fun cleanup() {
        glDisableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboIds.toIntArray())

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }

    companion object {
        /** Load a .obj model from [resourceID] and create a [Mesh]. */
        @Throws(Exception::class)
        fun load(resourceID: String): Mesh {
            val obj = Utils.loadResource(resourceID)
            val positions = ArrayList<Vector3f>()
            val textures = ArrayList<Vector2f>()
            val normals = ArrayList<Vector3f>()
            val faces = ArrayList<Face>()

            for (line in obj.lines()) {
                val tokens = line.split(" ")
                when (tokens[0]) {
                    // Vertex position
                    "v" -> {
                        positions.add(
                            Vector3f(
                                tokens[1].toFloat(),
                                tokens[2].toFloat(),
                                tokens[3].toFloat()
                            )
                        )
                    }
                    // Texture Coordinate
                    "vt" -> {
                        textures.add(
                            Vector2f(
                                tokens[1].toFloat(),
                                tokens[2].toFloat()
                            )
                        )
                    }
                    // Vertex normal
                    "vn" -> {
                        normals.add(
                            Vector3f(
                                tokens[1].toFloat(),
                                tokens[2].toFloat(),
                                tokens[3].toFloat()
                            )
                        )
                    }
                    // Face
                    "f" -> faces.add(Face(tokens[1], tokens[2], tokens[3]))
                }
            }

            return reorderLists(positions, textures, normals, faces)
        }

        private fun reorderLists(
            vertices: List<Vector3f>, textureCoordinates: List<Vector2f>,
            normals: List<Vector3f>, faces: List<Face>
        ): Mesh {
            val outIndices = ArrayList<Int>()
            // Create position array in the order it has been declared
            val outPositions = FloatArray(vertices.size * 3)
            val outTextureCoordinates = FloatArray(vertices.size * 2)
            val outNormals = FloatArray(vertices.size * 3)

            for ((i, vertex) in vertices.withIndex()) {
                outPositions[i * 3] = vertex.x
                outPositions[i * 3 + 1] = vertex.y
                outPositions[i * 3 + 2] = vertex.z
            }

            for (face in faces) {
                val faceVertexIndices = face.faceVertexIndices
                for (indexGroup in faceVertexIndices) {
                    processFaceVertex(
                        indexGroup, textureCoordinates, normals,
                        outIndices, outTextureCoordinates, outNormals
                    )
                }
            }

            return Mesh(outPositions, outTextureCoordinates, outNormals, outIndices.toIntArray())
        }

        private fun processFaceVertex(
            indexGroups: IndexGroup,
            textureCoordinates: List<Vector2f>,
            normals: List<Vector3f>,

            outIndices: MutableList<Int>,
            outTextureCoordinates: FloatArray,
            outNormals: FloatArray
        ) {

            // Set index for vertex coordinates
            val positionIndex = indexGroups.positionIndex
            outIndices.add(positionIndex)

            // Reorder texture coordinates
            if (indexGroups.textureCoordinatesIndex >= 0) {
                val textureCoordinate = textureCoordinates[indexGroups.textureCoordinatesIndex]
                outTextureCoordinates[positionIndex * 2] = textureCoordinate.x
                outTextureCoordinates[positionIndex * 2 + 1] = 1 - textureCoordinate.y
            }

            if (indexGroups.normalIndex >= 0) {
                // Reorder normals
                val normal = normals[indexGroups.normalIndex]
                outNormals[positionIndex * 3] = normal.x
                outNormals[positionIndex * 3 + 1] = normal.y
                outNormals[positionIndex * 3 + 2] = normal.z
            }
        }

        private class IndexGroup {
            var positionIndex: Int = 0
            var textureCoordinatesIndex: Int = 0
            var normalIndex: Int = 0

            init {
                positionIndex = NO_VALUE
                textureCoordinatesIndex = NO_VALUE
                normalIndex = NO_VALUE
            }

            companion object {
                const val NO_VALUE = -1
            }
        }

        private class Face(
            vertexGroup1: String,
            vertexGroup2: String,
            vertexGroup3: String
        ) {

            /**
             * List of indexGroup groups for a face triangle (3 vertices per face).
             */
            var faceVertexIndices: Array<IndexGroup>

            init {
                faceVertexIndices = arrayOf(
                    parseLine(vertexGroup1),
                    parseLine(vertexGroup2),
                    parseLine(vertexGroup3)
                )
            }

            private fun parseLine(line: String): IndexGroup {
                val indexGroup = IndexGroup()

                val lineTokens = line.split("/")
                val length = lineTokens.size
                indexGroup.positionIndex = lineTokens[0].toInt() - 1

                if (length > 1) {
                    indexGroup.textureCoordinatesIndex = when {
                        // It can be empty if the obj does not define texture coordinates
                        lineTokens[1].isNotEmpty() -> lineTokens[1].toInt() - 1
                        else -> IndexGroup.NO_VALUE
                    }

                    if (length > 2) {
                        indexGroup.normalIndex = lineTokens[2].toInt() - 1
                    }
                }

                return indexGroup
            }
        }
    }
}
