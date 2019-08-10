package com.github.eight0153.genetic_algorithms.engine.graphics

import org.joml.Vector4f

data class Material(
    val ambientColour: Vector4f,
    val diffuseColour: Vector4f,
    val specularColour: Vector4f,
    val reflectance: Float = 0.0f
) {
    companion object {
        val defaultColour = Vector4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    var texture: Texture? = null
    val hasTexture: Boolean get() = texture != null

    constructor() : this(defaultColour)

    constructor(colour: Vector4f, reflectance: Float = 0.0f) : this(colour, colour, colour, reflectance)

    constructor(texture: Texture, reflectance: Float = 0.0f) : this(defaultColour, reflectance) {
        this.texture = texture
    }
}