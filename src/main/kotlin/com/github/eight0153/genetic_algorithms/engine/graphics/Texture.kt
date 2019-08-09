package com.github.eight0153.genetic_algorithms.engine.graphics

import com.github.eight0153.genetic_algorithms.engine.Utils
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack


class Texture(resourceID: String) {
    private val id: Int

    init {
        id = loadTexture(Utils.getResourcePath(resourceID))
    }

    fun bind() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun cleanup() {
        glDeleteTextures(id)
    }

    companion object {
        @Throws(Exception::class)
        private fun loadTexture(fileName: String): Int {
            // Load Texture file
            val stack = MemoryStack.stackPush()
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val channels = stack.mallocInt(1)

            val buffer = stbi_load(fileName, w, h, channels, 4)
                ?: throw Exception("Image file [" + fileName + "] not loaded: " + stbi_failure_reason())

            /* Get width and height of image */
            val width: Int = w.get()
            val height: Int = h.get()

            // Create a new OpenGL texture
            val textureId = glGenTextures()
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, textureId)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGBA, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer
            )
            glGenerateMipmap(GL_TEXTURE_2D)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

            stbi_image_free(buffer)

            return textureId
        }
    }
}