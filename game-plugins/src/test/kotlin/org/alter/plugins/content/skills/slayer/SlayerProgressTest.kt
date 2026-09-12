package org.alter.plugins.content.skills.slayer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Pure kill-credit rules: matching a killed NPC to the player's active task,
 * awarding experience, decrementing the remaining count and completing the task
 * for points.
 */
class SlayerProgressTest {
    private fun goblins() =
        SlayerTaskEntry(
            taskName = "goblins",
            categoryId = 2,
            npcIds = listOf("npc.goblin_1"),
            requiredLevel = 1,
            xp = 5.0,
            bonusXp = 250.0,
            amountMin = 10,
            amountMax = 25,
        )

    private fun rats() = goblins().copy(taskName = "rats", categoryId = 3, xp = 3.0)

    @Test
    fun `no credit when the player has no active task`() {
        assertNull(SlayerProgress.onKill(assigned = null, killTask = goblins(), remaining = 5, pointsPerTask = 15))
    }

    @Test
    fun `no credit when the killed npc is not part of the task`() {
        assertNull(SlayerProgress.onKill(assigned = goblins(), killTask = rats(), remaining = 5, pointsPerTask = 15))
    }

    @Test
    fun `no credit when the task is already complete`() {
        assertNull(SlayerProgress.onKill(assigned = goblins(), killTask = goblins(), remaining = 0, pointsPerTask = 15))
    }

    @Test
    fun `a kill awards the task xp and decrements the counter`() {
        val result = SlayerProgress.onKill(goblins(), goblins(), remaining = 5, pointsPerTask = 15)!!

        assertEquals(5.0, result.xp)
        assertEquals(0.0, result.bonusXp)
        assertEquals(4, result.remaining)
        assertEquals(0, result.pointsAwarded)
        assertEquals(false, result.completed)
    }

    @Test
    fun `the final kill completes the task and awards points plus bonus xp`() {
        val result = SlayerProgress.onKill(goblins(), goblins(), remaining = 1, pointsPerTask = 15)!!

        assertEquals(5.0, result.xp)
        assertEquals(250.0, result.bonusXp)
        assertEquals(0, result.remaining)
        assertEquals(15, result.pointsAwarded)
        assertEquals(true, result.completed)
    }
}
