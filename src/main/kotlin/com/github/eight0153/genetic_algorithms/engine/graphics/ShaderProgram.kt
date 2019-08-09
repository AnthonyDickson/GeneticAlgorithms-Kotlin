package com.github.eight0153.genetic_algorithms.engine.graphics


import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack

/** Encapsulates a shader program and its shaders. */
class ShaderProgram @Throws(Exception::class)
constructor() {
    private val programId: Int = glCreateProgram()
    private var vertexShaderId: Int = 0
    private var fragmentShaderId: Int = 0

    private val uniforms: MutableMap<String, Int>

    init {
        if (programId == 0) {
            throw Exception("Could not create Shader")
        }

        uniforms = HashMap()
    }

    /** Create a global GLSL variable with the name [uniformName]. */
    @Throws(Exception::class)
    fun createUniform(uniformName: String) {
        val uniformLocation = glGetUniformLocation(programId, uniformName)

        if (uniformLocation < 0) {
            throw Exception("Could not find uniform: $uniformName")
        }

        uniforms[uniformName] = uniformLocation
    }

    fun setUniform(uniformName: String, value: Int) {
        glUniform1i(uniforms[uniformName]!!, value)
    }

    fun setUniform(uniformName: String, value: Vector3f) {
        MemoryStack.stackPush().use {
            val fb = it.mallocFloat(3)
            value.get(fb)
            glUniform3fv(uniforms[uniformName]!!, fb)
        }
    }
    fun setUniform(uniformName: String, value: Matrix4f) {
        MemoryStack.stackPush().use {
            val fb = it.mallocFloat(16)
            value.get(fb)
            glUniformMatrix4fv(uniforms[uniformName]!!, false, fb)
        }
    }

    fun createVertexShader(shaderCode: String) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER)
    }

    fun createFragmentShader(shaderCode: String) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER)
    }

    /** Create a shader from [shaderCode] of type [shaderType] and return the created shader's ID. */
    @Throws(Exception::class)
    private fun createShader(shaderCode: String, shaderType: Int): Int {
        val shaderId = glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: $shaderType")
        }

        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024))
        }

        glAttachShader(programId, shaderId)

        return shaderId
    }

    /** Link the shaders. */
    @Throws(Exception::class)
    fun link() {
        glLinkProgram(programId)

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024))
        }

        if (vertexShaderId != 0) {
            // We can get rid of the shader once the program has been linked.
            glDetachShader(programId, vertexShaderId)
        }

        if (fragmentShaderId != 0) {
            // We can get rid of the shader once the program has been linked.
            glDetachShader(programId, fragmentShaderId)
        }

        glValidateProgram(programId)

        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024))
        }
    }

    /** Tell OpenGL to use this program's shaders. */
    fun bind() {
        glUseProgram(programId)
    }

    /** Tell OpenGL to stop using this program's shaders. */
    fun unbind() {
        glUseProgram(0)
    }

    fun cleanup() {
        unbind()

        if (programId != 0) {
            glDeleteProgram(programId)
        }
    }
}