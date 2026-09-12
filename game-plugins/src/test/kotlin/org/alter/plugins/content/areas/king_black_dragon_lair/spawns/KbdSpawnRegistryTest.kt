package org.alter.plugins.content.areas.king_black_dragon_lair.spawns

import org.alter.plugins.content.npcs.kbd.KbdConfigsPlugin
import org.alter.plugins.testing.PluginIntegrationSupport
import org.alter.rscm.RSCM
import org.alter.rscm.RSCM.getRSCM
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Cross-references the King Black Dragon spawn against the plugin that
 * registers its combat definition.
 */
class KbdSpawnRegistryTest {

    @Test
    fun `spawn plugin targets the same npc as the combat plugin`() {
        assertEquals(KbdConfigsPlugin.NPC_ID, KbdSpawnPlugin.NPC_ID)
        assertEquals(listOf(KbdConfigsPlugin.NPC_ID), KbdSpawnPlugin.SPAWNED_NPC_IDS)
    }

    @Test
    fun `every spawned npc id resolves through rscm`() {
        KbdSpawnPlugin.SPAWNED_NPC_IDS.forEach { RSCM.getRSCM(it) }
    }

    @Test
    fun `every spawned npc has a registered combat definition`() {
        val world = PluginIntegrationSupport.newWorld("kbd-registry")
        KbdConfigsPlugin(world.plugins, world, PluginIntegrationSupport.newServer())

        val missing =
            KbdSpawnPlugin.SPAWNED_NPC_IDS.filterNot {
                world.plugins.npcCombatDefs.containsKey(getRSCM(it))
            }

        assertTrue(missing.isEmpty(), "spawned NPCs missing a combat definition: $missing")
        assertFalse(world.plugins.npcCombatDefs.isEmpty())
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun initRscm() {
            RSCM.init()
        }
    }
}
