package org.alter.plugins.content.skills.slayer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Pure assignment rules: which of a master's tasks a player may receive, the
 * weighted task roll, and the kill-count roll. All randomness is injected as a
 * roll in `[0.0, 1.0)` so the behaviour is deterministic in tests.
 */
class SlayerAssignmentTest {
    private val npcIds =
        mapOf(
            "npc.goblin_1" to 100,
            "npc.rat" to 200,
            "npc.turael" to 900,
        )

    private fun repository() =
        SlayerRepository(
            tasks =
                listOf(
                    task("goblins", categoryId = 2, requiredLevel = 1, amountMin = 10, amountMax = 25, weight = 1.0),
                    task("rats", categoryId = 3, requiredLevel = 5, amountMin = 5, amountMax = 5, weight = 3.0),
                ),
            masters =
                listOf(
                    SlayerMasterEntry(
                        npc = "npc.turael",
                        level = 1,
                        pointsPerTask = 0,
                        tasks = listOf("goblins", "rats"),
                    ),
                ),
            resolveNpc = { npcIds.getValue(it) },
        )

    private fun task(
        name: String,
        categoryId: Int,
        requiredLevel: Int,
        amountMin: Int,
        amountMax: Int,
        weight: Double,
    ) = SlayerTaskEntry(
        taskName = name,
        categoryId = categoryId,
        npcIds = listOf("npc.goblin_1"),
        requiredLevel = requiredLevel,
        xp = 5.0,
        amountMin = amountMin,
        amountMax = amountMax,
        masters = listOf("npc.turael"),
        weight = weight,
    )

    @Test
    fun `eligible tasks only include the master's tasks at or below the player's level`() {
        val repo = repository()
        val master = repo.getMaster(900)!!

        assertEquals(listOf("goblins"), SlayerMechanics.eligibleTasks(master, repo, slayerLevel = 1).map { it.taskName })
        assertEquals(listOf("goblins", "rats"), SlayerMechanics.eligibleTasks(master, repo, slayerLevel = 99).map { it.taskName })
    }

    @Test
    fun `a task that cannot be resolved is skipped`() {
        val repo = repository()
        val master = repo.getMaster(900)!!.copy(tasks = listOf("goblins", "unknown"))

        assertEquals(listOf("goblins"), SlayerMechanics.eligibleTasks(master, repo, slayerLevel = 99).map { it.taskName })
    }

    @Test
    fun `weighted roll selects the expected task`() {
        val repo = repository()
        val eligible = SlayerMechanics.eligibleTasks(repo.getMaster(900)!!, repo, slayerLevel = 99)

        assertEquals("goblins", SlayerMechanics.chooseTask(eligible, roll = 0.0)?.taskName)
        assertEquals("goblins", SlayerMechanics.chooseTask(eligible, roll = 0.24)?.taskName)
        assertEquals("rats", SlayerMechanics.chooseTask(eligible, roll = 0.25)?.taskName)
        assertEquals("rats", SlayerMechanics.chooseTask(eligible, roll = 0.99)?.taskName)
    }

    @Test
    fun `weighted roll over no tasks returns null`() {
        assertNull(SlayerMechanics.chooseTask(emptyList(), roll = 0.5))
    }

    @Test
    fun `amount roll stays within the task range`() {
        val task = repository().getTask("goblins")!!

        assertEquals(10, SlayerMechanics.chooseAmount(task, roll = 0.0))
        assertEquals(25, SlayerMechanics.chooseAmount(task, roll = 0.999999))
        assert(SlayerMechanics.chooseAmount(task, roll = 0.5) in 10..25)
    }

    @Test
    fun `amount roll returns the only value for a fixed amount task`() {
        val task = repository().getTask("rats")!!
        assertEquals(5, SlayerMechanics.chooseAmount(task, roll = 0.42))
    }

    @Test
    fun `assign picks a task and an amount`() {
        val repo = repository()
        val assignment =
            SlayerMechanics.assign(
                master = repo.getMaster(900)!!,
                repository = repo,
                slayerLevel = 99,
                taskRoll = 0.99,
                amountRoll = 0.0,
            )

        assertEquals("rats", assignment?.task?.taskName)
        assertEquals(5, assignment?.amount)
    }

    @Test
    fun `assign returns null when no task is eligible`() {
        val repo = repository()
        assertNull(
            SlayerMechanics.assign(
                master = repo.getMaster(900)!!,
                repository = repo,
                slayerLevel = 0,
                taskRoll = 0.5,
                amountRoll = 0.5,
            ),
        )
    }
}
