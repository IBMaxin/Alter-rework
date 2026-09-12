package org.alter.plugins.content.skills.framework

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SkillNodeDataTest {
    private fun validLoot() = listOf(SkillLoot(item = "item.copper_ore"))

    private fun validNode(
        key: String = "object.copper_rock",
        levelRequired: Int = 1,
        xp: Double = 17.5,
        actionTicks: Int = 4,
        successLow: Int = 256,
        successHigh: Int = 256,
        respawnTicks: Int = 0,
        depletedKey: String? = null,
        loot: List<SkillLoot> = validLoot(),
    ) = SkillNode(
        key = key,
        levelRequired = levelRequired,
        xp = xp,
        actionTicks = actionTicks,
        animation = 625,
        successLow = successLow,
        successHigh = successHigh,
        respawnTicks = respawnTicks,
        depletedKey = depletedKey,
        loot = loot,
    )

    @Test
    fun `a valid node constructs`() {
        val node = validNode()

        assertEquals("object.copper_rock", node.key)
        assertEquals(17.5, node.xp)
        assertEquals(1, node.loot.size)
    }

    @Test
    fun `node rejects a blank key`() {
        assertFailsWith<IllegalArgumentException> { validNode(key = "  ") }
    }

    @Test
    fun `node rejects an out of range level requirement`() {
        assertFailsWith<IllegalArgumentException> { validNode(levelRequired = 0) }
        assertFailsWith<IllegalArgumentException> { validNode(levelRequired = 100) }
    }

    @Test
    fun `node rejects non positive xp`() {
        assertFailsWith<IllegalArgumentException> { validNode(xp = 0.0) }
        assertFailsWith<IllegalArgumentException> { validNode(xp = -1.0) }
    }

    @Test
    fun `node rejects a non positive action tick interval`() {
        assertFailsWith<IllegalArgumentException> { validNode(actionTicks = 0) }
    }

    @Test
    fun `node rejects negative success parameters`() {
        assertFailsWith<IllegalArgumentException> { validNode(successLow = -1) }
        assertFailsWith<IllegalArgumentException> { validNode(successHigh = -1) }
    }

    @Test
    fun `node rejects a negative respawn delay`() {
        assertFailsWith<IllegalArgumentException> { validNode(respawnTicks = -1) }
    }

    @Test
    fun `node rejects an empty loot table`() {
        assertFailsWith<IllegalArgumentException> { validNode(loot = emptyList()) }
    }

    @Test
    fun `loot rejects invalid bounds and chances`() {
        assertFailsWith<IllegalArgumentException> { SkillLoot(item = "item.copper_ore", min = 0) }
        assertFailsWith<IllegalArgumentException> { SkillLoot(item = "item.copper_ore", min = 2, max = 1) }
        assertFailsWith<IllegalArgumentException> { SkillLoot(item = "item.copper_ore", chance = 1.5) }
        assertFailsWith<IllegalArgumentException> { SkillLoot(item = "  ") }
    }

    @Test
    fun `success chance follows the osrs skilling success formula`() {
        val node = validNode(levelRequired = 1, successLow = 48, successHigh = 90)

        assertEquals(49.0 / 256.0, node.successChance(1), 1e-9)
        assertEquals(80.0 / 256.0, node.successChance(74), 1e-9)
        assertEquals(91.0 / 256.0, node.successChance(99), 1e-9)
    }

    @Test
    fun `success chance clamps levels outside one to ninety nine`() {
        val node = validNode(levelRequired = 1, successLow = 48, successHigh = 90)

        assertEquals(node.successChance(1), node.successChance(0), 1e-9)
        assertEquals(node.successChance(99), node.successChance(120), 1e-9)
    }

    @Test
    fun `success chance is clamped to one when the parameters exceed the cap`() {
        val node = validNode(successLow = 300, successHigh = 300)

        assertEquals(1.0, node.successChance(1), 1e-9)
        assertEquals(1.0, node.successChance(99), 1e-9)
    }
}
