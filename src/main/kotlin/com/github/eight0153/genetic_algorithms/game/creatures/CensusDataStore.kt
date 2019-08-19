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
import java.net.ConnectException
import java.sql.Connection
import java.sql.DriverManager
import kotlin.math.max
import kotlin.system.measureTimeMillis

/** A persistent data store for population data. */
class CensusDataStore(
    /** How often to update the backing store (i.e. perform database operations) in milliseconds. */
    private val updateInterval: Long = 1000L
) {

    private val DB_URL = System.getenv("DB_URL") ?: "jdbc:mysql://localhost:3306/"

    //  Database credentials
    private val DB_USER = System.getenv("DB_USER") ?: "root"
    private val DB_PASSWORD = System.getenv("DB_PASSWORD") ?: "password"

    private var connection: Connection? = null
    private var shouldQuit: Boolean = false

    private var runId: Int = -1

    private var batchProcessingCoroutine: Job? = null
    private val speciesBuffer = ArrayList<Species>()
    private val creaturesBuffer = ArrayList<Creature>()
    private val censusesBuffer = ArrayList<Census>()

    fun init() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
        } catch (e: Exception) {
            // TODO: Is it best to hide the original exception like this?
            throw ConnectException(
                "Something went wrong when trying to connect to the server.\n" +
                        "Check that the server is running and your environment variables are set up appropriately."
            )
        }

        val statement = connection?.createStatement()

        //language=MySQL
        var sql = """
            CREATE SCHEMA IF NOT EXISTS `genetic_algorithms` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            USE genetic_algorithms
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`runs`
            (
                `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT,
                `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (`id`),
                UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
            )
                ENGINE = InnoDB
            """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`censuses`
            (
                `id`           INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
                `run_id`       INT UNSIGNED     NOT NULL,
                `date_created` DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (`id`, `run_id`),
                INDEX `fk_censuses_runs1_idx` (`run_id` ASC) VISIBLE,
                CONSTRAINT `fk_censuses_runs1`
                    FOREIGN KEY (`run_id`)
                        REFERENCES `genetic_algorithms`.`runs` (`id`)
                        ON DELETE NO ACTION
                        ON UPDATE NO ACTION
            )
                ENGINE = InnoDB
                AUTO_INCREMENT = 4
                DEFAULT CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_0900_ai_ci
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`species`
            (
                `id`     INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
                `run_id` INT UNSIGNED     NOT NULL,
                `name`   VARCHAR(45)      NOT NULL,
                PRIMARY KEY (`id`, `run_id`),
                INDEX `fk_species_runs1_idx` (`run_id` ASC) VISIBLE,
                CONSTRAINT `fk_species_runs1`
                    FOREIGN KEY (`run_id`)
                        REFERENCES `genetic_algorithms`.`runs` (`id`)
                        ON DELETE NO ACTION
                        ON UPDATE NO ACTION
            )
                ENGINE = InnoDB
                AUTO_INCREMENT = 620
                DEFAULT CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_0900_ai_ci
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`creatures`
            (
                `id`                   INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
                `run_id`               INT UNSIGNED     NOT NULL,
                `species_id`           INT(10) UNSIGNED NOT NULL,
                `age`                  INT(10) UNSIGNED NOT NULL,
                `replication_chance`   DOUBLE           NOT NULL,
                `death_chance`         DOUBLE           NOT NULL,
                `speed`                DOUBLE           NOT NULL,
                `size`                 DOUBLE           NOT NULL,
                `colour_red`           DOUBLE           NOT NULL,
                `colour_green`         DOUBLE           NOT NULL,
                `colour_blue`          DOUBLE           NOT NULL,
                `metabolic_efficiency` DOUBLE           NOT NULL,
                `sensory_range`        DOUBLE           NOT NULL,
                `greediness`           DOUBLE           NOT NULL,
                `thriftiness`          DOUBLE           NOT NULL,
                `shininess`            DOUBLE           NOT NULL,
                PRIMARY KEY (`id`, `run_id`),
                INDEX `fk_creatures_runs1_idx` (`run_id` ASC) VISIBLE,
                INDEX `fk_creatures_species1_idx` (`species_id` ASC, `run_id` ASC) VISIBLE,
                CONSTRAINT `fk_creatures_runs1`
                    FOREIGN KEY (`run_id`)
                        REFERENCES `genetic_algorithms`.`runs` (`id`)
                        ON DELETE NO ACTION
                        ON UPDATE NO ACTION,
                CONSTRAINT `fk_creatures_species1`
                    FOREIGN KEY (`species_id`, `run_id`)
                        REFERENCES `genetic_algorithms`.`species` (`id`, `run_id`)
                        ON DELETE NO ACTION
                        ON UPDATE NO ACTION
            )
                ENGINE = InnoDB
                AUTO_INCREMENT = 2965
                DEFAULT CHARACTER SET = utf8mb4
                COLLATE = utf8mb4_0900_ai_ci
        """.trimIndent()
        statement?.execute(sql)

        //language=MySQL
        sql = """
            CREATE TABLE IF NOT EXISTS `genetic_algorithms`.`census_participants`
        (
            `run_id`       INT UNSIGNED     NOT NULL,
            `census_id`  INT(10) UNSIGNED NOT NULL,
            `creature_id` INT(10) UNSIGNED NOT NULL,
            PRIMARY KEY (`run_id`, `census_id`, `creature_id`),
            INDEX `fk_census_participants_runs1_idx` (`run_id` ASC) VISIBLE,
            INDEX `fk_census_participants_creatures1_idx` (`creature_id` ASC, `run_id` ASC) VISIBLE,
            CONSTRAINT `fk_census_participants_runs1`
                FOREIGN KEY (`run_id`)
                    REFERENCES `genetic_algorithms`.`runs` (`id`)
                    ON DELETE NO ACTION
                    ON UPDATE NO ACTION,
            CONSTRAINT `fk_census_participants_censuses1`
                FOREIGN KEY (`run_id`, `census_id`)
                    REFERENCES `genetic_algorithms`.`censuses` (`run_id`, `id`)
                    ON DELETE NO ACTION
                    ON UPDATE NO ACTION,
            CONSTRAINT `fk_census_participants_creatures1`
                FOREIGN KEY (`creature_id`, `run_id`)
                    REFERENCES `genetic_algorithms`.`creatures` (`id`, `run_id`)
                    ON DELETE NO ACTION
                    ON UPDATE NO ACTION
        )
            ENGINE = InnoDB
            DEFAULT CHARACTER SET = utf8mb4
            COLLATE = utf8mb4_0900_ai_ci
        """.trimIndent()
        statement?.execute(sql)

        sql = """
            INSERT INTO `genetic_algorithms`.`runs` VALUES ()
        """.trimIndent()
        statement?.execute(sql)

        sql = """
            SELECT LAST_INSERT_ID()
        """.trimIndent()
        val resultSet = statement?.executeQuery(sql)

        if (resultSet?.first() == true) {
            runId = resultSet.getInt(1)
        }

        resultSet?.close()

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
                val creaturesBatch = java.util.ArrayList<Creature>(creaturesBuffer.size)
                val censusesBatch = java.util.ArrayList<Census>(censusesBuffer.size)

                speciesBatch.addAll(speciesBuffer)
                creaturesBatch.addAll(creaturesBuffer)
                censusesBatch.addAll(censusesBuffer)

                speciesBuffer.clear()
                creaturesBuffer.clear()
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
            INSERT INTO `species` (id, run_id, name) VALUES (?, $runId, ?) 
        """.trimIndent()
        val statement = connection?.prepareStatement(sql)

        for (theSpecies in species) {
            statement?.setInt(1, theSpecies.id)
            statement?.setString(2, theSpecies.name)
            statement?.addBatch()
        }

        statement?.executeBatch()
        statement?.closeOnCompletion()
    }

    private fun insertCreatures(creatures: List<Creature>) {
        //language=MySQL
        val sql = """
            INSERT INTO `creatures` (id, run_id, species_id, age, 
            replication_chance, death_chance, speed, size, 
            colour_red, colour_green, colour_blue, metabolic_efficiency, 
            sensory_range, greediness, thriftiness, shininess) 
              VALUES ( ?, $runId,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val insertCreatureStatement = connection?.prepareStatement(sql)

        for (creature in creatures) {
            insertCreatureStatement?.setInt(1, creature.id)
            insertCreatureStatement?.setInt(2, creature.species!!.id)
            insertCreatureStatement?.setInt(3, creature.age)
            insertCreatureStatement?.setDouble(4, creature.chromosome[REPLICATION_CHANCE])
            insertCreatureStatement?.setDouble(5, creature.chromosome[DEATH_CHANCE])
            insertCreatureStatement?.setDouble(6, creature.chromosome[SPEED])
            insertCreatureStatement?.setDouble(7, creature.chromosome[SIZE])
            insertCreatureStatement?.setDouble(8, creature.chromosome[COLOUR_RED])
            insertCreatureStatement?.setDouble(9, creature.chromosome[COLOUR_BLUE])
            insertCreatureStatement?.setDouble(10, creature.chromosome[COLOUR_GREEN])
            insertCreatureStatement?.setDouble(11, creature.chromosome[METABOLIC_EFFICIENCY])
            insertCreatureStatement?.setDouble(12, creature.chromosome[SENSORY_RANGE])
            insertCreatureStatement?.setDouble(13, creature.chromosome[GREEDINESS])
            insertCreatureStatement?.setDouble(14, creature.chromosome[THRIFTINESS])
            insertCreatureStatement?.setDouble(15, creature.chromosome[SHININESS])
            insertCreatureStatement?.addBatch()
        }

        insertCreatureStatement?.executeBatch()
        insertCreatureStatement?.closeOnCompletion()
    }

    private fun insertCensuses(censuses: List<Census>) {
        //language=MySQL
        var sql: String = """
                INSERT INTO censuses (id, run_id) VALUES (?, $runId)
            """.trimIndent()
        val insertCensusStatement = connection?.prepareStatement(sql)

        //language=MySQL
        sql = """
            UPDATE `creatures` SET age = ? WHERE id = ? AND run_id = $runId
        """.trimIndent()
        val updateCreatureStatement = connection?.prepareStatement(sql)

        //language=MySQL
        sql = """
            INSERT INTO census_participants (census_id, creature_id, run_id) VALUES (?, ?, $runId)
        """.trimIndent()
        val insertCensusParticipantStatement = connection?.prepareStatement(sql)

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