package org.alter.game.model.combat

import org.alter.game.model.entity.Npc
import kotlin.test.Test
import kotlin.test.assertEquals

class NpcCombatStatsTest {
    @Test
    fun `combat definition stats propagate into npc stat levels`() {
        val stats = Npc.Stats(5)
        val def =
            NpcCombatDef.DEFAULT.copy(
                attack = 70,
                strength = 65,
                defence = 60,
                magic = 55,
                ranged = 50,
                hitpoints = 120,
            )

        stats.applyCombatStats(def)

        assertEquals(70, stats.getMaxLevel(NpcSkills.ATTACK))
        assertEquals(65, stats.getMaxLevel(NpcSkills.STRENGTH))
        assertEquals(60, stats.getMaxLevel(NpcSkills.DEFENCE))
        assertEquals(55, stats.getMaxLevel(NpcSkills.MAGIC))
        assertEquals(50, stats.getMaxLevel(NpcSkills.RANGED))

        assertEquals(70, stats.getCurrentLevel(NpcSkills.ATTACK))
        assertEquals(65, stats.getCurrentLevel(NpcSkills.STRENGTH))
        assertEquals(60, stats.getCurrentLevel(NpcSkills.DEFENCE))
        assertEquals(55, stats.getCurrentLevel(NpcSkills.MAGIC))
        assertEquals(50, stats.getCurrentLevel(NpcSkills.RANGED))
    }
}
