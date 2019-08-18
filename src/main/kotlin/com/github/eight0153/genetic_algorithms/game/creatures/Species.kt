package com.github.eight0153.genetic_algorithms.game.creatures

/** A collection of creatures with similar genes. */
class Species(
    val name: String,
    var representativeCreature: Creature
) {
    companion object {
        const val SIMILARITY_THRESHOLD = 0.0

        private var speciesCounter = 0
        val nextId: Int get() = ++speciesCounter
    }

    val id: Int = nextId
    var members: MutableSet<Creature> = HashSet()
    private var numPastMembers = 0

    /** Number of current(alive) members in the species. */
    val numMembers: Int get() = members.size
    /** Total number of current (alive) and past (dead) members. */
    val totalMembers: Int get() = numMembers + numPastMembers

    val isExtinct: Boolean get() = numMembers == 0

    init {
        members.add(representativeCreature)
    }

    /**
     * Add [creature] to this [Species].
     *
     * @return true if [creature] was added to the species, false if the creature was too different to be added.
     */
    fun add(creature: Creature): Boolean {
        return if (creature.similarity(representativeCreature) > SIMILARITY_THRESHOLD) {
            members.add(creature)
            creature.species = this

            true
        } else {
            false
        }
    }

    /**
     * Remove [creature] from [Species].
     *
     * @return true if [creature] was in this [Species], false otherwise.
     */
    fun remove(creature: Creature): Boolean {
        return if (members.remove(creature)) {
            numPastMembers++

            true
        } else {
            false
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Species) name == other.name else super.equals(other)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}