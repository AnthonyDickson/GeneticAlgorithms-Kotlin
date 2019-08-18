package com.github.eight0153.genetic_algorithms.game.creatures

import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_BLUE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_GREEN
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.COLOUR_RED
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.DEATH_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.GREEDINESS
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.METABOLIC_EFFICIENCY
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.REPLICATION_CHANCE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SENSORY_RANGE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SHININESS
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SIZE
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.SPEED
import com.github.eight0153.genetic_algorithms.game.creatures.Chromosome.Companion.THRIFTINESS
import kotlinx.coroutines.*
import java.sql.Connection
import java.sql.DriverManager
import kotlin.math.max
import kotlin.system.measureTimeMillis

// TODO: Docker-ise the MySQL server
class CensusDataStore(
    /** How often to update the backing store (i.e. perform database operations) in milliseconds. */
    private val updateInterval: Long = 1000L
) {

    private val DB_URL = System.getenv("DB_URL") ?: "jdbc:mysql://localhost/"

    //  Database credentials
    private val DB_USER_NAME = System.getenv("DB_USERNAME") ?: "root"
    private val DB_PASSWORD = System.getenv("DB_PASSWORD") ?: "password"

    private var connection: Connection? = null
    private var shouldQuit: Boolean = false

    private var batchProcessingCoroutine: Job? = null
    private val speciesBuffer = ArrayList<Species>()
    private val creaturesBuffer = ArrayList<Creature>()
    private val censusesBuffer = ArrayList<Census>()

    fun init() {
        connection = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD)
        val statement = connection?.createStatement()

        var sql = "DROP DATABASE IF EXISTS genetic_algorithms"
        statement?.execute(sql)

        //language=MySQL
        sql = "CREATE DATABASE genetic_algorithms"
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`censuses`(
              `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
              PRIMARY KEY (`id`))
            """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`chromosomes` (
              `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
              `replication_chance` DOUBLE NOT NULL,
              `death_chance` DOUBLE NOT NULL,
              `speed` DOUBLE NOT NULL,
              `size` DOUBLE NOT NULL,
              `colour_red` DOUBLE NOT NULL,
              `colour_green` DOUBLE NOT NULL,
              `colour_blue` DOUBLE NOT NULL,
              `metabolic_efficiency` DOUBLE NOT NULL,
              `sensory_range` DOUBLE NOT NULL,
              `greediness` DOUBLE NOT NULL,
              `thriftiness` DOUBLE NOT NULL,
              `shininess` DOUBLE NOT NULL,
              PRIMARY KEY (`id`),
              UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`species` (
              `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
              `name` VARCHAR(45) NOT NULL,
              PRIMARY KEY (`id`),
              UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
              UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`creatures` (
              `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
              `age` INT(10) UNSIGNED NOT NULL,
              `chromosome_id` INT(10) UNSIGNED NOT NULL,
              `species_id` INT(10) UNSIGNED NOT NULL,
              UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
              INDEX `species_id_idx` (`species_id` ASC) VISIBLE,
              INDEX `chromosome_id_idx` (`chromosome_id` ASC) VISIBLE,
              CONSTRAINT `chromosome_fk`
                FOREIGN KEY (`chromosome_id`)
                REFERENCES `genetic_algorithms`.`chromosomes` (`id`)
                ON DELETE CASCADE,
              CONSTRAINT `species_fk`
                FOREIGN KEY (`species_id`)
                REFERENCES `genetic_algorithms`.`species` (`id`)
                ON DELETE CASCADE)
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`census_participants` (
              `census_id` INT(10) UNSIGNED NOT NULL,
              `participant_id` INT(10) UNSIGNED NOT NULL,
              INDEX `census_fk_idx` (`census_id` ASC) VISIBLE,
              INDEX `individual_id_idx` (`participant_id` ASC) VISIBLE,
              PRIMARY KEY (`participant_id`, `census_id`),
              CONSTRAINT `census_fk`
                FOREIGN KEY (`census_id`)
                REFERENCES `genetic_algorithms`.`censuses` (`id`)
                ON DELETE CASCADE,
              CONSTRAINT `participant_fk`
                FOREIGN KEY (`participant_id`)
                REFERENCES `genetic_algorithms`.`creatures` (`id`)
                ON DELETE CASCADE)
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            USE genetic_algorithms
        """.trimIndent()
        statement?.execute(sql)

        statement?.closeOnCompletion()

        batchProcessingCoroutine = GlobalScope.launch { processBatches() }
    }

    fun add(species: Species) {
        speciesBuffer.add(species)
    }

    fun add(creature: Creature) {
        creaturesBuffer.add(creature)
    }

    fun add(census: Census) {
        censusesBuffer.add(census)
    }

    private suspend fun processBatches() {
        var batchTime: Long
        while (!shouldQuit) {
            batchTime = measureTimeMillis {
                // Create copies of buffers to avoid concurrency issues such as items being added while batch queries
                // are being built (this could cause creatures to be added whose species was not yet added to the
                // database, causing foreign key constrain issues).
                val speciesBatch = java.util.ArrayList<Species>(speciesBuffer.size)
                speciesBatch.addAll(speciesBuffer)
                speciesBuffer.clear()

                val creaturesBatch = java.util.ArrayList<Creature>(creaturesBuffer.size)
                creaturesBatch.addAll(creaturesBuffer)
                creaturesBuffer.clear()

                val censusesBatch = java.util.ArrayList<Census>(censusesBuffer.size)
                censusesBatch.addAll(censusesBuffer)
                censusesBuffer.clear()

                insertSpecies(speciesBatch)
                insertCreatures(creaturesBatch)
                insertCensuses(censusesBatch)
            }

            delay(max(1L, updateInterval - batchTime))
        }
    }

    private fun insertSpecies(species: List<Species>) {
        //language=MySQL
        val sql = """
            INSERT INTO `species` (name) VALUES (?) 
        """.trimIndent()
        val statement = connection?.prepareStatement(sql)

        for (theSpecies in species) {
            statement?.setString(1, theSpecies.name)
            statement?.addBatch()
        }

        statement?.executeBatch()
        statement?.closeOnCompletion()
    }

    private fun insertCreatures(creatures: List<Creature>) {
        //language=MySQL
        var sql = """
            INSERT INTO `chromosomes` 
              (id, replication_chance, death_chance, speed, size, colour_red, 
                colour_blue, colour_green, metabolic_efficiency, sensory_range, 
                greediness, thriftiness, shininess) 
              VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
        """.trimIndent()
        val insertChromosomeStatement = connection?.prepareStatement(sql)

        //language=MySQL
        sql = """
            INSERT INTO `creatures` (id, age, chromosome_id, species_id) 
              VALUES (?, ?, ?, ?)
        """.trimIndent()
        val insertCreatureStatement = connection?.prepareStatement(sql)

        for (creature in creatures) {
            insertChromosomeStatement?.setInt(1, creature.chromosome.id)
            insertChromosomeStatement?.setDouble(2, creature.chromosome[REPLICATION_CHANCE])
            insertChromosomeStatement?.setDouble(3, creature.chromosome[DEATH_CHANCE])
            insertChromosomeStatement?.setDouble(4, creature.chromosome[SPEED])
            insertChromosomeStatement?.setDouble(5, creature.chromosome[SIZE])
            insertChromosomeStatement?.setDouble(6, creature.chromosome[COLOUR_RED])
            insertChromosomeStatement?.setDouble(7, creature.chromosome[COLOUR_BLUE])
            insertChromosomeStatement?.setDouble(8, creature.chromosome[COLOUR_GREEN])
            insertChromosomeStatement?.setDouble(9, creature.chromosome[METABOLIC_EFFICIENCY])
            insertChromosomeStatement?.setDouble(10, creature.chromosome[SENSORY_RANGE])
            insertChromosomeStatement?.setDouble(11, creature.chromosome[GREEDINESS])
            insertChromosomeStatement?.setDouble(12, creature.chromosome[THRIFTINESS])
            insertChromosomeStatement?.setDouble(13, creature.chromosome[SHININESS])
            insertChromosomeStatement?.addBatch()

            insertCreatureStatement?.setInt(1, creature.id)
            insertCreatureStatement?.setInt(2, creature.age)
            insertCreatureStatement?.setInt(3, creature.chromosome.id)
            insertCreatureStatement?.setInt(4, creature.species!!.id)
            insertCreatureStatement?.addBatch()
        }

        insertChromosomeStatement?.executeBatch()
        insertChromosomeStatement?.closeOnCompletion()

        insertCreatureStatement?.executeBatch()
        insertCreatureStatement?.closeOnCompletion()
    }

    private fun insertCensuses(censuses: List<Census>) {
        //language=MySQL
        val sql: String = """
                INSERT INTO censuses (id) VALUES (?)
            """.trimIndent()
        val insertCensusStatement = connection?.prepareStatement(sql)

        //language=MySQL
        val updateCreatureSql = """
            UPDATE `creatures` SET age = ? WHERE id = ?
        """.trimIndent()
        val updateCreatureStatement = connection?.prepareStatement(updateCreatureSql)

        //language=MySQL
        val insertCensusParticipantSql = """
            INSERT INTO census_participants (census_id, participant_id) VALUES (?, ?)
        """.trimIndent()
        val insertCensusParticipantStatement = connection?.prepareStatement(insertCensusParticipantSql)

        for (census in censuses) {
            insertCensusStatement?.setInt(1, census.id)
            insertCensusStatement?.addBatch()

            for (creature in census.population) {
                updateCreatureStatement?.setInt(1, creature.age)
                updateCreatureStatement?.setInt(2, creature.id)
                updateCreatureStatement?.addBatch()

                insertCensusParticipantStatement?.setInt(1, census.id)
                insertCensusParticipantStatement?.setInt(2, creature.id)
                insertCensusParticipantStatement?.addBatch()
            }
        }

        insertCensusStatement?.executeBatch()
        insertCensusStatement?.closeOnCompletion()

        updateCreatureStatement?.executeBatch()
        updateCreatureStatement?.closeOnCompletion()

        insertCensusParticipantStatement?.executeBatch()
        insertCensusParticipantStatement?.closeOnCompletion()
    }

    fun cleanup() = runBlocking {
        shouldQuit = true
        batchProcessingCoroutine?.cancelAndJoin()

        connection?.close()
    }
}