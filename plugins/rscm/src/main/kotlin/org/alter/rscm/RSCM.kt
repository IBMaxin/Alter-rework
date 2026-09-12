package org.alter.rscm

import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path

/**
 * @author Cl0udS3c
 */
object RSCM {
    private var rscmList = mutableMapOf<String, Int>()
    val logger = KotlinLogging.logger {}
    fun getRSCM(entity: Array<String>): List<Int> = entity.map { getRSCM(it) }.toList()
    fun Int.asRSCM(table: String): String {
        return rscmList.entries.find { it.value == this && it.key.startsWith("$table.") }?.key
            ?: throw IllegalStateException("No RSCM entry found for ID $this with prefix '$table'.")
    }
    fun getRSCM(entity: String) : Int {
        if (rscmList.isEmpty()) {
            throw IllegalStateException("RSCM List is empty.")
        }
        var result = rscmList[entity] ?: -1
        if (result == -1) {
            throw IllegalStateException("RSCM returned -1 for $entity.")
        }
        return result
    }

    fun init() {
        initRSCM()
        logger.info { "RSCM Loaded" }
    }

    fun initRSCM() {
        Path.of("../data/cfg/rscm/").toFile().listFiles()?.forEach { file ->
            val map = file.name.replace(".rscm", "")
            file.bufferedReader(Charsets.UTF_8).use { buff ->
                buff.lineSequence().forEachIndexed { index, line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEachIndexed
                    val divider = line.split(":")
                    if (divider.size == 2) {
                        val key = "$map." + divider[0].trim()
                        val value = divider[1].trim().toIntOrNull()
                        if (value == null) {
                            logger.warn {
                                "Skipping malformed RSCM line in ${file.name}:${index + 1} - \"$trimmed\" " +
                                    "(expected '<name>:<id>')"
                            }
                            return@forEachIndexed
                        }
                        rscmList[key] = value
                    } else {
                        logger.warn { "$trimmed not enough arguments" }
                    }
                }
            }
        }
    }

    fun test() {
        // @TODO Check if some values match what was expected.
    }

}