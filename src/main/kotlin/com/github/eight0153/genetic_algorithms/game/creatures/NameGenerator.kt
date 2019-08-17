package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.engine.Utils

/** Generates names of creature species in a similar style to the Ubuntu release code names. */
object NameGenerator {
    private val adjectives: MutableMap<Char, MutableSet<String>> = HashMap()
    private val nouns: MutableMap<Char, MutableSet<String>> = HashMap()

    private val previousUniqueNames: MutableSet<String> = HashSet()

    init {
        val adjectivesFile = Utils.loadResource("naming/adjectives.txt")
        parseFileContents(adjectivesFile, adjectives)

        val nounsFile = Utils.loadResource("/naming/nouns.txt")
        parseFileContents(nounsFile, nouns)
    }

    /**
     * Read the contents of a file and index by first character.
     *
     * The file may contains lines that start with:
     * - A hash '#' that indicates that the line is a comment.
     * - A '[' which indicates that the line is indicating which section (block of words starting with the same letter)
     * we are in.
     *
     * The results are stored in [destination].
     *
     * This function is idempotent.
     */
    private fun parseFileContents(fileContents: String, destination: MutableMap<Char, MutableSet<String>>) {
        loop@ for (line in fileContents.lines()) {
            when (line.first()) {
                // Skip comments marked by hash
                '#' -> continue@loop
                '[' -> continue@loop
                else -> {
                    var processedLine = line.trim()
                    processedLine = processedLine.split(" ").joinToString(" ") { it.capitalize() }
                    processedLine = processedLine.split("-").joinToString("-") { it.capitalize() }

                    destination.getOrPut(processedLine[0].toLowerCase(), { HashSet() }).add(processedLine)
                }
            }
        }
    }

    /** Generate a random name. */
    fun random(): String {
        val startingLetter = adjectives.keys.random()

        val adjective = adjectives[startingLetter]!!.random()
        val noun = nouns[startingLetter]!!.random()

        return "$adjective $noun"
    }

    /**
     * Generate a unique random name.
     *
     * This method will not generate names that have been generated through this method before.
     */
    fun uniqueRandom(): String {
        var name: String

        do {
            val startingLetter = adjectives.keys.random()

            val adjective = adjectives[startingLetter]!!.random()
            val noun = nouns[startingLetter]!!.random()

            name = "$adjective $noun"
        } while (name in previousUniqueNames)

        previousUniqueNames.add(name)

        return name
    }
}