package com.github.eight0153.genetic_algorithms

class Utils {
    companion object {
        /**
         * Get the contents of a resource.
         * The [resourceID] can also include the path inside the resources directory.
         */
        fun loadResource(resourceID: String): String {
            val resourcePath = if (resourceID.startsWith("/")) resourceID else "/$resourceID"
            val url = Utils::class.java.getResource(resourcePath)

            return url.readText()
        }
    }
}