package org.alter.plugins.content.skills.framework

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GatherRollsTest {

    private fun node(
        successLow: Int,
        successHigh: Int,
        loot: List<SkillLoot>,
    ): ResolvedSkillNode =
        ResolvedSkillNode(
            node =
                SkillNode(
                    key = "object.rock",
                    levelRequired = 1,
                    xp = 1.0,
                    successLow = successLow,
                    successHigh = successHigh,
                    loot = loot,
                ),
            objectIds = intArrayOf(1),
            depletedObjectId = null,
            loot = loot.mapIndexed { index, spec -> ResolvedSkillLoot(index + 1, spec) },
        )

    @Test
    fun `a roll below the success chance succeeds`() {
        val node = node(successLow = 256, successHigh = 256, loot = listOf(SkillLoot("item.rock")))

        assertEquals(1.0, node.successChance(50), 1e-9)
        assertTrue(GatherRolls.rollSuccess(node, 50, 0.9999))
    }

    @Test
    fun `a roll at or above the success chance fails`() {
        val node = node(successLow = 0, successHigh = 0, loot = listOf(SkillLoot("item.rock")))

        assertEquals(1.0 / 256.0, node.successChance(50), 1e-9)
        assertFalse(GatherRolls.rollSuccess(node, 50, 0.5))
        assertFalse(GatherRolls.rollSuccess(node, 50, 1.0 / 256.0))
        assertTrue(GatherRolls.rollSuccess(node, 50, 0.003))
    }

    @Test
    fun `loot with a full chance is always produced`() {
        val node =
            node(
                successLow = 0,
                successHigh = 0,
                loot = listOf(SkillLoot(item = "item.rock", min = 1, max = 3, chance = 1.0)),
            )

        val values = ArrayDeque(listOf(0.0, 0.0))
        val result = GatherRolls.rollLoot(node) { values.removeFirst() }

        assertEquals(1, result.single().second)
    }

    @Test
    fun `loot amount respects the inclusive maximum`() {
        val node =
            node(
                successLow = 0,
                successHigh = 0,
                loot = listOf(SkillLoot(item = "item.rock", min = 1, max = 3, chance = 1.0)),
            )

        val values = ArrayDeque(listOf(0.0, 0.9999))
        val result = GatherRolls.rollLoot(node) { values.removeFirst() }

        assertEquals(3, result.single().second)
    }

    @Test
    fun `loot with a zero chance is skipped`() {
        val node =
            node(
                successLow = 0,
                successHigh = 0,
                loot = listOf(SkillLoot(item = "item.rock", chance = 0.0)),
            )

        val result = GatherRolls.rollLoot(node) { 0.0 }

        assertTrue(result.isEmpty())
    }

    @Test
    fun `every independent loot entry is rolled`() {
        val node =
            node(
                successLow = 0,
                successHigh = 0,
                loot =
                    listOf(
                        SkillLoot(item = "item.always", min = 2, max = 2, chance = 1.0),
                        SkillLoot(item = "item.rare", min = 2, max = 5, chance = 0.5),
                    ),
            )

        val values = ArrayDeque(listOf(0.0, 0.4, 0.0))
        val result = GatherRolls.rollLoot(node) { values.removeFirst() }

        assertEquals(listOf(1, 2), result.map { it.first.itemId })
        assertEquals(listOf(2, 2), result.map { it.second })
    }
}
