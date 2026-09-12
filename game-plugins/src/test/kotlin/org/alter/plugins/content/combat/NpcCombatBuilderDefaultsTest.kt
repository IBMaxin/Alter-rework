package org.alter.plugins.content.combat

import org.alter.api.NpcCombatBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class NpcCombatBuilderDefaultsTest {
    private fun minimalBuilder() =
        NpcCombatBuilder().apply {
            setHitpoints(10)
            setAttackSpeed(4)
            setDeathAnimation(836)
            setRespawnDelay(25)
        }

    @Test
    fun `combat levels default to one when no stats block is provided`() {
        val def = minimalBuilder().build()

        assertEquals(1, def.attack)
        assertEquals(1, def.strength)
        assertEquals(1, def.defence)
        assertEquals(1, def.magic)
        assertEquals(1, def.ranged)
    }

    @Test
    fun `explicit combat levels are preserved`() {
        val def =
            minimalBuilder()
                .apply {
                    setAttackLevel(70)
                    setStrengthLevel(65)
                    setDefenceLevel(60)
                    setMagicLevel(55)
                    setRangedLevel(50)
                }.build()

        assertEquals(70, def.attack)
        assertEquals(65, def.strength)
        assertEquals(60, def.defence)
        assertEquals(55, def.magic)
        assertEquals(50, def.ranged)
    }
}
