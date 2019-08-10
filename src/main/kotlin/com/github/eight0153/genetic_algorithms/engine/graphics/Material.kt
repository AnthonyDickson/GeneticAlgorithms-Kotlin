package com.github.eight0153.genetic_algorithms.engine.graphics

import org.joml.Vector3f

data class Material(
    val ambientColour: Vector3f,
    val diffuseColour: Vector3f,
    val specularColour: Vector3f,
    val reflectance: Float = 0.0f
) {
    companion object {
        val defaultColour = Vector3f(1.0f, 1.0f, 1.0f)
    }

    var texture: Texture? = null
    val hasTexture: Boolean get() = texture != null

    constructor() : this(defaultColour)

    constructor(colour: Vector3f, reflectance: Float = 0.0f) : this(colour, colour, colour, reflectance)

    constructor(texture: Texture, reflectance: Float = 0.0f) : this(defaultColour, reflectance) {
        this.texture = texture
    }
}