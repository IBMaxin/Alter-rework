package org.alter.plugins.content.npcs.slayer_tower

import org.alter.api.NpcCombatBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Validates the Slayer Tower NPC combat definitions produce correct
 * slayer requirements and XP values through the NpcCombatBuilder.
 */
class SlayerTowerCombatDefTest {

    private fun buildCrawlingHand(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(13)
        b.setAttackSpeed(3)
        b.setRespawnDelay(25)
        b.setAttackLevel(10)
        b.setStrengthLevel(10)
        b.setDefenceLevel(10)
        b.setMagicLevel(1)
        b.setRangedLevel(1)
        b.setDeathAnimation(1590)
        b.setSlayerParams(levelReq = 5, xp = 12.0)
        return b
    }

    private fun buildBanshee(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(22)
        b.setAttackSpeed(4)
        b.setRespawnDelay(25)
        b.setAttackLevel(30)
        b.setStrengthLevel(25)
        b.setDefenceLevel(25)
        b.setMagicLevel(1)
        b.setRangedLevel(1)
        b.setDeathAnimation(1524)
        b.setSlayerParams(levelReq = 15, xp = 22.0)
        return b
    }

    private fun buildInfernalMage(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(64)
        b.setAttackSpeed(4)
        b.setRespawnDelay(25)
        b.setAttackLevel(68)
        b.setStrengthLevel(65)
        b.setDefenceLevel(60)
        b.setMagicLevel(80)
        b.setRangedLevel(1)
        b.setDeathAnimation(1508)
        b.setSlayerParams(levelReq = 45, xp = 60.0)
        return b
    }

    private fun buildBloodveld(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(124)
        b.setAttackSpeed(4)
        b.setRespawnDelay(25)
        b.setAttackLevel(96)
        b.setStrengthLevel(110)
        b.setDefenceLevel(100)
        b.setMagicLevel(1)
        b.setRangedLevel(1)
        b.setDeathAnimation(1553)
        b.setSlayerParams(levelReq = 50, xp = 120.0)
        return b
    }

    private fun buildAberrantSpectre(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(96)
        b.setAttackSpeed(4)
        b.setRespawnDelay(25)
        b.setAttackLevel(86)
        b.setStrengthLevel(84)
        b.setDefenceLevel(80)
        b.setMagicLevel(1)
        b.setRangedLevel(1)
        b.setDeathAnimation(1508)
        b.setSlayerParams(levelReq = 60, xp = 90.0)
        return b
    }

    private fun buildGargoyle(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(115)
        b.setAttackSpeed(4)
        b.setRespawnDelay(25)
        b.setAttackLevel(111)
        b.setStrengthLevel(118)
        b.setDefenceLevel(120)
        b.setMagicLevel(1)
        b.setRangedLevel(1)
        b.setDeathAnimation(1520)
        b.setSlayerParams(levelReq = 75, xp = 105.0)
        return b
    }

    private fun buildNechryael(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(175)
        b.setAttackSpeed(4)
        b.setRespawnDelay(25)
        b.setAttackLevel(140)
        b.setStrengthLevel(150)
        b.setDefenceLevel(140)
        b.setMagicLevel(1)
        b.setRangedLevel(1)
        b.setDeathAnimation(1530)
        b.setSlayerParams(levelReq = 80, xp = 105.0)
        return b
    }

    private fun buildAbyssalDemon(): NpcCombatBuilder {
        val b = NpcCombatBuilder()
        b.setHitpoints(150)
        b.setAttackSpeed(4)
        b.setRespawnDelay(25)
        b.setAttackLevel(138)
        b.setStrengthLevel(135)
        b.setDefenceLevel(140)
        b.setMagicLevel(1)
        b.setRangedLevel(1)
        b.setDeathAnimation(1538)
        b.setSlayerParams(levelReq = 85, xp = 150.0)
        return b
    }

    @Test
    fun `crawling hand has correct slayer data`() {
        val def = buildCrawlingHand().build()
        assertEquals(5, def.slayerReq)
        assertEquals(12.0, def.slayerXp)
        assertEquals(13, def.hitpoints)
    }

    @Test
    fun `banshee has correct slayer data`() {
        val def = buildBanshee().build()
        assertEquals(15, def.slayerReq)
        assertEquals(22.0, def.slayerXp)
        assertEquals(22, def.hitpoints)
    }

    @Test
    fun `infernal mage has correct slayer data`() {
        val def = buildInfernalMage().build()
        assertEquals(45, def.slayerReq)
        assertEquals(60.0, def.slayerXp)
        assertEquals(64, def.hitpoints)
    }

    @Test
    fun `bloodveld has correct slayer data`() {
        val def = buildBloodveld().build()
        assertEquals(50, def.slayerReq)
        assertEquals(120.0, def.slayerXp)
        assertEquals(124, def.hitpoints)
    }

    @Test
    fun `aberrant spectre has correct slayer data`() {
        val def = buildAberrantSpectre().build()
        assertEquals(60, def.slayerReq)
        assertEquals(90.0, def.slayerXp)
        assertEquals(96, def.hitpoints)
    }

    @Test
    fun `gargoyle has correct slayer data`() {
        val def = buildGargoyle().build()
        assertEquals(75, def.slayerReq)
        assertEquals(105.0, def.slayerXp)
        assertEquals(115, def.hitpoints)
    }

    @Test
    fun `nechryael has correct slayer data`() {
        val def = buildNechryael().build()
        assertEquals(80, def.slayerReq)
        assertEquals(105.0, def.slayerXp)
        assertEquals(175, def.hitpoints)
    }

    @Test
    fun `abyssal demon has correct slayer data`() {
        val def = buildAbyssalDemon().build()
        assertEquals(85, def.slayerReq)
        assertEquals(150.0, def.slayerXp)
        assertEquals(150, def.hitpoints)
    }

    @Test
    fun `all tower npcs have slayer requirements above 1`() {
        listOf(
            buildCrawlingHand().build(),
            buildBanshee().build(),
            buildInfernalMage().build(),
            buildBloodveld().build(),
            buildAberrantSpectre().build(),
            buildGargoyle().build(),
            buildNechryael().build(),
            buildAbyssalDemon().build(),
        ).forEach { def ->
            assert(def.slayerReq > 1) { "NPC slayer requirement should be > 1, got ${def.slayerReq}" }
            assert(def.slayerXp > 0.0) { "NPC slayer XP should be > 0, got ${def.slayerXp}" }
        }
    }

    @Test
    fun `slayer requirements are in ascending order`() {
        val reqs = listOf(
            buildCrawlingHand().build().slayerReq,
            buildBanshee().build().slayerReq,
            buildInfernalMage().build().slayerReq,
            buildBloodveld().build().slayerReq,
            buildAberrantSpectre().build().slayerReq,
            buildGargoyle().build().slayerReq,
            buildNechryael().build().slayerReq,
            buildAbyssalDemon().build().slayerReq,
        )
        for (i in 1 until reqs.size) {
            assert(reqs[i] > reqs[i - 1]) {
                "Slayer requirements should be ascending: ${reqs[i - 1]} < ${reqs[i]}"
            }
        }
    }
}
