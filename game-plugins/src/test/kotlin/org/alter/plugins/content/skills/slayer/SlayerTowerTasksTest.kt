package org.alter.plugins.content.skills.slayer

import org.alter.rscm.RSCM
import org.junit.BeforeClass
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Validates the Slayer Tower task definitions load, resolve NPC IDs,
 * and are assignable by Duradel.
 */
class SlayerTowerTasksTest {

    private fun loadRepository(): SlayerRepository {
        val service = SlayerService()
        service.load(
            tasksFile = Paths.get("../data/cfg/slayer/tasks.json"),
            mastersFile = Paths.get("../data/cfg/slayer/masters.json"),
        )
        return service.repository
    }

    @Test
    fun `all slayer tower tasks exist`() {
        val repo = loadRepository()
        val towerTasks = listOf(
            "crawling_hand",
            "banshee",
            "infernal_mage",
            "bloodveld",
            "aberrant_spectre",
            "gargoyles",
            "nechryael",
            "abyssal_demons",
        )
        towerTasks.forEach { name ->
            assertNotNull(repo.getTask(name), "Task '$name' should exist in tasks.json")
        }
    }

    @Test
    fun `tower tasks have resolved npc ids`() {
        val repo = loadRepository()
        val towerTasks = listOf(
            "crawling_hand",
            "banshee",
            "infernal_mage",
            "bloodveld",
            "aberrant_spectre",
            "gargoyles",
            "nechryael",
            "abyssal_demons",
        )
        towerTasks.forEach { name ->
            val task = repo.getTask(name)!!
            assertTrue(task.resolvedNpcIds.isNotEmpty(), "Task '$name' should have resolved NPC IDs")
            assertEquals(task.npcIds.size, task.resolvedNpcIds.size, "Task '$name' NPC ID count mismatch")
        }
    }

    @Test
    fun `tower tasks have correct slayer level requirements`() {
        val repo = loadRepository()
        assertEquals(5, repo.getTask("crawling_hand")!!.requiredLevel)
        assertEquals(15, repo.getTask("banshee")!!.requiredLevel)
        assertEquals(45, repo.getTask("infernal_mage")!!.requiredLevel)
        assertEquals(50, repo.getTask("bloodveld")!!.requiredLevel)
        assertEquals(60, repo.getTask("aberrant_spectre")!!.requiredLevel)
        assertEquals(75, repo.getTask("gargoyles")!!.requiredLevel)
        assertEquals(80, repo.getTask("nechryael")!!.requiredLevel)
        assertEquals(85, repo.getTask("abyssal_demons")!!.requiredLevel)
    }

    @Test
    fun `tower tasks have correct slayer xp`() {
        val repo = loadRepository()
        assertEquals(12.0, repo.getTask("crawling_hand")!!.xp)
        assertEquals(22.0, repo.getTask("banshee")!!.xp)
        assertEquals(60.0, repo.getTask("infernal_mage")!!.xp)
        assertEquals(120.0, repo.getTask("bloodveld")!!.xp)
        assertEquals(90.0, repo.getTask("aberrant_spectre")!!.xp)
        assertEquals(105.0, repo.getTask("gargoyles")!!.xp)
        assertEquals(105.0, repo.getTask("nechryael")!!.xp)
        assertEquals(150.0, repo.getTask("abyssal_demons")!!.xp)
    }

    @Test
    fun `duradel can assign all tower tasks at level 99`() {
        val repo = loadRepository()
        val duradel = repo.getMaster(RSCM.getRSCM("npc.duradel_13622"))
        assertNotNull(duradel, "Duradel should exist")

        val eligible = SlayerMechanics.eligibleTasks(duradel, repo, slayerLevel = 99)
        val names = eligible.map { it.taskName }

        listOf(
            "crawling_hand", "banshee", "infernal_mage", "aberrant_spectre",
        ).forEach { name ->
            assertTrue(name in names, "Duradel should be able to assign '$name' at level 99")
        }
    }

    @Test
    fun `tower tasks are not assignable below their level requirement`() {
        val repo = loadRepository()
        val duradel = repo.getMaster(RSCM.getRSCM("npc.duradel_13622"))!!

        val eligibleLow = SlayerMechanics.eligibleTasks(duradel, repo, slayerLevel = 1)
        val namesLow = eligibleLow.map { it.taskName }

        listOf(
            "crawling_hand", "banshee", "infernal_mage", "aberrant_spectre",
            "gargoyles", "nechryael", "abyssal_demons",
        ).forEach { name ->
            assertTrue(name !in namesLow, "Task '$name' should NOT be assignable at level 1")
        }
    }

    @Test
    fun `tower tasks have unique category ids`() {
        val repo = loadRepository()
        val towerTasks = listOf(
            "crawling_hand", "banshee", "infernal_mage", "bloodveld",
            "aberrant_spectre", "gargoyles", "nechryael", "abyssal_demons",
        )
        val categoryIds = towerTasks.map { repo.getTask(it)!!.categoryId }
        assertEquals(categoryIds.size, categoryIds.toSet().size, "Tower tasks must have unique category IDs")
    }

    @Test
    fun `tower tasks have positive weights`() {
        val repo = loadRepository()
        val towerTasks = listOf(
            "crawling_hand", "banshee", "infernal_mage", "bloodveld",
            "aberrant_spectre", "gargoyles", "nechryael", "abyssal_demons",
        )
        towerTasks.forEach { name ->
            val task = repo.getTask(name)!!
            assertTrue(task.weight > 0.0, "Task '$name' should have positive weight")
        }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun loadRscm() {
            RSCM.init()
        }
    }
}
