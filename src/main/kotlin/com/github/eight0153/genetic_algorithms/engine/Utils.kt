package com.github.eight0153.genetic_algorithms.engine

import java.io.File
import java.net.URL

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
    }
}