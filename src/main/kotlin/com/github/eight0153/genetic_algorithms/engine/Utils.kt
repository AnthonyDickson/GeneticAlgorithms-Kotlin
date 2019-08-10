package com.github.eight0153.genetic_algorithms.engine

import org.joml.Vector3f
import java.io.File
import java.net.URL
import kotlin.random.Random

class Utils {
    companion object {
        /**
         * Get the contents of a resource.
         * The [resourceID] can also include the path inside the resources directory.
         */
        fun loadResource(resourceID: String): String {
            return getResourceUrl(resourceID)!!.readText()
        }

        fun getResourceUrl(resourceID: String): URL? {
            val resourcePath = if (resourceID.startsWith("/")) resourceID else "/$resourceID"
            return Utils::class.java.getResource(resourcePath)
        }

        fun getResourcePath(resourceID: String): String {
            val file = File(getResourceUrl(resourceID)!!.file)

            return file.absolutePath
        }

        fun randomColour(): Vector3f {
            return Vector3f(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat()
            )
        }
    }
}