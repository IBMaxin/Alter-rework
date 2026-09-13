package org.alter.plugins.content.skills.slayer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Validation contract for the immutable Slayer configuration entries loaded from
 * [data/cfg/slayer/tasks.json] and [data/cfg/slayer/masters.json].
 */
class SlayerDataTest {
    @Test
    fun `task entry accepts a valid definition`() {
        val task =
            SlayerTaskEntry(
                taskName = "goblins",
                displayName = "Goblins",
                categoryId = 2,
                npcIds = listOf("npc.goblin_1", "npc.goblin_2"),
                requiredLevel = 1,
                xp = 5.0,
                amountMin = 10,
                amountMax = 25,
                masters = listOf("npc.turael"),
                weight = 10.0,
            )

        assertEquals("goblins", task.taskName)
        assertEquals("Goblins", task.displayName)
        assertEquals(2, task.categoryId)
        assertEquals(2, task.npcIds.size)
    }

    @Test
    fun `task entry rejects a blank task name`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(taskName = "  ")
        }
    }

    @Test
    fun `task entry rejects a blank display name`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(displayName = "  ")
        }
    }

    @Test
    fun `task entry rejects a negative category id`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(categoryId = -1)
        }
    }

    @Test
    fun `task entry rejects an empty npc list`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(npcIds = emptyList())
        }
    }

    @Test
    fun `task entry rejects a level requirement below one`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(requiredLevel = 0)
        }
    }

    @Test
    fun `task entry rejects negative experience`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(xp = -0.1)
        }
    }

    @Test
    fun `task entry rejects negative bonus experience`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(bonusXp = -0.1)
        }
    }

    @Test
    fun `task entry rejects a minimum amount below one`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(amountMin = 0)
        }
    }

    @Test
    fun `task entry rejects a maximum amount lower than the minimum`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(amountMin = 20, amountMax = 10)
        }
    }

    @Test
    fun `task entry rejects a non positive weight`() {
        assertFailsWith<IllegalArgumentException> {
            validTask().copy(weight = 0.0)
        }
    }

    @Test
    fun `master entry accepts a valid definition`() {
        val master =
            SlayerMasterEntry(
                npc = "npc.turael",
                level = 1,
                pointsPerTask = 0,
                tasks = listOf("goblins", "rats"),
            )

        assertEquals("npc.turael", master.npc)
        assertEquals(0, master.pointsPerTask)
        assertEquals(2, master.tasks.size)
    }

    @Test
    fun `master entry rejects a blank npc`() {
        assertFailsWith<IllegalArgumentException> {
            validMaster().copy(npc = "")
        }
    }

    @Test
    fun `master entry rejects a level below one`() {
        assertFailsWith<IllegalArgumentException> {
            validMaster().copy(level = 0)
        }
    }

    @Test
    fun `master entry rejects negative points per task`() {
        assertFailsWith<IllegalArgumentException> {
            validMaster().copy(pointsPerTask = -1)
        }
    }

    @Test
    fun `master entry rejects an empty task list`() {
        assertFailsWith<IllegalArgumentException> {
            validMaster().copy(tasks = emptyList())
        }
    }

    private fun validTask() =
        SlayerTaskEntry(
            taskName = "goblins",
            displayName = "Goblins",
            categoryId = 2,
            npcIds = listOf("npc.goblin_1"),
            requiredLevel = 1,
            xp = 5.0,
            amountMin = 10,
            amountMax = 25,
            masters = listOf("npc.turael"),
            weight = 10.0,
        )

    private fun validMaster() =
        SlayerMasterEntry(
            npc = "npc.turael",
            level = 1,
            pointsPerTask = 0,
            tasks = listOf("goblins"),
        )
}
